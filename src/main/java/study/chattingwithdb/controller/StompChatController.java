package study.chattingwithdb.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import study.chattingwithdb.domain.entity.ChatMessage;
import study.chattingwithdb.service.ChatService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StompChatController {

    private final ChatService chatService;
    //private final SimpMessagingTemplate template;

    @MessageMapping(value = "/chat/enter")
    public void enter(ChatMessage chatMessage) {
        log.info("{}번 유저가 채팅방에 입장했습니다", chatMessage.getWriterId());
        // 유저 입장 시 메세지 읽음 처리 진행

    }

    @MessageMapping(value = "/chat/message")
    public void send(ChatMessage chatMessage) {
        chatService.sendChatMessage(chatMessage);
    }

    @MessageMapping(value = "/chat/read")
    public void read(ChatMessage chatMessage) {
        log.info("읽");
        //chatService.readChatMessage(chatMessage);
    }

    @MessageMapping(value = "/chat/disconnect")
    public void disconnect(ChatMessage chatMessage) {
        log.info("나감");
        //chatService.readChatMessage(chatMessage);
    }
}
