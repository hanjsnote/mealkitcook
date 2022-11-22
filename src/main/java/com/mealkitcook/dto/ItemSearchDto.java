package com.mealkitcook.dto;

import com.mealkitcook.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ItemSearchDto {

    private String searchDateType;  //현재 시간과 상품 등록일 비교해서 상품 데이터를 조회 예)all:상품 등록일 전체 1d:최근하루동안 등록된상품

    private ItemSellStatus searchSellStatus;    //상품의 판매상태를 기준으로 상품 데이터를 조회

    private String searchBy;    //상품을 조회할 때 어떤 유형으로 조회할지 선택 itemNm:상품명  createdBy:상품등록자 아이디

    private String searchQuery = "";    //조회할 검색어 저장할 변수 searchBy가 itemNm일 경우 상품명을 기준으로 검색하고 createdBy일경우 상품 등록자 아이디 기준으로 검색

}
