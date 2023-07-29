package com.now.naaga.member.fixture;

import com.now.naaga.member.application.dto.MemberCommand;
import com.now.naaga.member.domain.Member;

public class MemberFixture {

    public static final MemberCommand MEMBER_COMMAND = new MemberCommand("111@woowa.com", "1111");

    public static final Member MEMBER = new Member("111@woowa.com", "1111");
}
