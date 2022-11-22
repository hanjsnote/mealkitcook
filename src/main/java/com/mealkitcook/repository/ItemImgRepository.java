package com.mealkitcook.repository;

import com.mealkitcook.entity.ItemImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//상품의 이미지 정보를 저장하기 위해 JpaRepository를 상속받는 인터페이스
public interface ItemImgRepository extends JpaRepository<ItemImg, Long> {

    List<ItemImg> findByItemIdOrderByIdAsc(Long itemId);

    ItemImg findByItemIdAndRepimgYn(Long itemId, String repimgYn);

}
