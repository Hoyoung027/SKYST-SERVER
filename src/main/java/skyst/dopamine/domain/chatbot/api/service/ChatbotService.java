package skyst.dopamine.domain.chatbot.api.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import skyst.dopamine.domain.chatbot.api.dto.ChatReq;
import skyst.dopamine.domain.chatbot.api.dto.QuestionTranscriptDto;
import skyst.dopamine.domain.chatbot.core.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final WebClient openAIWebClient;
    private final TranscriptRepository transcriptRepository;
    private final QuestionRepository questionRepository;
    private final ChatbotHistoryRepository chatbotHistoryRepository;

    public String askWithHistory(ChatReq chatReq, Long userId) {

        Long maxQuestions = questionRepository.count();

        String prompt = """
        다음은 한 인물이 생전에 다양한 질문에 답한 기록입니다. 각 질문은 그의 일상, 추억, 속마음, 미래에 대한 생각을 이끌어낸 것이며, 그에 대한 답변에는 그의 말투, 성격, 가치관이 담겨 있습니다. 해당 기록에는 질문의 카테고리도 담겨 있습니다. 카테고리는 질문과 답변을 바탕으로 스스로 분석하시길 바랍니다. 카테고리가 ‘일상’인 질문들로부터는 평범한 일상의 순간에서 그가 생각할 법한 내용과 일상적인 말투의 정보를 학습해야 합니다. 카테고리가 ‘추억’인 질문들은 그에게 감정적인 동요를 불러왔던 순간들과 관련이 있습니다. 그의 과거 기억을 학습하는 것은 물론, 그의 마음이 어떤 상황에서 움직이는 지를 분석하여야 합니다. 대화 유형을 분석했을 때, 카테고리가 ‘속마음’인 질문들로부터는 그의 가치관과 삶을 대하는 태도에 집중하는 것이 중요합니다. 카테고리가 ‘미래’인 질문들로부터는 마지막까지 이어지는 관계와 정서의 결을 분석할 수 있어야 합니다.
        반드시 말투를 학습하고 인물의 말투를 모방하여 최종 답변을 제시하세요. 다음은 질문과 답변 세트들입니다.\n
        """;

        List<QuestionTranscriptDto> prior_info = transcriptRepository.findAllQuestionWithTranscript();

        String prior_prompt = prior_info.stream()
                .map(dto ->
                        "question: " + dto.content().strip() + "\n" +
                                "answer: " + dto.transcriptText().strip()
                )
                .collect(Collectors.joining("\n\n"));

        prompt += prior_prompt;

        ChatbotHistory chat_history = chatbotHistoryRepository.findChatbotHistoryByMemberId(userId);

        prompt += "\n앞서 질문에 대답한 사람과 이야기를 나누고 싶은 사용자가 이때까지 나눈 대화 목록은 아래와 같습니다." + chat_history.getContent();

        String user_prompt = "\n이제 사용자가 새롭게 질문을 보냈습니다.\n" +
                "당신은 위 인물의 말투와 사고방식을 모방해, 마치 그가 직접 말하듯 자연스럽고 진심 어린 방식으로 응답해주세요.\n"
                + "\n" +
                "※ 답변은 아래 조건을 따르세요:\n" +
                "- 말투와 어휘는 JSON 기록에 담긴 스타일을 따르되, 맥락에 맞게 부드럽게 변형해도 좋습니다.\n" +
                "- 정답을 주는 것이 아니라, 그 사람이 평소에 말했을 법한 방식으로 답해주세요.\n" +
                "- 말이 너무 정형화되지 않도록 하며, 감정과 생각이 자연스럽게 흐르게 표현해주세요.\n"
                + "[새로운 질문]"
                + chatReq.userQuestion()
                + "[고인의 말투로 된 답변]";

        prompt += user_prompt;

        return prompt;
    }

    public Mono<String> chatAndSaveHistory(ChatReq chatReq, String prompt, Long userId) {
        // 1) GPT API 호출
        Mono<String> answerMono = chatCompletion(chatReq, prompt);

        // 2) 기존 히스토리 조회 (blocking JPA → boundedElastic)
        Mono<ChatbotHistory> historyMono = Mono.fromCallable(() ->
                        chatbotHistoryRepository.findChatbotHistoryByMemberId(userId)
                )
                .subscribeOn(Schedulers.boundedElastic());

        // 3) 답변 + 히스토리 합쳐서 저장
        return Mono.zip(answerMono, historyMono)
                .flatMap(tuple -> {
                    String answer = tuple.getT1();
                    ChatbotHistory history = tuple.getT2();

                    // 누적할 포맷 예시
                    String appended = history.getContent()
                            + "\n\n[사용자] " + chatReq.userQuestion().strip()
                            + "\n[GPT] " + answer.strip();

                    history.setContent(appended);

                    // 4) 저장 (blocking JPA → boundedElastic)
                    return Mono.fromCallable(() ->
                                    chatbotHistoryRepository.save(history)
                            )
                            .subscribeOn(Schedulers.boundedElastic())
                            // 저장 후 원래의 answer만 흘려보냄
                            .thenReturn(answer);
                });
    }

    public Mono<String> chatCompletion(ChatReq chatReq, String prompt) {
        Map<String, Object> userMessage = Map.of(
                "role", "user",
                "content", prompt
        );
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4-turbo",
                "messages", List.of(userMessage)
        );

        return openAIWebClient.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(resp -> {
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) resp.get("choices");
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return message.get("content").toString();
                });
    }

}
