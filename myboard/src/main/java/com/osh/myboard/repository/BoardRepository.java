package com.osh.myboard.repository;

import com.osh.myboard.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {
    Page<Board> findAllByOrderByCreatedDateTimeDesc(Pageable pageable);
}