package skyst.dopamine.domain.music.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import skyst.dopamine.domain.music.api.service.SongService;
import skyst.dopamine.domain.music.core.song.Song;

import java.util.List;

@RestController
@RequestMapping("/playlist/{playlistId}")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;

    @PostMapping
    public Song addSong(@PathVariable Long playlistId, @RequestBody Song song) {
        song = Song.builder()
                .playlistId(playlistId)
                .title(song.getTitle())
                .artist(song.getArtist())
                .imageUrl(song.getImageUrl())
                .age(song.getAge())
                .description(song.getDescription())
                .build();
        return songService.addSong(song);
    }

    @GetMapping
    public List<Song> getSongs(@PathVariable Long playlistId) {
        return songService.getSongsByPlaylist(playlistId);
    }

    @DeleteMapping
    public void deleteSongByTitle(@PathVariable Long playlistId, @RequestParam String title) {
        songService.deleteSongByTitle(playlistId, title);
    }
}
