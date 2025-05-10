package skyst.dopamine.domain.music.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import skyst.dopamine.domain.music.core.repository.SongRepository;
import skyst.dopamine.domain.music.core.song.Song;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;

    public Song addSong(Song song) {
        return songRepository.save(song);
    }

    public List<Song> getSongsByPlaylist(Long playlistId) {
        return songRepository.findByPlaylistId(playlistId);
    }

    public void deleteSongByTitle(Long playlistId, String title) {
        Song song = songRepository.findByPlaylistIdAndTitle(playlistId, title)
                .orElseThrow(() -> new IllegalArgumentException("해당 제목의 곡이 존재하지 않습니다."));
        songRepository.delete(song);
    }
}
