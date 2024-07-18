package com.osh.myboard.service;

import com.osh.myboard.repository.MemberRepository;
import com.osh.myboard.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;

    /**
     * 로그인
     */
    public Member login(String loginId, String password) {

        Member findMember = memberRepository.findByLoginId(loginId);
        if (findMember != null && findMember.getPassword().equals(password)) {
            return findMember;
        } else {
            return null;
        }
    }


}