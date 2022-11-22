package com.mealkitcook.service;

import com.mealkitcook.constant.ItemSellStatus;
import com.mealkitcook.dto.CartItemDto;
import com.mealkitcook.entity.CartItem;
import com.mealkitcook.entity.Item;
import com.mealkitcook.entity.Member;
import com.mealkitcook.repository.CartItemRepository;
import com.mealkitcook.repository.ItemRepository;
import com.mealkitcook.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import static com.mealkitcook.entity.QCartItem.cartItem;
import static com.mealkitcook.entity.QItem.item;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class CartServiceTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CartService cartService;

    @Autowired
    CartItemRepository cartItemRepository;

    public Item saveItem(){     //1
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        return itemRepository.save(item);
    }

    public Member saveMember(){ //2
        Member member = new Member();
        member.setEmail("test@test.com");
        return memberRepository.save(member);
    }
    //1,2 테스트를 위해 장바구니에 담을 상품과 회원정보를 저장하는 메소드 생성

    @Test
    @DisplayName("장바구니 담기 테스트")
    public void addCart(){
        Item item = saveItem();
        Member member = saveMember();

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCount(5);    //3
        cartItemDto.setItemId(item.getId());    //4

        //3,4 장바구니에 담을 상품과 수량을 cartItemDto 객체에 세팅

        Long cartItemId = cartService.addCart(cartItemDto, member.getEmail());  //상품을 장바구니에 담는 로직 호출 결과 생성된 장바구니 상품 아이디를 cartItemId 변수에 저장합니다

        CartItem cartItem = cartItemRepository.findById(cartItemId) //장바구니 상품 아이디를 이용하여 생성된 장바구니 상품정보를 조회
                .orElseThrow(EntityNotFoundException::new);

        assertEquals(item.getId(), cartItem.getItem().getId()); //상품 아이디와 장바구니에 저장된 상품 아이디가 같다면 테스트 통과
        assertEquals(cartItemDto.getCount(), cartItem.getCount());  //장바구니에 담았던 수량과 실제로 장바구니에 저장된 수량이 같다면 테스트 통과
    }

}