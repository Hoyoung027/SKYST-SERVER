package skyst.dopamine.domain.chatbot.core;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "transcripts")
@RequiredArgsConstructor
@Getter
@Setter
public class Transcript {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String audioKey;

    @Lob
    private String transcriptText;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}
