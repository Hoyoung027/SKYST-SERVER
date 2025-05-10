package skyst.dopamine.domain.photo.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import skyst.dopamine.domain.photo.api.service.PhotoBoothService;
import skyst.dopamine.domain.photo.core.photobooth.PhotoBooth;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/showing")
@RequiredArgsConstructor
@Slf4j
public class PhotoBoothController {

    private final PhotoBoothService photoBoothService;

    @PostMapping(
            path = "/new",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<PhotoBooth> uploadPhotoBooth(
            @RequestPart("image") MultipartFile image,
            @RequestPart("title") String title,
            @RequestPart("category") String category,
            @RequestPart("content") String content,
            @RequestPart("date") String date
    ) {
        try {
            PhotoBooth saved = photoBoothService.createPhotoBooth(
                    image, title, category, content, date
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            log.error("📸 Photo upload failed", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping
    public List<PhotoBooth> getPhotosByCategory(@RequestParam String category) {
        return photoBoothService.findByCategory(category);
    }

    @GetMapping("/{photoId}")
    public PhotoBooth getPhotoDetail(@PathVariable Long photoId) {
        return photoBoothService.getById(photoId);
    }
}
