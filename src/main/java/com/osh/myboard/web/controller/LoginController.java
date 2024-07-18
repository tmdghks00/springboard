package com.osh.myboard.web.controller;

import com.osh.myboard.domain.Member;
import com.osh.myboard.service.LoginService;
import com.osh.myboard.web.form.LoginForm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.osh.myboard.web.SessionConst.LOGIN_MEMBER;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    /**
     * 로그인 폼
     */
    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm")LoginForm loginForm) {
        return "login/loginForm";
    }

    /**
     * 로그인 하기
     */
    @PostMapping("/login")
    public String login(@Validated @ModelAttribute LoginForm loginForm, BindingResult bindingResult,
                        @RequestParam(defaultValue = "/") String redirectURL,
                        HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());
        if (loginMember == null) { //글로벌 오류 출력 (code = loginFail)
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 일치하지 않습니다.");
            return "login/loginForm";
        }

        //로그인 성공
        //세션 반환 (없으면 생성)
        HttpSession session = request.getSession();
        //세션에 로그인 회원 정보 보관
        session.setAttribute(LOGIN_MEMBER, loginMember);
        log.info("로그인 성공 : {}", loginMember);

        //debug
        log.info("Redirecting to : {}", redirectURL);

        return "redirect:" + redirectURL;

    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); //session이 존재하면 세션 내의 값 다 날아가도록
            log.info("세션에서 지우기 성공");
        }
        return "redirect:/";
    }


}