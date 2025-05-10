package skyst.dopamine.domain.chatbot.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import skyst.dopamine.domain.chatbot.api.dto.ChatReq;
import skyst.dopamine.domain.chatbot.api.dto.QuestionTranscriptDto;
import skyst.dopamine.domain.chatbot.core.QuestionRepository;
import skyst.dopamine.domain.chatbot.core.Transcript;
import skyst.dopamine.domain.chatbot.core.TranscriptRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final WebClient openAIWebClient;
    private final TranscriptRepository transcriptRepository;
    private final QuestionRepository questionRepository;

    public String askWithHistory(ChatReq chatReq) {

        Long maxQuestions = questionRepository.count();

        String prompt = """
        당신은 지금부터 한 인물의 말투와 사고방식을 모방해야 합니다.
        아래는 그 인물이 과거에 여러 질문에 답한 기록입니다. 각 기록에는 질문과 답변이 포함되어 있습니다.
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

        String user_prompt = "\n이제 앞서 제시한 인물과 대화를 나누고 싶은 사용자가 새로운 질문을 했습니다.\n 질문은 다음과 같습니다."
                + chatReq.userQuestion()
                + "위 JSON의 말투와 사고방식을 참고하여, 마치 그 인물이 직접 대답하는 것처럼 자연스럽고 일관된 말투로 아래에 답해주세요."
                + "\n[답변]";
        prompt += user_prompt;

        System.out.println(prompt);

        return prompt;
    }

    public Mono<String> chatCompletion(String prompt) {
        Map<String, Object> userMessage = Map.of(
                "role", "user",
                "content", prompt
        );

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo",
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
