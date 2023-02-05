package study.chattingwithdb.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import study.chattingwithdb.domain.entity.ChatRoom;
import study.chattingwithdb.domain.entity.User;
import study.chattingwithdb.repository.ChatMessageRepository;
import study.chattingwithdb.repository.ChatRoomRepository;
import study.chattingwithdb.repository.UserRepository;
import study.chattingwithdb.service.ChatService;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private HashMap<String, Long> map = new HashMap<>();

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        log.info("============Message Pre Send===========");

        // accessor.getCommand => CONNECT, SUBSCRIBE, SEND, DISCONNECT
        log.info("Command : {}", accessor.getCommand());

        if(accessor.getCommand() == StompCommand.SUBSCRIBE) {
            map.put(accessor.getSessionId(), Long.valueOf(accessor.getDestination().split("room")[1]));
        } else if(accessor.getCommand() == StompCommand.DISCONNECT) {
            Long roomId = map.get(accessor.getSessionId());
            User user = userRepository.findByUserName(accessor.getUser().getName()).get();
            ChatRoom chatRoom = chatRoomRepository.findById(roomId).get();
            if(user.getId() == chatRoom.getId()) {
                chatRoom.setUser1BeforeDisconnection(LocalDateTime.now());
            } else {
                chatRoom.setUser2BeforeDisconnection(LocalDateTime.now());
            }
            chatRoomRepository.save(chatRoom);
            map.remove(accessor.getSessionId());
        }

        // message 추출
        byte[] bytes = (byte[]) message.getPayload();
        log.info("string: {}", new String(bytes, StandardCharsets.UTF_8));
        log.info("=======================================");

        return message;
    }
}
