package com.mealkitcook.service;

import com.mealkitcook.entity.ItemImg;
import com.mealkitcook.repository.ItemImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;

//상품 이미지를 업로드하고 상품 이미지 정보를 저장하는 클래스
@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgService {

    @Value("${itemImgLocation}") //@Value를 통해 application.properties에 등록한 itemImgLocation값을 불러와서 itemImgLocation 변수에 넣어준다
    private String itemImgLocation;

    private final ItemImgRepository itemImgRepository;

    private final FileService fileService;

    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception{

        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        //파일 업로드
        if(!StringUtils.isEmpty(oriImgName)){
            imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            //사용자가 상품의 이미지를 등록했다면 저장할 경로와 파일의 이름, 파일을 uploadFile메소드를 호출 호출결과 로컬에 저장된 파일의 이름을 imgName변수에 저장
            imgUrl = "/images/item/" + imgName; //저장한 상품 이미지를 불러올 경로를 설정한다 외부리소스를 불러오는 urlPatterns로 WebMvcConfig클래스에서 "/images/**"를 설정해주었다
            //또한 application.properties에서 설정한 uploadPath프로퍼티 경로인 "C:/shop/"아래 item폴더에 이미지를 저장하므로 상품 이미지를 불러오는 경로로 "/images/item/를 붙여준다
        }

        //상품 이미지 정보 저장
        itemImg.updateItemImg(oriImgName, imgName, imgUrl); //입력받은 상품이미지 정보를 저장
        itemImgRepository.save(itemImg);    //입력받은 상품 이미지 정보를 저장
        //imgName:실제 로컬에 저장된 상품이미지 파일이름
        //oriImgName:업로드했던 상품이미지 파일의 원래이름
        //imgUrl:업로드 결과 로컬에 저장된 상품 이미지 파일을 불러오는 경로
    }

    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception{
        if(!itemImgFile.isEmpty()){ //상품이미지를 수정후 업데이트한다
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId).orElseThrow(EntityNotFoundException::new);    //상품이미지 아이디를 이용하여 기존에 저장했던 상품이미지 엔티티를 조회한다
            //기존 이미지 파일 삭제
            if(!StringUtils.isEmpty(savedItemImg.getImgName())){    //기존에 등록된 상품 이미지 파일이 있을 경우 해당파일을 삭제합니다
                fileService.deleteFile(itemImgLocation+"/"+ savedItemImg.getImgName());
            }
            String oriImgName = itemImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());   //업데이트한 상품 이미지 파일을 업로드한다
            String imgUrl = "/images/item/" + imgName;
            savedItemImg.updateItemImg(oriImgName, imgName, imgUrl);
            //변경된 상품 이미지 정보를 세팅한다. 상품등록때처럼 itemImgRepository.save()로직을 호출하지 않는다 savedItemImg엔티티는 현재 영속상태이므로 데이터를 변경하는 것만으로
            //변경감지기능이 동작하여 트랜잭션이 끝날때 update쿼리가 실행된다
        }
    }


}
