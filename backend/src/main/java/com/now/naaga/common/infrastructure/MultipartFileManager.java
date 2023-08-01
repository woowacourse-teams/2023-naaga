package com.now.naaga.common.infrastructure;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class MultipartFileManager implements FileManager<MultipartFile> {

    @Override
    public Path save(final MultipartFile multipartFile,
                     final String saveDirectory) {
        String originalFilename = multipartFile.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String savedFilename = UUID.randomUUID() + extension;
        Path uploadPath = Paths.get(saveDirectory, savedFilename);

        try {
            multipartFile.transferTo(uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("파일저장에 실패하였습니다.");
        }
        return uploadPath;
    }
}
