package skyst.dopamine.domain.music.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import skyst.dopamine.domain.music.core.playlist.Playlist;
import skyst.dopamine.domain.music.core.repository.PlaylistRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;

    public Playlist create(Playlist playlist) {
        if (Boolean.TRUE.equals(playlist.getRepresentative())) {
            playlistRepository.findByRepresentativeTrue()
                    .ifPresent(existing -> {
                        existing.setRepresentative(false);
                        playlistRepository.save(existing);
                    });
        }
        return playlistRepository.save(playlist);
    }

    public List<Playlist> findAll() {
        return playlistRepository.findAll();
    }

    public void updateRepresentative(Long id) {
        // 기존 대표를 false 처리
        playlistRepository.findByRepresentativeTrue()
                .ifPresent(existing -> {
                    existing.setRepresentative(false);
                    playlistRepository.save(existing);
                });

        // 새로운 대표로 설정
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 플레이리스트가 존재하지 않습니다."));
        playlist.setRepresentative(true);
        playlistRepository.save(playlist);
    }
}
