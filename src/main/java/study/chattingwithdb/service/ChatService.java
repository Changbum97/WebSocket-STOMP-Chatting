package study.chattingwithdb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import java.time.LocalDateTime;
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
                .user1BeforeDisconnection(LocalDateTime.now())
                .user2BeforeDisconnection(LocalDateTime.now())
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

    public Page<ChatMessage> findLatestMessages(Long chatRoomId, Pageable pageable, String userName) {
        User user = userRepository.findByUserName(userName).get();
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).get();
        if(chatRoom.getUser1Id() == user.getId()) {
            return chatMessageRepository.findByChatRoomIdAndCreatedAtBefore(chatRoomId, chatRoom.getUser1BeforeDisconnection(), pageable);
        } else {
            return chatMessageRepository.findByChatRoomIdAndCreatedAtBefore(chatRoomId, chatRoom.getUser2BeforeDisconnection(), pageable);
        }
    }

    public List<ChatMessage> findNotReadMessages(Long chatRoomId, String userName) {
        User user = userRepository.findByUserName(userName).get();
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).get();
        if(chatRoom.getUser1Id() == user.getId()) {
            return chatMessageRepository.findByChatRoomIdAndCreatedAtAfter(chatRoomId, chatRoom.getUser1BeforeDisconnection());
        } else {
            return chatMessageRepository.findByChatRoomIdAndCreatedAtAfter(chatRoomId, chatRoom.getUser2BeforeDisconnection());
        }
    }
    @Transactional
    public void readChatMessage(ChatMessage chatMessage) {
        ChatMessage targetChatMessage = chatMessageRepository.findById(chatMessage.getTargetMessageId()).get();
        targetChatMessage.setReadCheck(true);
        chatMessageRepository.save(targetChatMessage);
    }

    @Transactional
    public void disconnect(User user, Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).get();
        if(user.getId() == chatRoom.getId()) {
            chatRoom.setUser1BeforeDisconnection(LocalDateTime.now());
        } else {
            chatRoom.setUser2BeforeDisconnection(LocalDateTime.now());
        }
    }

}
