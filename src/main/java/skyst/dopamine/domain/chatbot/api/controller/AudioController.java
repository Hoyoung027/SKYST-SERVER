package skyst.dopamine.domain.chatbot.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import skyst.dopamine.domain.chatbot.api.service.AudioService;
import skyst.dopamine.domain.chatbot.core.Transcript;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
public class AudioController {

    private final AudioService audioService;
    private final S3Client s3Client;
    private final String bucket;

    public AudioController(AudioService audioService, S3Client s3Client,
                           @Value("${aws.s3.audio-bucket}") String bucket) {
        this.audioService = audioService;
        this.s3Client = s3Client;
        this.bucket = bucket;
    }

    @PostMapping( path ="/upload-audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAudio(@RequestParam("file") MultipartFile file) {
        try {
            // 1) S3에 저장할 고유 키 생성
            String key = "audio/" +
                    UUID.randomUUID() + "_" +
                    URLEncoder.encode(file.getOriginalFilename(), StandardCharsets.UTF_8);

            // 2) PutObject 요청
            PutObjectRequest por = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(por,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            try {
                Transcript saved = audioService.transcribeFromS3(key);
                return ResponseEntity.ok(saved);
            } catch (Exception e) {
                return ResponseEntity.status(500).build();
            }

        } catch (IOException e) {
            return ResponseEntity
                    .status(500)
                    .body("파일 처리 실패: " + e.getMessage());
        }
    }

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
