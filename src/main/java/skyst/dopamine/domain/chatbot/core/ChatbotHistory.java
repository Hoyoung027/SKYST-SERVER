package skyst.dopamine.domain.chatbot.core;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chatbot_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatbotHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // member_id 컬럼 매핑
    @Column(name = "member_id")
    private Long memberId;

    // content 컬럼 매핑
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

}
