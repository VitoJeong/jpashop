package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void join() throws Exception {

        // GIVEN
        Member member = new Member();
        member.setName("HELLO");

        // WHEN
        Long joinedId = memberService.join(member);

        // THEN
        assertEquals(member, memberService.findOne(joinedId));

    }

    @Test
    void joinDuplicatedName() throws Exception {

        // GIVEN
        Member member1 = new Member();
        member1.setName("HELLO1");
        Member member2 = new Member();
        member2.setName("HELLO1");

        // WHEN
        memberService.join(member1);

        // THEN
        assertThrows(IllegalStateException.class, () -> memberService.join(member2));

    }
}