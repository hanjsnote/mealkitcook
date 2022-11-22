package com.mealkitcook.repository;

import com.mealkitcook.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

    //로그인한 회원의 Cart엔티티를 찾는다
    Cart findByMemberId(Long memberId);

}
