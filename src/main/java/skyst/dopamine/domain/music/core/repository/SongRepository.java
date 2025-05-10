package skyst.dopamine.domain.music.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import skyst.dopamine.domain.music.core.song.Song;

import java.util.List;
import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findByPlaylistId(Long playlistId);
    Optional<Song> findByPlaylistIdAndTitle(Long playlistId, String title);
}
