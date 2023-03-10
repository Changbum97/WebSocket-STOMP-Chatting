package study.chattingwithdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.chattingwithdb.domain.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);
}
