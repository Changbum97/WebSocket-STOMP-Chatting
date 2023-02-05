package study.chattingwithdb.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoom {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long user1Id;
    private Long user2Id;
    private LocalDateTime user1BeforeDisconnection;
    private LocalDateTime user2BeforeDisconnection;

    @OneToMany(mappedBy = "chatRoom")
    @JsonIgnore
    private List<ChatMessage> chatMessages = new ArrayList<>();
}
