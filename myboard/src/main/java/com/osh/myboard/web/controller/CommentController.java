package com.osh.myboard.web.controller;

import com.osh.myboard.web.form.CommentForm;
import com.osh.myboard.domain.Board;
import com.osh.myboard.domain.Comment;
import com.osh.myboard.domain.Member;
import com.osh.myboard.service.BoardService;
import com.osh.myboard.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.osh.myboard.web.SessionConst.LOGIN_MEMBER;
import static com.osh.myboard.web.SessionConst.LOGIN_MEMBER;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CommentController {

    private final BoardService boardService;
    private final CommentService commentService;

    /**
     * 댓글 작성 처리
     */
    @PostMapping("/boards/{id}/comment") //boardId
    public String addComment(@PathVariable int id, @ModelAttribute("commentForm") CommentForm form,
                             HttpServletRequest request, RedirectAttributes redirectAttributes) {

        //id로 board찾아오기
        Board board = boardService.detail(id);

        HttpSession session = request.getSession(false);
        Member loginMember = (Member) session.getAttribute(LOGIN_MEMBER);


        Comment comment = Comment.builder()
                .content(form.getContent())
                .member(loginMember)
                .build();

        //comment 저장
        commentService.writeComment(comment, board.getId(), loginMember.getLoginId());

        redirectAttributes.addAttribute("boardId", id);

        return "redirect:/boards/{boardId}";
    }

    /**
     * 댓글 삭제
     */
    @GetMapping("/boards/{id}/comment/{commentId}/delete")
    public String commentDelete(@PathVariable int id, @PathVariable Long commentId, RedirectAttributes redirectAttributes) {

        log.info("댓글 삭제 메서드 실행");
        commentService.commentDelete(commentId);
        log.info("댓글 삭제 메서드 성공");
        redirectAttributes.addAttribute("boardId", id);

        return "redirect:/boards/{boardId}";
    }


}