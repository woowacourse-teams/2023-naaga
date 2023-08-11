package com.now.naaga.member.fixture;

import com.now.naaga.member.domain.Member;

public class MemberFixture {

    public static final String MEMBER_EMAIL = "kokodak@koko.dak";

    public static Member MEMBER() {
        return new Member(MEMBER_EMAIL);
    }
}
