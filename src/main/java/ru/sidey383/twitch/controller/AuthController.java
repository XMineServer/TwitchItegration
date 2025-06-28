package ru.sidey383.twitch.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.sidey383.twitch.config.JwtConfig;
import ru.sidey383.twitch.dto.auth.AuthRequest;
import ru.sidey383.twitch.dto.auth.AuthResponse;
import ru.sidey383.twitch.dto.auth.ChangePasswordRequest;
import ru.sidey383.twitch.dto.auth.RefreshTokenRequest;
import ru.sidey383.twitch.dto.user.UserDTO;
import ru.sidey383.twitch.model.User;
import ru.sidey383.twitch.service.AuthService;
import ru.sidey383.twitch.service.RefreshTokenService;
import ru.sidey383.twitch.service.UserService;
import ru.sidey383.twitch.util.JwtTokenProvider;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final JwtConfig jwtConfig;

    private ResponseCookie createCookie(String accessToken) {
        return ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(jwtConfig.getExpiration())
                .sameSite("Lax")
                .build();
    }

    private ResponseCookie deleteCookies() {
        return ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        Authentication authentication = authService.authenticateUser(request.username(), request.password());
        User user = (User) authentication.getPrincipal();
        String accessToken = tokenProvider.generateToken(authentication);
        String refreshToken = refreshTokenService.createRefreshToken(user);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, createCookie(accessToken).toString())
                .body(new AuthResponse(accessToken, refreshToken, jwtConfig.getExpiration()));
    }

    @PostMapping("/changepassword")
    public ResponseEntity<UserDTO> changepassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal User user
    ) {
        authService.authenticateUser(user.getUsername(), request.oldPassword());
        user = userService.changePassword(user, request.newPassword());
        return ResponseEntity.ok(UserDTO.fromUser(user));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();
        User user = refreshTokenService.validateRefreshToken(refreshToken);

        UserDetails userDetails = authService.loadUserByUsername(user.getUsername());
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String newAccessToken = tokenProvider.generateToken(authentication);
        String newRefreshToken = refreshTokenService.updateRefreshToken(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, createCookie(newAccessToken).toString())
                .body(new AuthResponse(newAccessToken, refreshToken, jwtConfig.getExpiration()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        refreshTokenService.deleteByToken(request.refreshToken());
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteCookies().toString())
                .build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(UserDTO.fromUser(user));
    }
}
