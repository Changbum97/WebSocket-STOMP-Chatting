package study.chattingwithdb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import study.chattingwithdb.domain.dto.CreateChatRoomRequest;
import study.chattingwithdb.domain.entity.ChatMessage;
import study.chattingwithdb.domain.entity.ChatRoom;
import study.chattingwithdb.domain.entity.User;
import study.chattingwithdb.repository.ChatMessageRepository;
import study.chattingwithdb.repository.ChatRoomRepository;
import study.chattingwithdb.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    // 특정 Broker로 메세지 전달
    private final SimpMessagingTemplate template;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public void createChatRoom(CreateChatRoomRequest request) {

        // 작은 Id가 user1Id, 큰 Id가 user2Id
        Long user1Id = request.getUser1Id();
        Long user2Id = request.getUser2Id();
        if (user1Id > user2Id) {
            Long temp = user1Id;
            user1Id = user2Id;
            user2Id = temp;
        }

        ChatRoom chatRoom = ChatRoom
                .builder()
                .user1Id(user1Id)
                .user2Id(user2Id)
                .build();

        chatRoomRepository.save(chatRoom);
    }

    public List<ChatRoom> findMyRooms(String userName) {
        User user = userRepository.findByUserName(userName).get();
        return chatRoomRepository.findByUser1IdOrUser2Id(user.getId(), user.getId());
    }

    public ChatRoom findRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId).get();
    }

    @Transactional
    public void sendChatMessage(ChatMessage chatMessage) {
        chatMessage.setReadCheck(false);
        chatMessage.setChatRoom(chatRoomRepository.findById(chatMessage.getRoomId()).get());
        chatMessageRepository.save(chatMessage);
        template.convertAndSend("/sub/chat/room" + chatMessage.getRoomId(), chatMessage);
    }

    public List<ChatMessage> findAllMessages(Long chatRoomId) {
        return chatMessageRepository.findByChatRoomId(chatRoomId);
    }

}
