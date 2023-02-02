package study.chattingwithdb.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateChatRoomRequest {
    private Long user1Id;
    private Long user2Id;
}
