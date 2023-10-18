package com.now.naaga.common.infrastructure;

import static com.now.naaga.common.exception.CommonExceptionType.INVALID_REQUEST_BODY;
import static com.now.naaga.common.exception.InternalExceptionType.FAIL_MAKE_DIRECTORY;

import com.now.naaga.common.exception.CommonException;
import com.now.naaga.common.exception.CommonExceptionType;
import com.now.naaga.common.exception.InternalException;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class MultipartFileManager implements FileManager<MultipartFile> {

    private final String saveDirectory;

    private final String imagesUrlPrefix;

    public MultipartFileManager(@Value("${image.path.directory.prefix}") final String saveDirectory,
                                @Value("${image.path.url.prefix}") final String imagesUrlPrefix) {
        this.saveDirectory = saveDirectory;
        this.imagesUrlPrefix = imagesUrlPrefix;
    }

    @Override
    public File save(final MultipartFile multipartFile) {
        final File directory = new File(saveDirectory);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                throw new InternalException(FAIL_MAKE_DIRECTORY);
            }
        }
        final String originalFilename = multipartFile.getOriginalFilename();
        if (Objects.isNull(originalFilename)) {
            throw new CommonException(INVALID_REQUEST_BODY);
        }
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

    @Override
    public String convertToUrlPath(final File file) {
        final String filePath = file.toString();
        final String fileNameIncludeDirectorySeparator = filePath.replaceAll(saveDirectory, "");
        return imagesUrlPrefix + fileNameIncludeDirectorySeparator;
    }
}
