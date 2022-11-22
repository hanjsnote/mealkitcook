package com.mealkitcook.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;
//main페이지에서 상품을 보여줄때 사용하는 dto
@Getter @Setter
public class MainItemDto {

    private Long id;

    private String itemNm;

    private String itemDetail;

    private String imgUrl;

    private Integer price;

    @QueryProjection    //생성자에 해당 어노테이션을 선언하여 Querydsl로 결과 조회시 MainItemDto 객체로 바로 받아오도록 한다
    public MainItemDto(Long id, String itemNm, String itemDetail, String imgUrl, Integer price){

        this.id = id;
        this.itemNm = itemNm;
        this.itemDetail = itemDetail;
        this.imgUrl = imgUrl;
        this.price = price;
    }

}
