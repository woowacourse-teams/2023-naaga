package com.now.naaga.common.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class MultipartFileManager implements FileManager<MultipartFile> {

    @Value("${multipartFile.directory.path-local}")
    private String saveDirectory;

    @Override
    public Path save(final MultipartFile multipartFile) {
        final String originalFilename = multipartFile.getOriginalFilename();
        final String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        final String savedFilename = UUID.randomUUID() + extension;
        final Path uploadPath = Paths.get(saveDirectory, savedFilename);
        try {
            multipartFile.transferTo(uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("파일저장에 실패하였습니다.");
        }
        return uploadPath;
    }
}
