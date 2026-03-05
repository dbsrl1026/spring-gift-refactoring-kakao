package gift.wish;

import gift.auth.AuthenticationResolver;
import gift.member.Member;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/wishes")
public class WishController {
    private final WishService wishService;
    private final AuthenticationResolver authenticationResolver;

    public WishController(WishService wishService, AuthenticationResolver authenticationResolver) {
        this.wishService = wishService;
        this.authenticationResolver = authenticationResolver;
    }

    @GetMapping
    public ResponseEntity<Page<WishResponse>> getWishes(
        @RequestHeader("Authorization") String authorization,
        Pageable pageable
    ) {
        return authenticationResolver.extractMember(authorization)
            .map(member -> {
                Page<WishResponse> wishes = wishService.getWishes(member.getId(), pageable);
                return ResponseEntity.ok(wishes);
            })
            .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping
    public ResponseEntity<WishResponse> addWish(
        @RequestHeader("Authorization") String authorization,
        @Valid @RequestBody WishRequest request
    ) {
        return authenticationResolver.extractMember(authorization)
            .map(member -> addWishForMember(member.getId(), request))
            .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    private ResponseEntity<WishResponse> addWishForMember(Long memberId, WishRequest request) {
        return wishService.addWish(memberId, request)
            .map(result -> {
                if (result.created()) {
                    return ResponseEntity
                        .created(URI.create("/api/wishes/" + result.response().id()))
                        .body(result.response());
                }
                return ResponseEntity.ok(result.response());
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeWish(
        @RequestHeader("Authorization") String authorization,
        @PathVariable Long id
    ) {
        return authenticationResolver.extractMember(authorization)
            .map(member -> removeWishForMember(member.getId(), id))
            .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    private ResponseEntity<Void> removeWishForMember(Long memberId, Long wishId) {
        return wishService.removeWish(memberId, wishId)
            .map(result -> switch (result) {
                case SUCCESS -> ResponseEntity.noContent().<Void>build();
                case NOT_FOUND -> ResponseEntity.notFound().<Void>build();
                case FORBIDDEN -> ResponseEntity.status(HttpStatus.FORBIDDEN).<Void>build();
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
