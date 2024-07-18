package com.osh.myboard.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Builder
@Getter @Setter
public class Comment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne
    private Board board;

    @ManyToOne
    private Member member;

    public Comment() {
    }

    public Comment(Long id, String content, Board board, Member member) {
        this.id = id;
        this.content = content;
        this.board = board;
        this.member = member;
    }
}