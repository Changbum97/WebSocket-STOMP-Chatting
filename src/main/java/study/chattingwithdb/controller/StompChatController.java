package study.chattingwithdb.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import study.chattingwithdb.domain.dto.ChatMessageDto;
import study.chattingwithdb.service.ChatService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StompChatController {

    private final ChatService chatService;

    @MessageMapping(value = "/chat/enter")
    public void enter(ChatMessageDto chatMessage) {
        log.info("{}번 유저가 채팅방에 입장했습니다", chatMessage.getWriterId());
        chatService.sendChatMessage(chatMessage);
    }

    @MessageMapping(value = "/chat/message")
    public void send(ChatMessageDto chatMessage) {
        log.info("chatMessage :{}", chatMessage.getReadCheck());
        chatService.sendChatMessage(chatMessage);
    }

    @MessageMapping(value = "/chat/read")
    public void read(ChatMessageDto chatMessage) {
        chatService.readChatMessage(chatMessage);
        chatService.sendChatMessage(chatMessage);
    }
}
