package com.now.naaga.auth.repository;

import com.now.naaga.auth.domain.AuthTokens;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository  extends JpaRepository<AuthTokens, Long> {

}
