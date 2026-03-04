package gift.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
 * Handles the Kakao OAuth2 login flow.
 * 1. /login redirects the user to Kakao's authorization page
 * 2. /callback receives the authorization code, exchanges it for an access token,
 *    retrieves user info, auto-registers the member if new, and issues a service JWT
 */
@RestController
@RequestMapping("/api/auth/kakao")
public class KakaoAuthController {
    private final KakaoLoginProperties properties;
    private final KakaoAuthService kakaoAuthService;

    public KakaoAuthController(KakaoLoginProperties properties, KakaoAuthService kakaoAuthService) {
        this.properties = properties;
        this.kakaoAuthService = kakaoAuthService;
    }

    @GetMapping("/login")
    public ResponseEntity<Void> login() {
        String kakaoAuthUrl = kakaoAuthService.buildAuthorizationUrl(
            properties.clientId(),
            properties.redirectUri()
        );

        return ResponseEntity.status(HttpStatus.FOUND)
            .header(HttpHeaders.LOCATION, kakaoAuthUrl)
            .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<TokenResponse> callback(@RequestParam("code") String code) {
        TokenResponse token = kakaoAuthService.processCallback(code);
        return ResponseEntity.ok(token);
    }
}
