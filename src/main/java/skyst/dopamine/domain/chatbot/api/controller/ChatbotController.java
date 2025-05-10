package skyst.dopamine.domain.chatbot.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.RestController;
import skyst.dopamine.domain.chatbot.api.dto.ChatReq;
import skyst.dopamine.domain.chatbot.api.service.ChatbotService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/chat/{id}")
    public Mono<ResponseEntity<String>> chat(@RequestBody ChatReq userQuestion,
    @PathVariable Long id) {

        String prompt = chatbotService.askWithHistory(userQuestion, id);

        return chatbotService.chatCompletion(prompt)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(
                        ResponseEntity.status(500).body(e.getMessage())
                ));
    }
}
