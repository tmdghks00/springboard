package com.osh.myboard.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
public class UploadFile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String uploadFileName; //회원이 업로드한 파일명

    private String storeFileName; //서버 내부에서 관리하는 파일명

    public UploadFile(String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }

}