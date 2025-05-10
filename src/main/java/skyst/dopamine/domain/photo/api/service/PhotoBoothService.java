package skyst.dopamine.domain.photo.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import skyst.dopamine.domain.photo.core.photobooth.PhotoBooth;
import skyst.dopamine.domain.photo.core.repository.PhotoBoothRepository;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PhotoBoothService {

    private final S3Client s3Client;
    private final PhotoBoothRepository photoBoothRepository;

    @Value("${aws.s3.member-bucket}")
    private String bucket;

    @Value("${aws.region}")
    private String region;

    public PhotoBooth createPhotoBooth(MultipartFile multipartFile,
                                       String title,
                                       String category,
                                       String content,
                                       String date) throws IOException {
        // 1) MultipartFile → 임시 파일
        Path tmpFile = Files.createTempFile("upload-", multipartFile.getOriginalFilename());
        multipartFile.transferTo(tmpFile.toFile());

        try {
            // 2) S3 업로드
            String key = "photo-booth/" + UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();
            PutObjectRequest req = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            s3Client.putObject(req, tmpFile);

            // 3) URL 생성
            String url = String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, key);

            // 4) 엔티티 저장
            PhotoBooth entity = new PhotoBooth();
            entity.setImageUrl(url);
            entity.setTitle(title);
            entity.setCategory(category);
            entity.setContent(content);
            entity.setDate(date);
            return photoBoothRepository.save(entity);
        } finally {
            // 5) 임시 파일 정리
            Files.deleteIfExists(tmpFile);
        }
    }

    public List<PhotoBooth> findByCategory(String category) {
        return photoBoothRepository.findByCategory(category);
    }

    public PhotoBooth getById(Long id) {
        return photoBoothRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 사진이 존재하지 않습니다. id = " + id));
    }
}
