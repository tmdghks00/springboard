package com.osh.myboard.web.form;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AddMemberForm {

    @NotBlank(message = "{NotBlank.username}")
    private String username;

    @NotNull(message = "{NotBlank.loginId}")
    @Pattern(regexp = "^[a-z0-9]{2,10}$", message = "{Pattern.loginId}")
    private String loginId;

    @NotNull(message = "{NotBlank.password}")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "{Pattern.password}")
    private String password;

    @Pattern(regexp = "^$|^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])+[.][a-zA-Z]{2,3}$", message = "{Pattern.email}")
    private String email;
}