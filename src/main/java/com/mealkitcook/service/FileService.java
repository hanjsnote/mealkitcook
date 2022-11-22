package com.mealkitcook.service;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

//파일을 업로드하고 삭제하는 메소드 작성
@Service
@Log
public class FileService {

    public String uploadFile(String uploadPath, String originalFileName, byte[] fileData) throws Exception{

        UUID uuid = UUID.randomUUID();  //UUID는 서로 다른 개체들을 구별하기 위해 이름을 부여할때 사용 실제 사용시 중복될 가능성의 거의 업기에 파일의 이름으로 사용하면 파일명 중복문제를 해결할수 있다
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String savedFileName = uuid.toString() + extension; //UUID로 받은 값과 원래 파일의 이름의 확장자를 조합하여 저장될 파일 이름을 만든다
        String fileUploadFullUrl = uploadPath + "/" + savedFileName;
        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl); //생성자로 파일이 저장될 위치와 파일의 이름을 넘겨 파일에 쓸 파일출력 스트림을 만든다
        fos.write(fileData);    //fileData를 파일 출력 스트림에 입력한다
        fos.close();
        return savedFileName;   //업로드된 파일의 이름을 반환
    }

    public void deleteFile(String filePath) throws Exception{
        File deleteFile = new File(filePath);   //파일이 저장된 경로를 이용하여 파일객체를 생성

        if(deleteFile.exists()){    //해당 파일이 존재하면 파일을 삭제
            deleteFile.delete();
            log.info("파일을 삭제하였습니다.");
        } else {
            log.info("파일이 존재하지 않습니다.");
        }

    }

}
