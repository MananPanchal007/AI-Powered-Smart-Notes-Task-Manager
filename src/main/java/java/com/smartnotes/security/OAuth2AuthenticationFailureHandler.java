package java.com.smartnotes.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class OAuth2AuthenticationFailureHandler implements ServerAuthenticationFailureHandler {

    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    public OAuth2AuthenticationFailureHandler(HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository) {
        this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
    }

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        ServerWebExchange exchange = webFilterExchange.getExchange();
        
        return httpCookieOAuth2AuthorizationRequestRepository.getRedirectUri(exchange)
                .map(redirectUri -> {
                    String errorUrl = UriComponentsBuilder.fromUriString(redirectUri)
                            .queryParam("error", exception.getLocalizedMessage())
                            .build().toUriString();
                    return redirect(exchange, errorUrl);
                })
                .orElseGet(() -> {
                    String errorUrl = "/?error=" + exception.getLocalizedMessage();
                    return redirect(exchange, errorUrl);
                });
    }

    private Mono<Void> redirect(ServerWebExchange exchange, String url) {
        return httpCookieOAuth2AuthorizationRequestRepository.deleteCookies(exchange)
                .then(Mono.defer(() -> {
                    exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.SEE_OTHER);
                    exchange.getResponse().getHeaders().setLocation(URI.create(url));
                    return exchange.getResponse().setComplete();
                }));
    }
}
