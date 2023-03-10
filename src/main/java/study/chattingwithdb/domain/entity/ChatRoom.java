package study.chattingwithdb.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;;
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

    @OneToMany(mappedBy = "chatRoom")
    @JsonIgnore
    private List<ChatMessage> chatMessages = new ArrayList<>();
}
