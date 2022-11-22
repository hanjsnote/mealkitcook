package com.mealkitcook.controller;

import com.mealkitcook.dto.ItemFormDto;
import com.mealkitcook.dto.ItemSearchDto;
import com.mealkitcook.entity.Item;
import com.mealkitcook.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model){
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "/item/itemForm";
    }

    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, Model model,
                          @RequestParam("itemImgFile")List<MultipartFile> itemImgFileList){

        if(bindingResult.hasErrors()){  //상품 등록시 필수값이 없다면 다시 상품등록페이지로 전환한다
            return "item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){    //상품 등록시 첫번째 이미지가 없다면 에러미시지와 함께 상품등록 페이지로 전환 상품의 첫번쨰 이미지는 메인페이지에서 보여줄 상품 이미지로 사용하기 위해 필수값으로 지정
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값입니다.");
            return "item/itemForm";
        }

        try{
            itemService.saveItem(itemFormDto, itemImgFileList); //상품 저장로직을 호출 매개변수로 상품 정보와 상품이미지 정보를 담고 있는 itemImgFileList를 넘겨준다
        } catch (Exception e){
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생했습니다.");
            return "item/itemForm";
        }
        return "redirect:/";    //상품이 정상적으로 등록되었다면 메인페이지로 이동
    }

    //상품 수정으로 진입하는 코드

    @GetMapping(value = "/admin/item/{itemId}")
    public String itemDtl(@PathVariable("itemId") Long itemId, Model model){

        try{
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);   //조회한 상품 데이터를 모델에 담아서 뷰로 전달
            model.addAttribute("itemFormDto", itemFormDto);
        } catch (EntityNotFoundException e){    //상품 엔티티가 존재하지 않을경우 에러메시지를 담아서 상품 등록페이지로 이동
            model.addAttribute("errorMessage", "존재하지 않은 상품 입니다.");
            model.addAttribute("itemFormDto", new ItemFormDto());
            return "item/itemForm";
        }

        return "item/itemForm";
    }

    @PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto,BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList, Model model){
        if(bindingResult.hasErrors()){
            return "item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }
        try{
            itemService.updateItem(itemFormDto, itemImgFileList);   //상품 수정 로직을 호출
        } catch (Exception e){
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }
        return "redirect:/";
    }

    //상품 관리 화면 이동 및 조회한 상품 데이터를 화면에 전달하는 로직
    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})    //value에 상품 관리 화면 진입시 URL에 페이지 번호가 없는 경우와 페이지 번호가 있는 경우 2가지를 매핑
    public String itemManage(ItemSearchDto itemSearchDto, @PathVariable("page")Optional<Integer> page, Model model){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 3);    //페이징을 위해 PageRequest.of 메소드를 통해 Pageable 객체를 생성 첫번째 파라미터는 페이지번호 두번째는 한번에 가지고 올 데이터 수
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);   //조회 조건과 페이징 정보를 파라미터로 넘겨서 Page<Item> 객체를 반환 받는다
        model.addAttribute("items", items); //조회한 상품 데이터 및 페이징 정보를 뷰에 전달
        model.addAttribute("itemSearchDto", itemSearchDto); //페이지 전환 시 기존 검색 조건을 유지한 채 이동할수 있또록 뷰에 다시 전달
        model.addAttribute("maxPage", 5);   //상품 관리 메뉴 하단에 보여줄 페이지의 최대 개수
        return "item/itemMng";
    }

    //상품 상세페이지로 이동하는 코드
    @GetMapping(value = "/item/{itemId}")
    public String itemDtl(Model model, @PathVariable("itemId") Long itemId){
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        model.addAttribute("item", itemFormDto);
        return "item/itemDtl";
    }


    @GetMapping(value = "/admin/memberMng")
    public String memberMng(){

        return "/member/memberMng";
    }

}
