package com.osh.myboard.service;

import com.osh.myboard.repository.UploadFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UploadFileService {

    private final UploadFileRepository uploadFileRepository;

    /**
     * 첨부파일 삭제
     */
    @Transactional
    public void deleteFile(int idx) {
        uploadFileRepository.deleteById(idx);
    }
}