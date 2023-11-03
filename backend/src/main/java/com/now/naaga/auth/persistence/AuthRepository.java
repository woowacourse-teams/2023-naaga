package com.now.naaga.auth.persistence;

import com.now.naaga.auth.domain.AuthToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository  extends JpaRepository<AuthToken, Long> {

    Optional<AuthToken> findByRefreshToken(final String refreshToken);

    void deleteByMemberId(Long memberId);
}
