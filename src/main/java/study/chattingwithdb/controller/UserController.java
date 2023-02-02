package study.chattingwithdb.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import study.chattingwithdb.domain.dto.UserDto;
import study.chattingwithdb.repository.UserRepository;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @GetMapping("/join")
    public String joinPage(Model model) {
        model.addAttribute("user", new UserDto());
        return "join";
    }

    @PostMapping("/join")
    public String join(UserDto dto) {
        userRepository.save(dto.toEntity(encoder.encode(dto.getPassword())));
        return "redirect:/users/login";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("user", new UserDto());
        return "login";
    }

}
