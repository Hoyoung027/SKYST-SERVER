package skyst.dopamine.domain.photo.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import skyst.dopamine.domain.photo.core.photobooth.PhotoBooth;
import java.util.Optional;


import java.util.List;

public interface PhotoBoothRepository extends JpaRepository<PhotoBooth, Long> {
    List<PhotoBooth> findByCategory(String category);
    Optional<PhotoBooth> findById(Long id);
}
