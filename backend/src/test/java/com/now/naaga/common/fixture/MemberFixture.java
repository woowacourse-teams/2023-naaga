package com.now.naaga.common.fixture;

import com.now.naaga.member.domain.Member;

public class MemberFixture {

    public static final String MEMBER_EMAIL = "member@email.com";

    public static Member MEMBER() {
        return new Member(MEMBER_EMAIL);
    }
}
