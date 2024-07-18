package com.osh.myboard.repository;

import com.osh.myboard.domain.Board;
import com.osh.myboard.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByBoard(Board board);
}