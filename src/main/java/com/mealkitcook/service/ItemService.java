package com.mealkitcook.service;

import com.mealkitcook.dto.ItemFormDto;
import com.mealkitcook.dto.ItemImgDto;
import com.mealkitcook.dto.ItemSearchDto;
import com.mealkitcook.dto.MainItemDto;
import com.mealkitcook.entity.Board;
import com.mealkitcook.entity.Item;
import com.mealkitcook.entity.ItemImg;
import com.mealkitcook.repository.ItemImgRepository;
import com.mealkitcook.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//상품 등록하는 로직
@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;

    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{

        //상품등록
        Item item = itemFormDto.createItem();   //상품 등록폼으로부터 입력받은 데이터를 이용하여 item 객체를 생성
        itemRepository.save(item);  //상품데이터를 저장한다

        //이미지 등록
        for(int i=0; i<itemImgFileList.size(); i++){
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);
            if(i == 0)  //첫번째 이미지일 경우 대표상품 이미지 여부값을 "Y"로 세팅한다 나머지 상품 이미지는 "N"으로 설정한다
                itemImg.setRepimgYn("Y");
            else
                itemImg.setRepimgYn("N");
            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));    //상품의 이미지 정보를 저장한다
        }
        return item.getId();
    }

    //등록된 상품을 불러오는 메소드
    @Transactional(readOnly = true) //상품 데이터를 읽어오는 트랙잭션을 읽기전용을 설정 이럴경우 JPA가 변경감지를 수행하지 않아서 성능 향상한다
    public ItemFormDto getItemDtl(Long itemId){

        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId); //해당 상품의 이미지를 조회한다 등록순으로 가지고 오기 위해 상품이미지 아이디 오름차순으로 설정
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();
        for (ItemImg itemImg : itemImgList){    //조회한 ItemImg 엔티티를 ItemImgDto 객체로 만들어서 리스트에 추가
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }

        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new); //상품의 아이디를 통해 상품 엔티티를 조회, 존재하지 않을경우 익셉션 발생
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        return itemFormDto;
    }

    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{

        //상품 수정
        Item item = itemRepository.findById(itemFormDto.getId()).orElseThrow(EntityNotFoundException::new);    //상품 등록 화면으로부터 전달 받은 상품 아이디를 이용하여 상품 엔티티를 조회한다
        item.updateItem(itemFormDto);   //상품 등록 화면으로부터 전달

        List<Long> itemImgIds = itemFormDto.getItemImgIds();    //상품 이미지 아이디 리스트를 조회

        //이미지 등록
        for(int i=0; i<itemImgFileList.size(); i++){
            itemImgService.updateItemImg(itemImgIds.get(i),
                    itemImgFileList.get(i));    //상품 이미지를 업데이트하기 위해서 updateItemImg() 메소드에 상품 이미지 아이디와 상품 이미지 파일 정보를 파라미터로 전달한다
        }
        return item.getId();
    }
    //상품 조회 조건과 페이지 정보를 파라미터로 받아서 상품 데이터를 조회
    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
    }

    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getMainItemPage(itemSearchDto, pageable);
    }


}
