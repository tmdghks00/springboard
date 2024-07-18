package com.osh.myboard.web.controller;

import com.osh.myboard.web.form.BoardForm;
import com.osh.myboard.web.form.CommentForm;
import com.osh.myboard.domain.Board;
import com.osh.myboard.domain.Comment;
import com.osh.myboard.domain.Member;
import com.osh.myboard.domain.UploadFile;
import com.osh.myboard.file.FileStore;
import com.osh.myboard.service.BoardService;
import com.osh.myboard.service.CommentService;
import com.osh.myboard.service.UploadFileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static com.osh.myboard.web.SessionConst.LOGIN_MEMBER;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService boardService;
    private final FileStore fileStore;
    private final CommentService commentService;
    private final UploadFileService uploadFileService;

    /**
     * 게시글 등록 폼 열기
     */
    @GetMapping("/boards/new")
    public String createPostForm(Model model, HttpServletRequest request) {

        BoardForm boardForm = new BoardForm();

        HttpSession session = request.getSession(false);
        Member loginMember = (Member) session.getAttribute(LOGIN_MEMBER);

        if (loginMember != null) {
            boardForm.setWriter(loginMember.getLoginId()); //session에서 찾아온 회원id를 boardForm에 뿌리기
            log.info("작성자 : {}", loginMember.getLoginId());
        }

        model.addAttribute("boardForm", boardForm);

        return "boards/createBoardForm";
    }
    /**
     * 게시글 등록
     */
    @PostMapping("/boards/new")
    public String registerPost(@ModelAttribute("boardForm") BoardForm form, RedirectAttributes redirectAttributes) throws IOException {


        Board board = new Board();
        board.setWriter(form.getWriter());
        board.setTitle(form.getTitle());
        board.setContent(form.getContent());
        board.setCreatedDateTime(LocalDateTime.now());

        if (form.getAttachFile() != null) {
            UploadFile attachFile = fileStore.storeFile(form.getAttachFile());
            board.setAttachFile(attachFile);
        }

        boardService.addPost(board);

        redirectAttributes.addAttribute("boardId", board.getId());
        redirectAttributes.addFlashAttribute("result", "registerOK");

        return "redirect:/boards/{boardId}";
    }

    /**
     * 게시글 목록
     */
    @GetMapping("/boards")
    public String boardList(Model model,   @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "5") int size, HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        Member loginMember = (Member) session.getAttribute(LOGIN_MEMBER);

        Page<Board> boardPage = boardService.getBoardList(page, size);

        model.addAttribute("member", loginMember);
        model.addAttribute("boardPage", boardPage);
        return "boards/boardList";
    }

    /**
     * 게시글 상세보기
     */
    @GetMapping("/boards/{id}")
    public String boardView(@PathVariable int id, Model model, HttpServletRequest request) {

        Board board = boardService.detail(id); //해당 id의 게시글 찾기

        HttpSession session = request.getSession(false);
        Member loginMember = (Member) session.getAttribute(LOGIN_MEMBER);

        //게시글 댓글 가져오기
        List<Comment> comments = commentService.commentList(id);
        //댓글 작성 폼
        CommentForm commentForm = new CommentForm();

        model.addAttribute("board", board);
        model.addAttribute("member", loginMember);
        model.addAttribute("comments", comments);
        model.addAttribute("commentForm", commentForm);

        return "boards/boardView";
    }

    /**
     * 게시글 삭제
     */
    @GetMapping("/boards/{id}/delete")
    public String boardDelete(@PathVariable int id, Model model) {

        boardService.delete(id);

        model.addAttribute("message", "글이 삭제되었습니다.");
        model.addAttribute("searchUrl", "/boards");

        return "message";
    }

    /**
     * 게시글 수정 폼
     */
    @GetMapping("/boards/{id}/edit")
    public String boardEditForm(@PathVariable int id, Model model) throws IOException {

        //id로 게시물 찾아오기
        Board board = boardService.detail(id);

        if (board != null) {
            BoardForm form = new BoardForm();
            form.setWriter(board.getWriter());
            form.setTitle(board.getTitle());
            form.setContent(board.getContent());

            // 파일이 존재할 때만 파일 가져오기 -> null일 경우는 가져오지 않도록
            /*if (board.getAttachFile() != null) {
                UploadFile attachFile = fileStore.storeFile((MultipartFile) board.getAttachFile()); //casting 에러
            }*/
            model.addAttribute("boardForm", form);
        }
        return "boards/updateBoardForm";
    }

    /**
     * 게시글 수정 진행
     */
    @PostMapping("/boards/{id}/edit")
    public String boardEdit(@PathVariable int id, @ModelAttribute("boardForm") BoardForm boardForm,
                            RedirectAttributes redirectAttributes) throws IOException {

        MultipartFile file = boardForm.getAttachFile();
        UploadFile uploadFile = null;

        if (file != null && !file.isEmpty()) { //수정할 파일이 있다면
            //새로운 파일 저장
            uploadFile = fileStore.storeFile(file);

            //기존 파일이 존재하면 삭제
            Board existingBoard = boardService.detail(id);
            UploadFile existingFile = existingBoard.getAttachFile();
            if (existingFile != null) {
                fileStore.deleteFile(existingFile.getStoreFileName(), existingFile);
            }
        }

        boardService.update(id, boardForm.getTitle(), boardForm.getContent(), uploadFile);

        redirectAttributes.addAttribute("boardId", id);
        redirectAttributes.addFlashAttribute("result", "modifyOK");

        return "redirect:/boards/{boardId}";
    }

    /**
     * 파일 다운로드
     */
    @GetMapping("/attach/{id}")
    public ResponseEntity<Resource> downloadAttach(@PathVariable int id) throws MalformedURLException {

        Board board = boardService.detail(id); //id로 해당 게시글 찾아오기
        String storeFileName = board.getAttachFile().getStoreFileName(); //저장소 파일명
        String uploadFileName = board.getAttachFile().getUploadFileName(); //original 파일명

        UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(storeFileName)); //url 경로 생성

        //original 파일명의 특수 문자를 올바르게 인코딩
        String encodedUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);
        //응답을 위한 content_disposition 헤더값 생성
        String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\"";

        //상태가 200ok인 responseEntity를 구성하고 반환
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }
}