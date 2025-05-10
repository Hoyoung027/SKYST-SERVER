package skyst.dopamine.domain.chatbot.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import skyst.dopamine.domain.chatbot.api.service.AudioService;
import skyst.dopamine.domain.chatbot.core.Transcript;

@RestController
@RequiredArgsConstructor
public class AudioController {

    private final AudioService audioService;


    /**
     * 이미 S3에 있는 파일을 transcribe 합니다.
     * @param key S3 객체 키 (e.g. uploads/xxxx.wav)
     */
    @PostMapping("/transcribe")
    public ResponseEntity<Transcript> transcribeExisting(
            @RequestParam("key") String s3Key) {
        try {
            Transcript saved = audioService.transcribeFromS3(s3Key);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

}
