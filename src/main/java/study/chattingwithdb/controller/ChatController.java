package study.chattingwithdb.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import study.chattingwithdb.domain.dto.CreateChatRoomRequest;
import study.chattingwithdb.domain.entity.ChatMessage;
import study.chattingwithdb.domain.entity.User;
import study.chattingwithdb.repository.UserRepository;
import study.chattingwithdb.service.ChatService;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;

    // 채팅방 리스트
    @GetMapping("/rooms")
    public String getAllChatRooms(Model model, Principal principal) {
        log.info(principal.getName());
        model.addAttribute("rooms", chatService.findMyRooms(principal.getName()));
        return "chatList";
    }

    // 채팅방 개설
    @PostMapping("/room")
    @ResponseBody
    public String createChatRoom(@RequestBody CreateChatRoomRequest request) {
        chatService.createChatRoom(request);
        return "채팅방 생성 완료";
    }

    // 채팅방 조회
    @GetMapping("/room/{roomId}")
    public String getChatRoom(@PathVariable Long roomId, Model model, Principal principal,
                              @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable) {
        User user = userRepository.findByUserName(principal.getName()).get();
        model.addAttribute("user", user);
        model.addAttribute("room", chatService.findRoomById(roomId));
        Page<ChatMessage> chatMessages = chatService.findMessages(roomId, pageable);
        model.addAttribute("latestMessages", chatMessages);
        model.addAttribute("noMoreMessages", chatMessages.getTotalElements() <= 10 ? true : false);
        return "chatRoom";
    }

    // 채팅 메세지 더 가져오기
    @GetMapping("/more/{roomId}")
    @ResponseBody
    public Page<ChatMessage> getMoreMessages(@PathVariable Long roomId,
                                             @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable) {
        return chatService.findMessages(roomId, pageable);
    }
}