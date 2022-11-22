package com.mealkitcook.entity;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.mealkitcook.constant.ItemSellStatus;
import com.mealkitcook.dto.ItemFormDto;
import com.mealkitcook.exception.OutOfStockException;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity     //Item 클래스를 Entity로 지정
@Table(name = "item")   //item테이블과 매핑
@Getter
@Setter
@ToString
public class Item extends BaseEntity {

    @Id //기본키
    @Column(name = "item_id")   //기본키가 될 컬럼
    @GeneratedValue(strategy = GenerationType.AUTO) //기본키 생성전략을 AUTO로 지정
    private Long id;    //상품코드

    @Column(nullable = false, length = 50)  //notnull설정
    private String itemNm;   //상품명

    @Column(name = "price", nullable = false)
    private int price;  //가격

    @Column(nullable = false)
    private int stockNumber;    //재고수량

    @Lob
    @Column(nullable = false)
    private String itemDetail;  //상품 상세 설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus;  //상품 판매 상태

    private LocalDateTime regTime; //등록시간
    private LocalDateTime updateTime;   //수정시간

    public void updateItem(ItemFormDto itemFormDto){
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
    }

    public void removeStock(int stockNumber){
        int restStock = this.stockNumber - stockNumber; //상품의 재고 수량에서 주문 후 남은 재고수량을 구한다
        if(restStock<0){
            throw new OutOfStockException("상품의 재고가 부족합니다.(현재 재고 수량: " + this.stockNumber + ")");    //상품의 재고가 주문 수량보다 작을때 예외 발생
        }
        this.stockNumber = restStock;   //주문후 남은 재고 수량을 상품의 현재 재고 값으로 할당
    }

    //주문 취소를 위한 메소드
    public void addStock(int stockNumber){  //상품의 재고를 증가시키는 메소드
        this.stockNumber += stockNumber;
    }



}


