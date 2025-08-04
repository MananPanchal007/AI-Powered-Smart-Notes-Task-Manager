package java.com.smartnotes.security;

import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.util.SerializationUtils;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

public class CookieUtils {

    public static Optional<String> getCookie(ServerWebExchange exchange, String name) {
        return Optional.ofNullable(exchange.getRequest())
                .map(req -> req.getCookies())
                .map(cookies -> cookies.getFirst(name))
                .map(cookie -> cookie.getValue());
    }

    public static Mono<Void> addCookie(ServerWebExchange exchange, String name, String value, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(exchange.getRequest().getSslInfo() != null)
                .path("/")
                .maxAge(maxAge)
                .sameSite("Lax")
                .build();

        exchange.getResponse().addCookie(cookie);
        return Mono.empty();
    }

    public static Mono<Void> deleteCookie(ServerWebExchange exchange, String name) {
        return getCookie(exchange, name)
                .map(cookie -> {
                    ResponseCookie responseCookie = ResponseCookie.from(name, "")
                            .path("/")
                            .maxAge(0)
                            .httpOnly(true)
                            .secure(exchange.getRequest().getSslInfo() != null)
                            .sameSite("Lax")
                            .build();
                    exchange.getResponse().addCookie(responseCookie);
                    return Mono.<Void>empty();
                })
                .orElseGet(() -> Mono.empty());
    }

    public static String serialize(Object object) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(object));
    }

    public static <T> T deserialize(String cookie, Class<T> cls) {
        return cls.cast(SerializationUtils.deserialize(
                Base64.getUrlDecoder().decode(cookie)));
    }
}
