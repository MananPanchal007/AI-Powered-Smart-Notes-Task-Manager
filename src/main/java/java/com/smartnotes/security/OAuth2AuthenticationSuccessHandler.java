package java.com.smartnotes.security;

import com.smartnotes.model.User;
import com.smartnotes.security.jwt.JwtTokenProvider;
import com.smartnotes.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final UserService userService;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerWebExchange exchange = webFilterExchange.getExchange();
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        
        return processOAuth2User(exchange, oauthToken, oauthUser)
                .flatMap(user -> {
                    String token = tokenProvider.generateToken(user.getEmail());
                    String targetUrl = determineTargetUrl(exchange);
                    
                    return httpCookieOAuth2AuthorizationRequestRepository.getRedirectUri(exchange)
                            .map(redirectUri -> {
                                if (redirectUri != null && !redirectUri.isEmpty()) {
                                    targetUrl = redirectUri;
                                }
                                return targetUrl;
                            })
                            .defaultIfEmpty(targetUrl)
                            .flatMap(redirectUrl -> {
                                String finalTargetUrl = UriComponentsBuilder.fromUriString(redirectUrl)
                                        .queryParam("token", token)
                                        .build().toUriString();
                                
                                return httpCookieOAuth2AuthorizationRequestRepository.deleteCookies(exchange)
                                        .then(redirect(exchange, finalTargetUrl));
                            });
                });
    }

    private Mono<User> processOAuth2User(ServerWebExchange exchange, OAuth2AuthenticationToken oauthToken, OAuth2User oauthUser) {
        String registrationId = oauthToken.getAuthorizedClientRegistrationId();
        Map<String, Object> attributes = oauthUser.getAttributes();
        
        // Extract user details based on the OAuth2 provider
        OAuth2UserInfo oauth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, attributes);
        
        return userService.findByEmail(oauth2UserInfo.getEmail())
                .switchIfEmpty(registerNewUser(oauth2UserInfo));
    }

    private Mono<User> registerNewUser(OAuth2UserInfo oauth2UserInfo) {
        User user = User.builder()
                .email(oauth2UserInfo.getEmail())
                .name(oauth2UserInfo.getName())
                .picture(oauth2UserInfo.getImageUrl())
                .emailVerified(true)
                .createdAt(LocalDateTime.now())
                .build();
                
        return userService.registerUser(user);
    }

    private String determineTargetUrl(ServerWebExchange exchange) {
        // Default redirect URL after successful login
        return "/";
    }

    private Mono<Void> redirect(ServerWebExchange exchange, String url) {
        exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.SEE_OTHER);
        exchange.getResponse().getHeaders().setLocation(URI.create(url));
        return exchange.getResponse().setComplete();
    }
}
