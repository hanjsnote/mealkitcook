package com.mealkitcook.entity;

import com.mealkitcook.constant.Role;
import com.mealkitcook.dto.MemberFormDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//회원정보를 저장하는 Member엔티티
@Entity
@Table(name = "member")
@Getter @Setter
@ToString
public class Member extends BaseEntity{

    @Id
    @Column(name="member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(unique = true) //회원은 이메일을 통해 유일하게 구분해야 하기 떄문에 동일한 값이 데이터베이스에 들어올 수 없도록 unique속성을 지정
    private String email;

    private String password;

    private String phone;

    private String address;

    @Enumerated(EnumType.STRING)    //자바의 enum타입을 엔티티속성으로 지정할수 있다 Enum을 사용할때 기본적으로 순서가 저장되는데 바뀔경우 문제가 발생할수 있으므로 EnumType.STRING옵션 사용
    private Role role;

    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder){    //Member엔티티를 생성하는 메소드

        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getAddress());
        String password = passwordEncoder.encode(memberFormDto.getPassword());  //스프링 시큐리티 설정 클래스에 등록한 BCryptPasswordEncoder Bean을 파라미터로 넘겨서 비밀번호를 암호화
        member.setPassword(password);
        member.setPhone(memberFormDto.getPhone());
        member.setRole(Role.USER); //Member 엔티티 생성시 Role.USER로 생성하던 권한을 Role.ADMIN으로 생성
        return member;
    }


}
