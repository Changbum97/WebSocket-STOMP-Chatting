package study.chattingwithdb.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import study.chattingwithdb.domain.entity.ChatMessage;
import study.chattingwithdb.domain.entity.ChatRoom;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {

    private Long id;
    private LocalDateTime createdAt;
    private String message;
    private Long writerId;
    private Boolean readCheck;   // 상대가 읽었는지 체크
    private Long roomId;
    private Long targetMessageId;
    private String messageType; // ENTER, TALK, READ, DISCONNECTION

    public ChatMessage toEntity(ChatRoom chatRoom) {
        return ChatMessage.builder()
                .message(message)
                .writerId(writerId)
                .readCheck(false)
                .chatRoom(chatRoom)
                .build();
    }
}
