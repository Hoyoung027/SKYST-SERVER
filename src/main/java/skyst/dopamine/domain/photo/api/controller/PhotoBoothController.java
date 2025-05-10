package skyst.dopamine.domain.photo.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import skyst.dopamine.domain.photo.core.photobooth.PhotoBooth;
import skyst.dopamine.domain.photo.api.service.PhotoBoothService;

import java.util.List;

@RestController
@RequestMapping("/api/photo")
@RequiredArgsConstructor
public class PhotoBoothController {

    private final PhotoBoothService photoBoothService;

    @GetMapping
    public List<PhotoBooth> getPhotosByCategory(@RequestParam String category) {
        return photoBoothService.findByCategory(category);
    }
}
