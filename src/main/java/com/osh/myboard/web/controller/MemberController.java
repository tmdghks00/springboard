package com.osh.myboard.web.controller;

import com.osh.myboard.domain.Member;
import com.osh.myboard.service.MemberService;
import com.osh.myboard.web.form.AddMemberForm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.osh.myboard.web.SessionConst.LOGIN_MEMBER;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원 등록 폼
     */
    @GetMapping("/members/new")
    public String createForm(Model model) {

        model.addAttribute("addMemberForm", new AddMemberForm());
        return "members/createMemberForm";
    }

    /**
     * 회원 가입 진행
     */
    @PostMapping("/members/new")
    public String create(@Validated @ModelAttribute AddMemberForm addMemberForm, BindingResult result, Model model,
                         RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            log.info("errors={}", result);
            return "members/createMemberForm";
        }

        Member member = new Member();
        member.setLoginId(addMemberForm.getLoginId());
        member.setPassword(addMemberForm.getPassword());
        member.setUsername(addMemberForm.getUsername());
        member.setEmail(addMemberForm.getEmail());

        try {
            memberService.join(member);

            redirectAttributes.addFlashAttribute("result", "addOK");
            return "redirect:/";

        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", "이미 존재하는 아이디입니다.");
            return "members/createMemberForm";
        }

    }

    /**
     * 마이페이지 (회원 정보 상세)
     */
    @GetMapping("/members/{id}")
    public String memberInfo(@PathVariable Long id, Model model, HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        Member loginMember = (Member) session.getAttribute(LOGIN_MEMBER);

        model.addAttribute("member", loginMember);

        return "members/myPage";
    }

    /**
     * 회원 정보 수정 폼
     */
    @GetMapping("/members/{id}/edit")
    public String editInfoForm(@PathVariable Long id, Model model, HttpServletRequest request) {

        //로그인 한 회원 찾아오기
        HttpSession session = request.getSession(false);
        Member loginMember = (Member) session.getAttribute(LOGIN_MEMBER);

        //회원 정보 받아오기
        AddMemberForm form = new AddMemberForm();
        form.setLoginId(loginMember.getLoginId());
        form.setPassword(loginMember.getPassword());
        form.setUsername(loginMember.getUsername());
        form.setEmail(loginMember.getEmail());

        model.addAttribute("addMemberForm", form);
        model.addAttribute("memberId", loginMember.getId());
        return "members/editInfo";
    }

    /**
     * 회원 정보 수정 진행
     */
    @PostMapping("/members/{id}/edit")
    public String editInfo(@PathVariable Long id, @Validated @ModelAttribute("addMemberForm") AddMemberForm form,
                           BindingResult result, RedirectAttributes redirectAttributes,
                           HttpServletRequest request) {

        if (result.hasErrors()) {
            log.info("errors={}", result);
            return "members/editInfo";
        }

        //회원 정보 변경
        memberService.update(id, form.getUsername(), form.getEmail());

        //변경된 회원 가져오기
        Member updateMember = memberService.findOne(id);
        //세션 찾아서 같은 코드값의 회원의 정보를 새 값으로 업데이트
        HttpSession session = request.getSession(false);
        session.setAttribute(LOGIN_MEMBER, updateMember);

        redirectAttributes.addAttribute("memberId", id);
        redirectAttributes.addFlashAttribute("result", "editInfoOK");

        return "redirect:/members/{memberId}";
    }


}