package com.mealkitcook.controller;

import com.mealkitcook.dto.MemberFormDto;
import com.mealkitcook.entity.Member;
import com.mealkitcook.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;

@SpringBootTest
@AutoConfigureMockMvc   //MockMvc테스트를 위해 선언
@Transactional
@TestPropertySource(locations="classpath:application-test.properties")
class MemberControllerTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MockMvc mockMvc;    //MockMvc클래스를 이용해 실제 객체와 비슷하지만 테스트에 필요한 기능만 가지는 가짜객체

    @Autowired
    PasswordEncoder passwordEncoder;

    public Member createMember(String email, String password) {   //로그인 예제 진행을 위해서 로그인 전 회원을 등록하는 메소드
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail(email);
        memberFormDto.setName("홍길동");
        memberFormDto.setAddress("서울시 마포구 합정동");
        memberFormDto.setPassword(password);
        Member member = Member.createMember(memberFormDto, passwordEncoder);
        return memberService.saveMember(member);
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    public void loginSuccessTest() throws Exception{
        String email = "test@email.com";
        String password = "1234";
        this.createMember(email, password);
        mockMvc.perform(formLogin().userParameter("email")
                .loginProcessingUrl("/members/login")   //회원가입 메소드를 실행후 가입된 회원정보로 로그인이 되는지 테스트 userParameter를 이용하여 이메일을 아이디로 세팅하고 로그인 url에 요청
                .user(email).password(password))
                .andExpect(SecurityMockMvcResultMatchers.authenticated());  //로그인 성공하여 인증되면 테스트코드가 통과

    }

    @Test
    @DisplayName("로그인 실패 테스트")
    public void loginFailTest() throws Exception{
        String email = "test@email.com";
        String password = "1234";
        this.createMember(email, password);
        mockMvc.perform(formLogin().userParameter("email")
                .loginProcessingUrl("/members/login")
                .user(email).password("12345"))
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated());    //회원가입은 정상적으로 진행했지만 다른 비밀번호로 로그인을 시도
    }

}