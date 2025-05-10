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
}
