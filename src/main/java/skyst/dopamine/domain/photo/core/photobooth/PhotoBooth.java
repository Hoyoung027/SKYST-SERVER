package skyst.dopamine.domain.photo.core.photobooth;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "photo_booth")
@Getter
@Setter
@AllArgsConstructor
@Builder
public class PhotoBooth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;
    private String title;
    private String date;
    private String category;
    private String content;

    public PhotoBooth(){
    }

}
