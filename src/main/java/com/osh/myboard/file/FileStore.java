package com.osh.myboard.file;

import com.osh.myboard.domain.UploadFile;
import com.osh.myboard.service.UploadFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

@Slf4j
@Component
public class FileStore {

    @Value("${file.dir}")
    private String fileDir;

    @Autowired
    private UploadFileService uploadFileService;

    public String getFullPath(String filename) {
        return fileDir + filename;
    }

    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {

        if (multipartFile.isEmpty()) {
            return null;
        }
        // 디렉토리 존재 여부 확인 및 생성
        File directory = new File(fileDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String uploadFileName = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(uploadFileName);

        multipartFile.transferTo(new File(getFullPath(storeFileName)));
        return new UploadFile(uploadFileName, storeFileName);
    }

    @Transactional
    public void deleteFile(String storeFileName, UploadFile existingFile) {
        File file = new File(getFullPath(storeFileName));
        if (file.exists()) {
            if (file.delete()) {
                log.info("File deleted successfully: {}", storeFileName);

                // db에서 삭제
                uploadFileService.deleteFile(existingFile.getId());
                log.info("Database entry deleted successfully for fileId: {}", existingFile.getId());
            } else {
                log.warn("Failed to delete file: {}", storeFileName);
            }
        } else {
            log.warn("File not found: {}", storeFileName);
        }
    }

    private String createStoreFileName(String originalFileName) {

        String ext = extractExt(originalFileName);
        String uuid = UUID.randomUUID().toString();

        return uuid + "." + ext; //uuid.ext를 저장소 파일명으로 설정

    }

    private String extractExt(String originalFileName) {

        int pos = originalFileName.lastIndexOf("."); //.의 위치 찾기
        return originalFileName.substring(pos+1); //.이후부터 끝까지 추출
    }

}