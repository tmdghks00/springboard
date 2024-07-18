package com.osh.myboard.web.form;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter @Setter
public class BoardForm {

    @NotNull
    private String writer;

    private String title;

    private String content;

    private MultipartFile attachFile; //@ModelAttribute에서 사용가능

    private LocalDateTime createdDateTime;
}