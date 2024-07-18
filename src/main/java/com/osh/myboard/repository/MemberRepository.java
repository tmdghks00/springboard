package com.osh.myboard.repository;

import com.osh.myboard.domain.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository //컴포넌트 스캔에서 자동으로 스프링 빈으로 등록
@RequiredArgsConstructor
@Slf4j
public class MemberRepository {

    private final EntityManager em;

    /**
     * 저장
     */
    public void save(Member member) {
        log.info("persist 성공");
        em.persist(member);
    }

    /**
     * 조회
     */
    public List<Member> findAll() { //전체 조회

        List<Member> result = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        return result;
    }

    public Member findOne(Long id) { //id로 회원 한명 조회
        return em.find(Member.class, id);
    }

    public Member findByLoginId(String loginId) { //loginId로 회원 한명 조회

        try {
            Member result = em.createQuery("select m from Member m where m.loginId = :loginId", Member.class)
                    .setParameter("loginId", loginId)
                    .getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        }
    }

}