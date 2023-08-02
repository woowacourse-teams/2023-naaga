package com.now.naaga.common.infrastructure;

import com.now.naaga.common.exception.CommonException;
import com.now.naaga.common.exception.CommonExceptionType;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class MultipartFileManager implements FileManager<MultipartFile> {

    @Value("${multipartFile.directory.path}")
    private String saveDirectory;

    @Override
    public File save(final MultipartFile multipartFile) {
        final File directory = new File(saveDirectory);
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
            throw new CommonException(CommonExceptionType.FILE_SAVE_ERROR);
        }
        return uploadPath;
    }
}
