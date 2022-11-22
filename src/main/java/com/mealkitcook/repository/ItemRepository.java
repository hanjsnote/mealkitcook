package com.mealkitcook.repository;

import com.mealkitcook.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>, //<엔티티 타입클래스, 기본키>
        QuerydslPredicateExecutor<Item> ,ItemRepositoryCustom{   //QueryDslPredicateExecutor 인터페이스 상속 추가

    List<Item> findByItemNm(String itemNm);

    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);  //상품을 상푸명과 상품상세설명을 OR조건을 이용하여 조회하는 쿼리메소드

    List<Item> findByPriceLessThan(Integer price);  //파라미터로 넘어온 price보다 값이 작은 상품데이터를 조회하는 쿼리메소드

    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);  //가격이 높은순으로 조회

    @Query("select i from Item i where i.itemDetail like %:itemDetail% order by i.price desc")  //@Query어노테이션 안에 JPQL로 작성한 쿼리문을 넣어준다 from뒤에는 엔티티 클래스로 작성한 Item을 지정해주었고 item으로부터 데이터를 select하겠다는 것을 의미
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);    //@파라미터에 @Param을 이용하여 파라미터로 넘어온 값을 JPQL에 들어갈 변수로 지정 현재는 itemDetail 변수를 "like % %"사이에 ":itemDetail"로 값이 들어가도록 작성

    @Query(value="select * from item i where i.item_detail like %:itemDetail% order by i.price desc", nativeQuery = true)
    List<Item> findByItemDetailByNative(@Param("itemDetail") String itemDetail);


}
