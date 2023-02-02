package study.chattingwithdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.chattingwithdb.domain.entity.ChatMessage;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomId(Long chatRoomId);
}
