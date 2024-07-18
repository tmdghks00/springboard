package com.osh.myboard.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Board {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String writer;

    private String title;

    private String content;

    @OneToOne(cascade = CascadeType.ALL)
    private UploadFile attachFile;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    private LocalDateTime createdDateTime;
}