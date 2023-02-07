package study.chattingwithdb.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import study.chattingwithdb.domain.dto.ChatMessageDto;
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
    public void sendChatMessage(ChatMessageDto chatMessageDto) {
        ChatMessage chatMessage = chatMessageDto.toEntity(chatRoomRepository.findById(chatMessageDto.getRoomId()).get());
        // 읽었다는 메세지가 아니고 입장 메세지도 아니면 저장
        if(chatMessageDto.getMessageType().equals("TALK")) {
            ChatMessage saved = chatMessageRepository.save(chatMessage);
            chatMessageDto.setCreatedAt(saved.getCreatedAt());
            chatMessageDto.setId(saved.getId());
            chatMessageDto.setReadCheck(saved.getReadCheck());
        } else if(chatMessageDto.getMessageType().equals("ENTER")) {
            List<ChatMessage> chatMessages = chatMessageRepository.findByWriterIdNotAndReadCheckAndChatRoomId(chatMessageDto.getWriterId(), false, chatMessageDto.getRoomId());
            for (ChatMessage beforeChatMessage :chatMessages) {
                if(beforeChatMessage.getReadCheck() == true) {
                    break;
                } else {
                    beforeChatMessage.setReadCheck(true);
                    chatMessageRepository.save(beforeChatMessage);
                }
            }
        }
        template.convertAndSend("/sub/chat/room" + chatMessageDto.getRoomId(), chatMessageDto);
    }

    public Page<ChatMessage> findLatestMessages(Long chatRoomId, Long firstMessageId, Pageable pageable) {
        return chatMessageRepository.findByChatRoomIdAndIdLessThan(chatRoomId, firstMessageId, pageable);
    }

    public List<ChatMessage> findNotReadMessages(Long chatRoomId, String userName) {
        User user = userRepository.findByUserName(userName).get();
        return chatMessageRepository.findByWriterIdNotAndReadCheckAndChatRoomId(user.getId(), false, chatRoomId);
    }
    @Transactional
    public void readChatMessage(ChatMessageDto chatMessageDto) {
        ChatMessage targetChatMessage = chatMessageRepository.findById(chatMessageDto.getTargetMessageId()).get();
        targetChatMessage.setReadCheck(true);
        chatMessageRepository.save(targetChatMessage);
    }
}
