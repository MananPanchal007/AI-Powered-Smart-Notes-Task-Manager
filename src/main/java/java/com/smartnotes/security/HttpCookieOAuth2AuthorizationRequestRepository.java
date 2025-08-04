package java.com.smartnotes.security;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Optional;

import static com.smartnotes.security.CookieUtils.getCookie;
import static com.smartnotes.security.CookieUtils.deleteCookie;

@Component
public class HttpCookieOAuth2AuthorizationRequestRepository {

    public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    public static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";
    private static final int cookieExpireSeconds = 180;

    public Mono<OAuth2AuthorizationRequest> loadAuthorizationRequest(ServerWebExchange exchange) {
        return getCookie(exchange, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                .map(cookie -> {
                    String value = new String(Base64.getUrlDecoder().decode(cookie.getValue()));
                    return OAuth2AuthorizationRequest.fromString(value);
                })
                .orElse(Mono.empty());
    }

    public Mono<Void> saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, ServerWebExchange exchange) {
        if (authorizationRequest == null) {
            return deleteCookies(exchange);
        }

        String value = OAuth2AuthorizationRequest.write(authorizationRequest);
        String encodedValue = Base64.getUrlEncoder().encodeToString(value.getBytes());
        
        return CookieUtils.addCookie(exchange, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, encodedValue, cookieExpireSeconds)
                .then(Mono.defer(() -> {
                    String redirectUriAfterLogin = exchange.getRequest().getQueryParams().getFirst("redirect_uri");
                    if (StringUtils.isNotBlank(redirectUriAfterLogin)) {
                        return CookieUtils.addCookie(exchange, REDIRECT_URI_PARAM_COOKIE_NAME, redirectUriAfterLogin, cookieExpireSeconds);
                    }
                    return Mono.empty();
                }));
    }

    public Mono<OAuth2AuthorizationRequest> removeAuthorizationRequest(ServerWebExchange exchange) {
        return loadAuthorizationRequest(exchange)
                .flatMap(request -> deleteCookies(exchange).thenReturn(request));
    }

    public Mono<String> getRedirectUri(ServerWebExchange exchange) {
        return getCookie(exchange, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(cookie -> cookie.getValue())
                .orElse(Mono.just("/"));
    }

    public Mono<Void> deleteCookies(ServerWebExchange exchange) {
        return CookieUtils.deleteCookie(exchange, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                .then(CookieUtils.deleteCookie(exchange, REDIRECT_URI_PARAM_COOKIE_NAME));
    }
}
