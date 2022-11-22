package com.mealkitcook.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@Table(name="cart_item")
public class CartItem extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name = "cart_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cart_id")
    private Cart cart;  //하나의 장바구니에는 여러 상품을 담을 수 있으므로 @ManyToOne으로 다대일 관계로 매핑

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;  //장바구니에 담을 상품의 정보를 알아야 하므로 상품 엔티티를 매핑 하나의 상품은 여러 장바구니의 장바구니 상품으로 담길수 있으므로 다대일로 매핑

    private int count;  //같은 상품을 장바구니에 몇개 담을지 저장

    public static CartItem createCartItem(Cart cart, Item item, int count){
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setCount(count);
        return cartItem;
    }

    public void addCount(int count){    //상품을 추가로 장바구니에 담을때 기존 장바구니에 담겨있는 상품 수량에 현재 담을 수량을 더해줄 메소드
        this.count += count;
    }

    public void updateCount(int count){ //현재 장바구니에 담겨있는 수량을 변경하는 메소드
        this.count = count;
    }

}