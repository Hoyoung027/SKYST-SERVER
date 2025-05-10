package skyst.dopamine.domain.chatbot.core;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(name = "audio_key", nullable = false, length = 255)
    private String audioKey;

    @Column(name = "transcript_text", columnDefinition = "LONGTEXT", nullable = false)
    private String transcriptText;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
