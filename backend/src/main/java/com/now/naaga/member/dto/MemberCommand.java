package com.now.naaga.member.dto;

public class MemberCommand {

    private String email;

    private String password;

    private MemberCommand() {
    }

    public MemberCommand(final String email, final String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
