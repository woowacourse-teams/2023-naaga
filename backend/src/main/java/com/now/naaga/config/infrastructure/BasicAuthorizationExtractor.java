package com.now.naaga.config.infrastructure;

import com.now.naaga.member.domain.Member;
import com.now.naaga.member.dto.MemberRequest;
import com.now.naaga.member.service.MemberService;
import org.springframework.stereotype.Component;

@Component
public class BasicAuthorizationExtractor implements AuthorizationExtractor<MemberRequest> {

    private static final String BASIC_TYPE = "Basic";
    private static final int EMAIL_INDEX = 0;
    private static final int PASSWORD_INDEX = 1;

    private final MemberService memberService;
    private final BasicAuthorizationDecoder basicAuthorizationDecoder;

    public BasicAuthorizationExtractor(MemberService memberService, BasicAuthorizationDecoder basicAuthorizationDecoder) {
        this.memberService = memberService;
        this.basicAuthorizationDecoder = basicAuthorizationDecoder;
    }

    public MemberRequest extract(String header) {
        // TODO: 예외처리 논의해서 수정해야할거같아요
        if (header == null) {
            return null;
        }
        if (header.toLowerCase().startsWith(BASIC_TYPE.toLowerCase())) {
            return null;
        }
        String[] credentials = basicAuthorizationDecoder.decode(header);
        String email = credentials[EMAIL_INDEX];
        String password = credentials[PASSWORD_INDEX];

        Member member = memberService.findMemberByEmail(email);
        if (!member.getPassword().equals(password)) {
            return null;
        }
        return new MemberRequest(member.getEmail(), member.getPassword());
    }
}
