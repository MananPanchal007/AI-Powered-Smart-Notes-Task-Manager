package java.com.smartnotes.security;

import com.smartnotes.model.User;
import com.smartnotes.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserService userService;

    public Mono<User> getCurrentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .flatMap(principal -> {
                    if (principal instanceof String) {
                        return userService.findByEmail((String) principal);
                    } else if (principal instanceof org.springframework.security.core.userdetails.User) {
                        return userService.findByEmail(((org.springframework.security.core.userdetails.User) principal).getUsername());
                    }
                    return Mono.empty();
                });
    }

    public Mono<String> getCurrentUserLogin() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
                        return ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
                    } else if (authentication.getPrincipal() instanceof String) {
                        return (String) authentication.getPrincipal();
                    }
                    return null;
                });
    }
}
