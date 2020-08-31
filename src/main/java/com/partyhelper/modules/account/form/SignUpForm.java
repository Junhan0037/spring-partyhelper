package com.partyhelper.modules.account.form;

import com.partyhelper.modules.account.Role;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class SignUpForm {

    @NotBlank
    @Length(min = 3, max = 20)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{3,20}$")
    private String nickname;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Length(min = 8, max = 50)
    private String password;

    @NotBlank
    @Length(min = 8, max = 50)
    private String passwordConfirm;

    private Role role;

}
