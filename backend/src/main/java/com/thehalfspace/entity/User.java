package com.thehalfspace.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role = UserRole.USER;

    @ElementCollection
    @CollectionTable(name = "user_favorite_teams", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "team_id")
    private List<Long> favoriteTeams = new ArrayList<>();

    @Column(name = "prediction_score", nullable = false)
    private int predictionScore = 0;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt;

    public static User ofLocal(String email, String nickname, String passwordHash) {
        User user = new User();
        user.email        = email;
        user.nickname     = nickname;
        user.passwordHash = passwordHash;
        return user;
    }

    public static User ofOAuth(String email, String nickname) {
        User user = new User();
        user.email    = email;
        user.nickname = nickname;
        return user;
    }

    public void updateNickname(String nickname) {
        this.nickname  = nickname;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.isActive  = false;
        this.updatedAt = Instant.now();
    }
}
