package com.ssafy.tournest.api.service.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserSignUpRequest {
    @NotNull
    @Size(max = 64)
    private String userId;

    @NotNull
    @Size(max = 100)
    private String username;

    @NotNull
    @Size(min = 8, max = 128)
    private String password;

    @NotNull
    @Email
    @Size(max = 512)
    private String email;

    private String profileImageUrl;
}
