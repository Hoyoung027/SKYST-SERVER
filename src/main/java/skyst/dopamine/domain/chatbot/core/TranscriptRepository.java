package skyst.dopamine.domain.chatbot.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import skyst.dopamine.domain.member.core.Member;

@Repository
public interface TranscriptRepository extends JpaRepository<Transcript, Long>{

}
