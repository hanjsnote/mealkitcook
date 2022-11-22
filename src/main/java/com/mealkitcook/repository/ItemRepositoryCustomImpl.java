package com.mealkitcook.repository;


import com.mealkitcook.constant.ItemSellStatus;
import com.mealkitcook.dto.ItemSearchDto;
import com.mealkitcook.dto.MainItemDto;
import com.mealkitcook.dto.QMainItemDto;
import com.mealkitcook.entity.Item;
import com.mealkitcook.entity.QItem;
import com.mealkitcook.entity.QItemImg;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{  //ItemRepositoryCustom 상속받는다

    private JPAQueryFactory queryFactory;   //동적으로 쿼리를 생성하기 위해 JPAQueryFactory 클래스를 사용

    public ItemRepositoryCustomImpl(EntityManager em){  //JPAQueryFactory의 생성자로 EntityManager 객체를 넣는다
        this.queryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus){ //상품 판매 상태 조건이 전체(null)일 경우는 null을 리턴, 결과값이 null이면 where절에서 해당 조건은 무시된다 null이 아니라 판매중or 품절이면 해당조건의 상품만 조회
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }

    //searchDateType의 값에 따라서 dateTime의 값을 이전 시간의 값으로 세팅후 해당시간 이후로 등록된 상품만 조회 예를들어
    //searchDateType 값이 "1m"인 경우 dataTime의 시간을 한달 전으로 세팅후 최근 한달동안 등록된 상품만 조회하도록 조건값을 반환
    private BooleanExpression regDtsAfter(String searchDateType){
        LocalDateTime dateTime = LocalDateTime.now();

        if(StringUtils.equals("all", searchDateType) || searchDateType == null){
            return null;
        } else if(StringUtils.equals("1d", searchDateType)){
            dateTime = dateTime.minusDays(1);
        } else if(StringUtils.equals("1w", searchDateType)){
            dateTime = dateTime.minusWeeks(1);
        } else if(StringUtils.equals("1m", searchDateType)){
            dateTime = dateTime.minusMonths(1);
        } else if(StringUtils.equals("6m", searchDateType)){
            dateTime = dateTime.minusMonths(6);
        }

        return QItem.item.regTime.after(dateTime);
    }

    //searchBy의 값에 따라서 상품명에 검색어를 포함하고 있는 상품 또는 상품 생성자의 아이디에 검색어를 포함하고 있는 상품을 조회하도록 조건값을 반환
    private BooleanExpression searchByLike(String searchBy, String searchQuery){

        if(StringUtils.equals("itemNm", searchBy)){
            return QItem.item.itemNm.like("%" + searchQuery + "%");
        } else if(StringUtils.equals("createdBy", searchBy)){
            return QItem.item.createdBy.like("%" + searchQuery + "%");
        }

        return null;
    }

    //queryFactory를 이용해서 쿼리를 생성 쿼리문을 직접 작성할때의 형태와 문법이 비슷하다
    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        QueryResults<Item> results = queryFactory.selectFrom(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(),
                                itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<Item> content=results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);    //조회한 데이터를 Page클래스의 구현체인 PgeImpl 객체로 반환
    }


    private BooleanExpression itemNmLike(String searchQuery){ //검색어가 null이 아니면 상품명에 해당 검색어가 포함되는 상품을 조회하는 조건을 반환

        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemNm.like("%" + searchQuery + "%");
    }

    //메인페이지에 상품리스트를 가져오는 메소드 구현
    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        QueryResults<MainItemDto> results = queryFactory.select(new QMainItemDto(   //QMainItemDto의 생성자에 반환할 값을 넣어준다
                item.id,
                item.itemNm,
                item.itemDetail,
                itemImg.imgUrl,
                item.price)
        )
                .from(itemImg)
                .join(itemImg.item, item)   //itemImg와 item을 내부 조인한다
                .where(itemImg.repimgYn.eq("Y"))    //상품이미지의 경우 대표 상품 이미지만 불러온다
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<MainItemDto> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }



}
