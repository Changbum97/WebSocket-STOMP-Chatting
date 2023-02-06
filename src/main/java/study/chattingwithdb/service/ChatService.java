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
        // 읽었다는 메세지가 아니고 입장 메세지도 아니면 저장
        if(chatMessage.getMessageType().equals("TALK")) {
            chatMessage.setReadCheck(false);
            chatMessage.setChatRoom(chatRoomRepository.findById(chatMessage.getRoomId()).get());
            chatMessageRepository.save(chatMessage);
        } else if(chatMessage.getMessageType().equals("ENTER")) {
            List<ChatMessage> chatMessages = chatMessageRepository.findByWriterIdNotAndReadCheck(chatMessage.getWriterId(), false);
            for (ChatMessage beforeChatMessage :chatMessages) {
                if(beforeChatMessage.getReadCheck() == true) {
                    break;
                } else {
                    beforeChatMessage.setReadCheck(true);
                    chatMessageRepository.save(beforeChatMessage);
                }
            }
        }
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
}
