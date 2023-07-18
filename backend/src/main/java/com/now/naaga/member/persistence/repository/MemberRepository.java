package com.now.naaga.member.persistence.repository;

import com.now.naaga.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
