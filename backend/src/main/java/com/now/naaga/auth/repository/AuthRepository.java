package com.now.naaga.auth.repository;

import com.now.naaga.auth.domain.AuthTokens;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthRepository  extends JpaRepository<AuthTokens, Long> {

    List<AuthTokens> findByRefreshToken(final String refreshToken);
}
