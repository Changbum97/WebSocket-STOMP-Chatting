package study.chattingwithdb.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import study.chattingwithdb.domain.entity.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String userName;
    private String password;

    public User toEntity(String encodedPassword) {
        return User.builder()
                .userName(userName)
                .password(encodedPassword)
                .build();
    }
}
