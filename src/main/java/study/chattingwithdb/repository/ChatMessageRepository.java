package study.chattingwithdb.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import study.chattingwithdb.domain.entity.ChatMessage;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByWriterIdNotAndReadCheckAndChatRoomId(Long writerId, Boolean readCheck, Long chatRoomId);
    Page<ChatMessage> findByChatRoomIdAndIdLessThan(Long roomId, Long id, Pageable pageable);

}
