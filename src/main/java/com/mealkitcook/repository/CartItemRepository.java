package com.mealkitcook.repository;

import com.mealkitcook.dto.CartDetailDto;
import com.mealkitcook.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

//장바구니에 들어갈 상품을 저장하거나 조회
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    CartItem findByCartIdAndItemId(Long cartId, Long itemId);   //카트 아이디와 상품 아이디를 이용해서 상품이 장바구니에 들어잇는지 조회

    //장바구니 페이지에 전달할 CartDetailDto리스트를 쿼리하나로 조회하는 JPQL문 작성
    @Query("select new com.mealkitcook.dto.CartDetailDto(ci.id, i.itemNm, i.price, ci.count, im.imgUrl) " +
            //CartDetailDto의 생성자를 이용하여 DTO를 반환할때는 "new com.shop.dto.CartDetailDto(ci.id, i.itemNm, i.price, ci.count, im.imgUrl)" 처럼 new 키워드와 해당 DTO의 패키지, 클래스명을 적어준다
            "from CartItem ci, ItemImg im " +
            "join ci.item i " +
            "where ci.cart.id = :cartId " +
            "and im.item.id = ci.item.id " + //장바구니에 담겨있는 상품의 대표 이미지만 가져오도록 조건문을 작성
            "and im.repimgYn = 'Y' " + //장바구니에 담겨있는 상품의 대표 이미지만 가져오도록 조건문을 작성
            "order by ci.regTime desc"
    )
    List<CartDetailDto> findCartDetailDtoList(Long cartId);


}
