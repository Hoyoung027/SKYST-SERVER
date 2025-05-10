package skyst.dopamine.domain.chatbot.api.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.Collectors;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.protobuf.ByteString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import skyst.dopamine.domain.chatbot.core.Transcript;
import skyst.dopamine.domain.chatbot.core.TranscriptRepository;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

@Service
public class AudioService {
    private static final Logger logger = LoggerFactory.getLogger(AudioService.class);

    private final S3Client s3;
    private final String bucket;
    private final SpeechClient speechClient;
    private final TranscriptRepository transcriptRepository;

    public AudioService(S3Client s3,
                        @Value("${aws.s3.audio-bucket}") String bucket,
                        SpeechClient speechClient,
                        TranscriptRepository transcriptRepository) {
        this.s3 = s3;
        this.bucket = bucket;
        this.speechClient = speechClient;
        this.transcriptRepository = transcriptRepository;
    }

    /**
     * S3에 저장된 오디오 파일을 불러와서
     * 모노(1채널) WAV로 변환 후
     * Google STT 변환 → DB 저장
     */
    public Transcript transcribeFromS3(String key) throws Exception {
        logger.info("Starting transcription for S3 key: {}", key);

        // 1) S3에서 바이트 읽기
        logger.debug("Fetching audio bytes from S3 bucket={}, key={}", bucket, key);
        ResponseBytes<?> audioBytes;
        try {
            audioBytes = s3.getObjectAsBytes(
                    GetObjectRequest.builder().bucket(bucket).key(key).build()
            );
            logger.debug("Fetched {} bytes from S3", audioBytes.asByteArray().length);
        } catch (Exception e) {
            logger.error("Failed to fetch audio from S3", e);
            throw e;
        }

        // 2) 스테레오 파일 모노로 변환
        byte[] monoBytes = convertToMono(audioBytes.asByteArray());
        logger.debug("Converted audio to mono, {} bytes", monoBytes.length);

        // 3) Google STT 호출 설정
        RecognitionConfig config = RecognitionConfig.newBuilder()
                .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                .setSampleRateHertz(44100)
                .setLanguageCode("ko-KR")
                .build();
        logger.debug("RecognitionConfig: encoding={}, sampleRate={}, language={}",
                config.getEncoding(), config.getSampleRateHertz(), config.getLanguageCode());

        RecognitionAudio audio = RecognitionAudio.newBuilder()
                .setContent(ByteString.copyFrom(monoBytes))
                .build();

        // 4) 변환 실행
        RecognizeResponse response;
        try {
            logger.info("Calling Google Speech-to-Text API");
            response = speechClient.recognize(config, audio);
            logger.info("Received {} result(s) from STT API", response.getResultsCount());
        } catch (Exception e) {
            logger.error("Error during STT API call", e);
            throw e;
        }

        // 5) 텍스트 조합
        String transcriptText = response.getResultsList().stream()
                .flatMap(r -> r.getAlternativesList().stream())
                .map(SpeechRecognitionAlternative::getTranscript)
                .collect(Collectors.joining(" "));
        logger.debug("Transcription text: {}", transcriptText);

        // 6) DB 저장
        Transcript entity = new Transcript();
        entity.setAudioKey(key);
        entity.setTranscriptText(transcriptText);
        Transcript saved;
        try {
            logger.debug("Saving transcript to DB");
            saved = transcriptRepository.save(entity);
            logger.info("Saved transcript with id={} to DB", saved.getId());
        } catch (Exception e) {
            logger.error("Failed to save transcript to DB", e);
            throw e;
        }

        return saved;
    }

    /**
     * 바이트 배열로 로드된 WAV 데이터를 모노(WAV PCM)로 변환하여 반환
     */
    private byte[] convertToMono(byte[] stereoBytes) throws IOException, UnsupportedAudioFileException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(stereoBytes)) {
            AudioInputStream sourceStream = AudioSystem.getAudioInputStream(bais);
            AudioFormat sourceFormat = sourceStream.getFormat();
            AudioFormat monoFormat = new AudioFormat(
                    sourceFormat.getEncoding(),
                    sourceFormat.getSampleRate(),
                    sourceFormat.getSampleSizeInBits(),
                    1, // mono 채널
                    (sourceFormat.getFrameSize() / sourceFormat.getChannels()),
                    sourceFormat.getFrameRate(),
                    sourceFormat.isBigEndian()
            );
            AudioInputStream monoStream = AudioSystem.getAudioInputStream(monoFormat, sourceStream);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            AudioSystem.write(monoStream, AudioFileFormat.Type.WAVE, baos);
            return baos.toByteArray();
        }
    }
}