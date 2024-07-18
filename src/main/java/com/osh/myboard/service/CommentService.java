package com.osh.myboard.service;

import com.osh.myboard.repository.MemberRepository;
import com.osh.myboard.domain.Board;
import com.osh.myboard.domain.Comment;
import com.osh.myboard.domain.Member;
import com.osh.myboard.repository.BoardRepository;
import com.osh.myboard.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    /**
     * 댓글 작성
     */
    @Transactional
    public Long writeComment(Comment comment, int boardId, String loginId) {

        //회원 찾아오기
        Member member = memberRepository.findByLoginId(loginId);
        //게시물 찾아오기
        Optional<Board> boardOptional = boardRepository.findById(boardId);
        Board board = boardOptional.orElseThrow(() -> new RuntimeException("Board not found"));

        Comment result = Comment.builder()
                .content(comment.getContent())
                .board(board)
                .member(member)
                .build();

        commentRepository.save(result);
        return result.getId();
    }

    /**
     * board id로 게시물 찾아서 해당 댓글 목록 가져오기
     */
    public List<Comment> commentList(int boardId) {

        Optional<Board> boardOptional = boardRepository.findById(boardId);
        Board board = boardOptional.orElseThrow(() -> new RuntimeException("Board not found"));

        //board로 댓글 목록 가져오기
        List<Comment> comments = commentRepository.findByBoard(board);

        return comments;
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public void commentDelete(Long id) {

        commentRepository.deleteById(id);

    }

    /**
     * 댓글 수정
     */
    @Transactional
    public void commentUpdate(Long id, String content) {

        Optional<Comment> comment = commentRepository.findById(id);

        if (comment.isPresent()) {
            Comment findComment = comment.get();
            findComment.setContent(content);
        }
        else {
            log.warn("comment with id {} not found", id);
        }
    }

}