package study.chattingwithdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.chattingwithdb.domain.entity.ChatRoom;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findByUser1IdOrUser2Id(Long user1Id, Long user2Id);
}
