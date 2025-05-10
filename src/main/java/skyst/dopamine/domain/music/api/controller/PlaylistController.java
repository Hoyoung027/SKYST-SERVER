package skyst.dopamine.domain.music.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import skyst.dopamine.domain.music.api.service.PlaylistService;
import skyst.dopamine.domain.music.core.playlist.Playlist;

import java.util.List;

@RestController
@RequestMapping("/playlist")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    @PostMapping
    public Playlist create(@RequestBody Playlist playlist) {
        return playlistService.create(playlist);
    }

    @GetMapping
    public List<Playlist> getAll() {
        return playlistService.findAll();
    }

    @PatchMapping("/{id}/represent")
    public void changeRepresentative(@PathVariable Long id) {
        playlistService.updateRepresentative(id);
    }
}
