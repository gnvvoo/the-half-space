package com.thehalfspace.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
@Entity
@Table(
        name = "social_accounts",
        uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "provider_id"})
)
public class SocialAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OAuthProvider provider;

    @Column(name = "provider_id", nullable = false, length = 255)
    private String providerId;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    public static SocialAccount of(User user, OAuthProvider provider, String providerId) {
        SocialAccount account = new SocialAccount();
        account.user       = user;
        account.provider   = provider;
        account.providerId = providerId;
        return account;
    }
}
