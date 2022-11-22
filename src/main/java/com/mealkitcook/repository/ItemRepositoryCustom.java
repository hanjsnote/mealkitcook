package com.mealkitcook.repository;

import com.mealkitcook.dto.ItemSearchDto;
import com.mealkitcook.dto.MainItemDto;
import com.mealkitcook.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


//Querydsl을 Spring Data Jpa와 함께 사용하기 위해 사용자 정의 리포지토리 정의
//총 3단계 과정으로 구현 1.사용자 정의 인터페이스 작성 2.사용자 정의 인터페이스 구현 3.Spring Data Jpa 리포지토리에서 사용자 정의 인터페이스 상속
public interface ItemRepositoryCustom {

    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
    //상품조회 조건을 담고있는 itemSearchDto객체와 페이징 정보를 담고있는 pageable 객체를 파라미터로 받는 getAdminItemPage메소드를 정의 반환데이터로 Page<Item> 객체를 반환

    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

}
