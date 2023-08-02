package com.now.naaga.common.infrastructure;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class MultipartFileManager implements FileManager<MultipartFile> {

    @Override
    public File save(final MultipartFile multipartFile,
                     final String saveDirectory) {
        File directory = new File(saveDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        final String originalFilename = multipartFile.getOriginalFilename();
        final String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        final String savedFilename = UUID.randomUUID() + extension;
        final File uploadPath = new File(saveDirectory, savedFilename);
        try {
            multipartFile.transferTo(uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("파일저장에 실패하였습니다.");
        }
        return uploadPath;
    }
}
