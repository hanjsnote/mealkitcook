package com.mealkitcook.repository;

import com.mealkitcook.constant.ItemSellStatus;
import com.mealkitcook.entity.Item;
import com.mealkitcook.entity.QItem;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")    //테스트코드 실행시 aplication.properties보다 우선순위로 실행된다
class ItemRepositoryTest {

    @PersistenceContext
    EntityManager em;   //영속성 컨텍스트를 사용하기 위해 @PersistenceContext어노테이션을 이용해 EntityManager빈을 주입

    @Autowired
    ItemRepository itemRepository;   //ItemRepository를 사용하기위해 @Autowired 어노테이션을 이용하여 Bean을 주입

    /*@Test
    @DisplayName("상품 저장 테스트")   //테스트명
    public void createItemTest(){
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        Item savedItem = itemRepository.save(item);
        System.out.println(savedItem.toString());
    }*/

    public void createItemList(){   //데이터베이스에 상품데이터가 없으므로 테스터 생성을 위해 10개의 상품을 저장하는 메소드작성 findByItemNmTest()에서 실행
        for(int i=1; i<=10; i++) {
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            Item savedItem = itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품명 조회 테스트")
    public void findByItemNmTest(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemNm("테스트 상품1");   //ItemRepository에서 작성한 findByItemNm 메소드호출 파라미터로는 "테스트 상품1"전달
        for(Item item : itemList){      //조회결과 얻은 item객체들을 출력
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("상품명, 상품상세설명 or 테스트")
    public void findByItemNmOrItemDetailTest(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemNmOrItemDetail("테스트 상품1", "테스트 상품 상세 설명5");
        for(Item item : itemList){                          //상품명이 "테스트 상품1" 또는 상세설명이 "테스트 상품 상세 설명5"이면 해당 상품을 itemList에 할당
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("가격 LessThan 테스트")
    public void findByPriceLessThanTest(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByPriceLessThan(10005);
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("가격 내림차순 조회 테스트")
    public void findByPriceLessThanOrderByPriceDesc(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByPriceLessThanOrderByPriceDesc(10005);
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("@Query를 이용한 상품 조회 테스트")
    public void findByItemDetailTest(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemDetail("테스트 상품 상세 설명");  //"테스트 상품 상세 설명"을 포함하는 상품데이터 10개가 가격이 높은순으로 조회
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("nativeQuery 속성을 이용한 상품 조회 테스트")
    public void findByItemDetailByNative(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemDetailByNative("테스트 상품 상세 설명");
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("Querydsl 조회 테스트1")
    public void queryDslTest(){
        this.createItemList();
        JPAQueryFactory queryFactory = new JPAQueryFactory(em); //JPAQueryFactory를 이용하여 쿼리를 동적으로 생성 생성자의 파리미터는 EntityManager 객체를 넣어준다
        QItem qItem = QItem.item;   //Querydsl을 통해 쿼리를 생성하기 위해 플러그일을 통해 자동으로 생성된 QItem 객체를 이용
        JPAQuery<Item> query = queryFactory.selectFrom(qItem)   //자바소스코드이지만 sql문과 비슷하게 소스를 작성할수 있다
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(qItem.itemDetail.like("%" + "테스트 상품 상세 설명" + "%"))
                .orderBy(qItem.price.desc());

        List<Item> itemList = query.fetch();    //JPAQuery 메소드중 하나인 fetch를 이용해서 쿼리 결과를 리스트로 반환 fetch() 메소드 실행 시점에 쿼리문이 실행

        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    public void createItemList2(){  //상품 데이터를 만드는 새로운 메소드 1번부터 5번은 상품의 판매상태 SELL(판매중)으로 지정
        for(int i=1; i<=5; i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }

        for(int i=6; i<=10; i++){   //6번부터 10번상품은 판매상태를 SOLD_OUT(품절)로 세팅해 생성
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
            item.setStockNumber(0);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품 Querydsl 조회 테스트 2")
    public void queryDslTest2(){

        this.createItemList2();

        BooleanBuilder booleanBuilder = new BooleanBuilder();   //쿼리에 들어갈 조건을 만들어주는 빌더
        QItem item = QItem.item;
        String itemDetail = "테스트 상품 상세 설명";
        int price = 10003;
        String itemSellStat = "SELL";

        booleanBuilder.and(item.itemDetail.like("%" + itemDetail + "%"));   //필요한 상품을 조회하는데 필요한 and조건 추가 아래 소스에서
        booleanBuilder.and(item.price.gt(price));           //상품의 판매상태가 SELL일때만 booleanBuilder에 판매상태조건을 동적으로 추가

        if(StringUtils.equals(itemSellStat, ItemSellStatus.SELL)){
            booleanBuilder.and(item.itemSellStatus.eq(ItemSellStatus.SELL));
        }

        Pageable pageable = PageRequest.of(0, 5); //데이터를 페이징해 조회하도록 PageRequest.of() 메소드를 이용해 Pageble 객체를 생성 첫번째 인자는 조회할 페이지 번호, 두번째는 한페이지당 조회할 데이터 갯수
        Page<Item> itemPagingResult = itemRepository.findAll(booleanBuilder, pageable); //QueryDslPredicateExecutor인터페이스에서 정의한 findAll()메소드를 이용해 조건에 맞는 데이터를 Page 객체로 받아온다
        System.out.println("total elements : " + itemPagingResult.getTotalElements());

        List<Item> resultItemList = itemPagingResult.getContent();
        for(Item resultItem: resultItemList){
            System.out.println(resultItem.toString());
        }
    }



}