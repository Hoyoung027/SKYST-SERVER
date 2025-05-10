package skyst.dopamine.domain.photo.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import skyst.dopamine.domain.photo.core.photobooth.PhotoBooth;
import skyst.dopamine.domain.photo.core.repository.PhotoBoothRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PhotoBoothService {

    private final PhotoBoothRepository photoBoothRepository;

    public List<PhotoBooth> findByCategory(String category) {
        return photoBoothRepository.findByCategory(category);
    }

    public PhotoBooth getById(Long id) {
        return photoBoothRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 사진이 존재하지 않습니다. id = " + id));
    }
}
