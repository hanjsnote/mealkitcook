package com.mealkitcook.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
//소스코드를 보면 Member엔티티에서 Cart 엔티티와 관련된 소스가 없다는것을 확인할수 있다 즉 장바구니 엔티티가 일방적으로 회원 엔티티를 참조함
//장바구니와 회원은 일대일로 매핑돼 있으며 장바구니 엔티티가 회원 엔티티를 참조하는 일대일 단방향 매핑이다
@Entity
@Table(name = "cart")
@Getter @Setter
@ToString
public class Cart {

    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)   //회원 엔티티와 일대일로 매핑
    @JoinColumn(name="member_id")   //매핑할 외래키를 지정 name속성에는 매핑할 외래키의 이름을 설정 name을 명시하지 않으면 JPA가 알아서 ID를 찾지만 컬럼명이 원하는 대로 생성되지 않을수 있기에 직접 지정
    private Member member;

    //회원 엔티티를 파라미터로 받아 장바구니 엔티티를 생성
    public static Cart createCart(Member member){
        Cart cart = new Cart();
        cart.setMember(member);
        return cart;
    }
}
