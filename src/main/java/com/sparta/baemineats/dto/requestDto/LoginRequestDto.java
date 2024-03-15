package com.sparta.baemineats.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class LoginRequestDto {

    private String username;

    private String password;

    public LoginRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
