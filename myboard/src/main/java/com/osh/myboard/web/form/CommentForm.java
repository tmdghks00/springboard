package com.osh.myboard.web.form;


import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommentForm {

    private String writer; //댓글 작성자

    private String content; //댓글 내용
}