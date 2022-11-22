package com.mealkitcook.entity;

import com.mealkitcook.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//주문과 주문상품엔티티 매핑관계 매핑
@Entity
@Table(name = "orders")  //정렬할때 사용하는 "order"키워드가 있기 때문에 Order엔티티에 매핑되는 테이블로 "orders"를 지정
@Getter @Setter
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;  //한명의 회원은 여러번 주문을 할 수 있으므로 주문엔티티 기준에서 다대일 단방향 매핑

    private LocalDateTime orderDate;    //주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;    //주문상태

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,   //부모엔티티의 영속성 상태변화를 자식 엔티티에 모두 전이하는 CascadeTypeAll 옵션설정
                orphanRemoval = true, fetch = FetchType.LAZY)   //고아객체 제거
    //주문상품 엔티티와 일대다 매핑을 한다. 외래키(order_id)가 order_item 테이블에 있으므로 연관관계의 주인은 OrderItem 엔티티이다. Order엔티티가 주인이 아니므로 "mappedBy"속성으로
    //연관관계의 주인을 설정한다. 속성의 값으로 "order"를 적어준 이유는 OrderItem에 있는 Order에 의해 관리된다는 의미로 해석 즉 연관관계의 주인의 필드인 order를 mappedBy의 값으로 세팅
    private List<OrderItem> orderItems = new ArrayList<>(); //하나의 주문이 여러개의 상품을 갖으므로 List자료형으로 매핑

    private LocalDateTime regTime;

    private LocalDateTime updateTime;

    //생성한 주문 상품 객체를 이용하여 주문 객체를 만드는 메소드
    public void addOrderItem(OrderItem orderItem){  //orderItems에는 주문 상품 정보들을 담아준다 orderItem객체를 order객체의 orderItems에 추가
        orderItems.add(orderItem);
        orderItem.setOrder(this);   //Order엔티티와 OrderItem엔티티가 양방향 참조 관계이므로 orderItem객체에도 order객체를 세팅
    }

    public static Order createOrder(Member member, List<OrderItem> orderItemList){
        Order order = new Order();
        order.setMember(member);    //상품을 주문한 회원의 정보를 세팅
        for(OrderItem orderItem : orderItemList){   //상품 페이지에는 1개의 상품을 주문하지만 장바구니 페이지에서는 한번에 여러개 상품을 주문할수 있다 따라서 여러개의 주문상품을 담을수 있도록 리스트형태로 파라미터값을 받으며 주문객체에 orderItem객체를 추가
            order.addOrderItem(orderItem);
        }
        order.setOrderStatus(OrderStatus.ORDER);    //주문 상태를 "ORDER"로 세팅
        order.setOrderDate(LocalDateTime.now());    //현재 시간을 주문 시간으로 세팅
        return order;
    }

    public int getTotalPrice(){ //총 주문 금액을 구하는 메소드
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    //주문 취소시 주문수량을 상품의 재고에 더해주는 로직과 주문상태를 취소로 바꿔주는 메소드
    public void cancelOrder(){
        this.orderStatus = OrderStatus.CANCEL;

        for(OrderItem orderItem : orderItems){
            orderItem.cancel();
        }
    }


}
