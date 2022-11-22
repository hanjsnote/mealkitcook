package com.mealkitcook.service;

import com.mealkitcook.dto.MemberFormDto;
import com.mealkitcook.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
//회원가입기능 테스트
@SpringBootTest
@Transactional  //테스트 클래스에 @Transactional을 선언할 경우 테스트 실행후 롤백처리가 된다
@TestPropertySource(locations = "classpath:application-test.properties")
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Member createMember(){   //회원 정보를 입력한 Member엔티티를 만드는 메소드 작성
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail("test@email.com");
        memberFormDto.setName("홍길동");
        memberFormDto.setAddress("서울시 마포구 합정동");
        memberFormDto.setPassword("!234");
        memberFormDto.setPhone("0101231234");
        return Member.createMember(memberFormDto, passwordEncoder);
    }

    @Test
    @DisplayName("회원가입 테스트")
    public void saveMemberTest() {   //Junit의 Assertions클래스의 assertEquals 메소드를 이용하여 저장하려고 요청했던 값과 실제 저장된 데이터를 비교 첫번째 파라미터는 기댓값 두번째 파라미터는 실제로 저장된 값을 넣어준다
        Member member = createMember();
        Member savedMember = memberService.saveMember(member);

        assertEquals(member.getEmail(), savedMember.getEmail());
        assertEquals(member.getName(), savedMember.getName());
        assertEquals(member.getAddress(), savedMember.getAddress());
        assertEquals(member.getPassword(), savedMember.getPassword());
        assertEquals(member.getPhone(), savedMember.getPassword());
        assertEquals(member.getRole(), savedMember.getRole());
    }

    @Test
    @DisplayName("중복 회원 가입 테스트")
    public void saveDuplicateMemberTest(){
        Member member1 = createMember();
        Member member2 = createMember();
        memberService.saveMember(member1);

        Throwable e = assertThrows(IllegalStateException.class, () -> { //Junit의 Assertions 클래스의 assertThrows 메소드를 이용하면 예외 처리 테스트가 가능하다. 첫번째 파라미터에는 발생할 예외 타입을 넣어준다
            memberService.saveMember(member2);
        });
        assertEquals("이미 가입된 회원입니다.", e.getMessage());  //발생한 예외 메시지가 예상 결과와 맞는지 검증
    }

}
