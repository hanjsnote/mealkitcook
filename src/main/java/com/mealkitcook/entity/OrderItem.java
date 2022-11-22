package com.mealkitcook.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class OrderItem extends BaseEntity{  //기존에 있던 regTime, updateTime 변수를 삭제하고 BaseEntity를 상속

    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;  //하나의 상품은 여러 주문상품으로 들어갈수 있으므로 주문상품 기준으로 다대일 단방향 매핑

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;    //한번의 주문에 여러개의 상품을 주문할수 있으므로 주문 상품 엔티티와 주문 엔티티를 다대일 단방향 매핑

    private int orderPrice; //주문가격

    private int count;  //수량

/*    private LocalDateTime regTime;

    private LocalDateTime updateTime;*/

    public static OrderItem createOrderItem(Item item, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);    //주문할 상품과 주문 수량을 세팅
        orderItem.setCount(count);
        orderItem.setOrderPrice(item.getPrice());   //현재시간 기준으로 상품가격을 주문가격으로 세팅 상품가격은 시간에 따라 달라질수 있음

        item.removeStock(count);    //주문 수량만큼 상품의 재고 수량을 감소
        return orderItem;
    }

    public int getTotalPrice(){ //주문 가격과 주문 수량을 곱해서 해당 상품을 주문한 총 가격을 계산하는 메소드
        return orderPrice*count;
    }

    //주문을 취소할때 주문 수량만큼 상품의 재고를 증가시키는 메소드
    public void cancel(){       //주문 취소시 주문 수량만큼 상품의 재고를 더해준다
        this.getItem().addStock(count);
    }

}
