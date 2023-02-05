package study.chattingwithdb.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import study.chattingwithdb.domain.entity.ChatMessage;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Page<ChatMessage> findByChatRoomId(Long chatRoomId, Pageable pageable);
    List<ChatMessage> findByChatRoomIdAndCreatedAtAfter(Long chatRoomId, LocalDateTime createdAt);
    Page<ChatMessage> findByChatRoomIdAndCreatedAtBefore(Long chatRoomId, LocalDateTime createdAt, Pageable pageable);
}
