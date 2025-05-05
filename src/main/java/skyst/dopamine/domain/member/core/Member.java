package skyst.dopamine.domain.member.core;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "member")
@Getter
@Setter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    private Long id;

    @Column(name = "kakao_user_id")
    private Long kakaoUserId;

    @Column(nullable = false, length = 45)
    private String nickname;

    @Column(nullable = false, length = 45, unique = true)
    private String email;


    public Member(Long kakaoUserId, String nickname, String email) {
        this.kakaoUserId = kakaoUserId;
        this.nickname = nickname;
        this.email = email;
    }
}
