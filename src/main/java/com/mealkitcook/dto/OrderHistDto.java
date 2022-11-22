package com.mealkitcook.dto;

import com.mealkitcook.constant.OrderStatus;
import com.mealkitcook.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

//주문정보를 담을 클래스
@Getter
@Setter
public class OrderHistDto {

    public OrderHistDto(Order order){   //OrderHistDto클래스의 생성자로 order객체를 파라미터로 받아서 멤버 변수값을 세팅 주문날짜의 경우 화면에 "yyyy-MM-dd HH:mm"형태로 전달하기 위해 포맷을 수정
        this.orderId = order.getId();
        this.orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.orderStatus = order.getOrderStatus();
    }

    private Long orderId;   //주문아이디

    private String orderDate;    //주문 날짜

    private OrderStatus orderStatus;    //주문상태

    //주문 상품 리스트
    private List<OrderItemDto> orderItemDtoList = new ArrayList<>();

    public void addOrderItemDto(OrderItemDto orderItemDto){ //orderItemDto 객체를 주문 상품 리스트에 추가하는 메소드
        orderItemDtoList.add(orderItemDto);
    }
}
