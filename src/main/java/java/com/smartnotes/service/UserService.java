package java.com.smartnotes.service;

import com.smartnotes.model.User;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

public interface UserService extends ReactiveUserDetailsService {
    Mono<User> registerUser(User user);
    Mono<User> findByEmail(String email);
    Mono<Boolean> existsByEmail(String email);
    Mono<User> getCurrentUser();
    Mono<User> getCurrentUserOrError();
}
