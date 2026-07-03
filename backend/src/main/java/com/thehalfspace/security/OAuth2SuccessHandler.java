package com.thehalfspace.security;

import com.thehalfspace.entity.OAuthProvider;
import com.thehalfspace.entity.SocialAccount;
import com.thehalfspace.entity.User;
import com.thehalfspace.repository.SocialAccountRepository;
import com.thehalfspace.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final String REFRESH_TOKEN_PREFIX = "refresh:";

    private final UserRepository userRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;

    @Value("${app.oauth2.redirect-uri}")
    private String redirectUri;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauth2User  = oauthToken.getPrincipal();
        OAuthProvider provider = OAuthProvider.valueOf(
                oauthToken.getAuthorizedClientRegistrationId().toUpperCase());
        String providerId = oauth2User.getName();

        User user = socialAccountRepository.findByProviderAndProviderId(provider, providerId)
                .map(SocialAccount::getUser)
                .orElseGet(() -> createUser(oauth2User, provider, providerId));

        String accessToken  = jwtProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId(), user.getEmail(), user.getRole());

        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + user.getId(),
                refreshToken,
                Duration.ofSeconds(jwtProvider.getRefreshTokenExpiry())
        );

        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private User createUser(OAuth2User oauth2User, OAuthProvider provider, String providerId) {
        String email    = extractEmail(provider, oauth2User);
        String nickname = resolveNickname(extractNickname(provider, oauth2User));

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(User.ofOAuth(email, nickname)));

        socialAccountRepository.save(SocialAccount.of(user, provider, providerId));
        return user;
    }

    private String resolveNickname(String base) {
        if (!userRepository.existsByNickname(base)) {
            return base;
        }
        String candidate;
        do {
            candidate = base + "_" + (int) (Math.random() * 10000);
        } while (userRepository.existsByNickname(candidate));
        return candidate;
    }

    private String extractEmail(OAuthProvider provider, OAuth2User oauth2User) {
        return switch (provider) {
            case GOOGLE -> oauth2User.getAttribute("email");
            // TODO: account_email 스코프 임시 비활성화 상태. 복구 시 kakao_account.email로 되돌릴 것
            case KAKAO -> "kakao_" + oauth2User.getName() + "@kakao.local";
        };
    }

    private String extractNickname(OAuthProvider provider, OAuth2User oauth2User) {
        return switch (provider) {
            case GOOGLE -> oauth2User.getAttribute("name");
            case KAKAO -> {
                Map<String, Object> properties = oauth2User.getAttribute("properties");
                yield (String) properties.get("nickname");
            }
        };
    }
}
