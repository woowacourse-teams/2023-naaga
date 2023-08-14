package com.now.naaga.auth.repository;

import com.now.naaga.auth.domain.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthRepository  extends JpaRepository<AuthToken, Long> {

    Optional<AuthToken> findByRefreshToken(final String refreshToken);
}
