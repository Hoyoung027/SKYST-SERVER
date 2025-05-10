package skyst.dopamine.domain.chatbot.core;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import skyst.dopamine.domain.chatbot.api.dto.QuestionTranscriptDto;
import skyst.dopamine.domain.member.core.Member;

import java.util.List;
import java.util.Optional;

@Repository
public interface TranscriptRepository extends JpaRepository<Transcript, Long>{

    // audioKey 로 한 건 조회
    Optional<Transcript> findByAudioKey(String audioKey);

    // questionId 에 연관된 모든 트랜스크립트 조회
    List<Transcript> findByQuestionId(Long questionId);

    @Query("""
        SELECT new skyst.dopamine.domain.chatbot.api.dto.QuestionTranscriptDto(
            q.content,
            t.transcriptText
        )
        FROM Transcript t
        JOIN t.question q
        """)
    List<QuestionTranscriptDto> findAllQuestionWithTranscript();
}
