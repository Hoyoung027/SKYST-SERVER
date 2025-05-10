package skyst.dopamine.domain.chatbot.core;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatbotHistoryRepository extends JpaRepository<ChatbotHistory, Long> {
    ChatbotHistory findChatbotHistoryByMemberId(Long memberId);
}
