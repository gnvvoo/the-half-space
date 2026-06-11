package com.thehalfspace.repository;

import com.thehalfspace.entity.OAuthProvider;
import com.thehalfspace.entity.SocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

    Optional<SocialAccount> findByProviderAndProviderId(OAuthProvider provider, String providerId);
}
