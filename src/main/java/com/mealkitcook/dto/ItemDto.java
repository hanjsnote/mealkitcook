package com.mealkitcook.dto;
//뷰 영역에서 사용할 ItemDto클래스를 생성 데이터를 주고 받을때는 Entity클래스 자체를 반환하면 안되고 데이터 전달용 객체를 생성해야한다
//데이터 베이스의 설계를 외부에 노출할 필요도 없으며 요청과 응답객체가 항상 엔티티와 같지 않기 때문이다
//ItemDto 객체를 하나 생성 후 모델에 데이터를 담아서 뷰에 전달한다
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ItemDto {

    private Long id;

    private String itemNm;

    private Integer price;

    private String itemDetail;

    private String sellStatCd;

    private LocalDateTime regTime;

    private LocalDateTime updateTime;
}
