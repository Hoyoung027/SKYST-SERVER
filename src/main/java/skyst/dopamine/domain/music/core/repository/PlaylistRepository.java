package skyst.dopamine.domain.music.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import skyst.dopamine.domain.music.core.playlist.Playlist;

import java.util.Optional;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    Optional<Playlist> findByRepresentativeTrue();
}
