package com.mealkitcook.service;

import com.mealkitcook.entity.Member;
import com.mealkitcook.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional  //비즈니스 로직을 담당하는 계층 클래스에 @Transactional 선언 로직을 처리하다 에러발생시 변경된 데이터를 로직 수행하기 전으로 콜백시킨다
@RequiredArgsConstructor    //final이나 @NonNull이 붙은 필드에 생성자를 생성해준다 빈에 생성자가 1개이고 파라미터타입이 빈으로 등록 가능하다면 @Autowired 없이 의존성 주입 가능
public class MemberService implements UserDetailsService {  //MemberService가 UserDetailService를 구현

    private final MemberRepository memberRepository;

    public Member saveMember(Member member){
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member){    //이미 가입된 회원의 경우 예외발생
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if(findMember != null){
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }

    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{   //UserDetailsService인터페이스의 loadUserByUsername() 메소드를 오버라이딩 로그인할 유저의 email을 파라미터로 전달받는다

        Member member = memberRepository.findByEmail(email);

        if(member == null){
            throw new UsernameNotFoundException(email);
        }

        return User.builder()   //UserDetail을 구현하고 있는 User객체를 반환 User객체를 생성하기 위해 생성자로 회원의 이메일, 비밀번호 role파라미터 넘겨준다
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }



}
