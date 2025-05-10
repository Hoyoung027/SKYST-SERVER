package skyst.dopamine.domain.music.core.song;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "music")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long playlistId;

    private String title;
    private String artist;
    private String imageUrl;
    private String age;
    private String description;
}
