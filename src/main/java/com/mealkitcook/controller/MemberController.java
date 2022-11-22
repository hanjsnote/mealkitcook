package com.mealkitcook.controller;

import com.mealkitcook.dto.ItemFormDto;
import com.mealkitcook.dto.MemberFormDto;
import com.mealkitcook.entity.Member;
import com.mealkitcook.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;


@RequestMapping("/members")
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping(value = "/agree")
    public String agree(){
        return "member/agreeForm";
    }

    @GetMapping(value = "/new")
    public String memberForm(Model model){  //회원가입 페이지로 이동할수 있도록 MemberController클래스에 메소드를 작성
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/memberForm";
    }

    /*@PostMapping(value = "/new")
    public String memberForm(MemberFormDto memberFormDto){

        Member member = Member.createMember(memberFormDto, passwordEncoder);
        memberService.saveMember(member);

        return "redirect:/";    //회원가입후 다시 되돌아간다(메인페이지)
    }*/

    //회원 가입이 성공하면 메인페이지로 리다이렉트 시켜주고 회원 정보 검증 및 중복회원 가입 조건에 의해 실패하면 다시 회원가입페이지로 돌아가 실패이유를 화면에 출력
    @PostMapping(value = "/new")
    public String newMember(@Valid MemberFormDto memberFormDto, BindingResult bindingResult, Model model){  //1

        if(bindingResult.hasErrors()){  //2
            return "member/memberForm";
        }
        //1,2 검증하려는 개체의 앞에 @Valid 어노테이션을 선언하고 파라미터로 bindingResult 객체를 추가. 검사후 결과는 bindingResult에 담아준다
        //bindingResult.hasErrors()를 호출하여 에러가 있다면 회원가입 페이지로 이동

        try{
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            memberService.saveMember(member);
        } catch (IllegalStateException e){
            model.addAttribute("errorMessage", e.getMessage()); //회원 가입시 중복회원 가입 예외가 발생하면 에러메시지를 뷰로 전달
            return "member/memberForm";
        }
        return "/member/joinCompletion";
    }

    @GetMapping(value = "/login")
    public String loginMember(){
        return "/member/memberLoginForm";
    }

    @GetMapping(value = "/login/error")
    public String loginError(Model model){
        model.addAttribute("loginErrorMsg","아이디 또는 비밀번호를 확인해주세요");
        return "/member/memberLoginForm";
    }

    //회원정보수정
    @GetMapping(value = "/memberUpdate")
    public String memberUpdate(){

        return "/member/memberUpdate";
    }


}
