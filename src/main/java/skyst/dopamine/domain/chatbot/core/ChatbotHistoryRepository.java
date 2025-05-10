package skyst.dopamine.domain.chatbot.core;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatbotHistoryRepository extends JpaRepository<ChatbotHistory, Long> {
    ChatbotHistory findChatbotHistoryByMemberId(Long memberId);
    Optional<ChatbotHistory> findTopByMemberIdOrderByIdDesc(Long memberId);
}
