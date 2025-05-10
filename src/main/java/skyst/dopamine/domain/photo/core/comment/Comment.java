package skyst.dopamine.domain.photo.core.comment;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;
    private Long photoboothId;
    private String content;
}
