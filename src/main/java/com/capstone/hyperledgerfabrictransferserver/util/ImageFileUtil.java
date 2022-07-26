package com.capstone.hyperledgerfabrictransferserver.util;

import com.capstone.hyperledgerfabrictransferserver.aop.customException.IncompatibleExtensionException;
import com.capstone.hyperledgerfabrictransferserver.domain.ImageFileExtension;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ImageFileUtil {

    private static final List<String> enableExtensionList = Stream.of(ImageFileExtension.values())
            .map(extension -> extension.name())
            .collect(Collectors.toList());

    public String getFileExtension(String fileName) {
        int start = fileName.lastIndexOf(".") + 1;
        int end = fileName.length();

        return fileName.substring(start, end);
    }

    public boolean isEnableExtension(String fileName) {
        return enableExtensionList.contains(getFileExtension(fileName).toLowerCase())
                || enableExtensionList.contains(getFileExtension(fileName).toUpperCase());
    }

    public void saveImageFileByMultipartFile(MultipartFile multipartFile) throws IOException {
        if (!isEnableExtension(multipartFile.getName())) {
            throw new IncompatibleExtensionException("호환하지 않는 이미지 파일 확장자입니다");
        }

        String imageFilePath = System.getenv("IMAGE_FILE_PATH");
        File writtenFile = new File(imageFilePath + UUID.randomUUID() + "." + getFileExtension(multipartFile.getName()));
        InputStream inputStream = multipartFile.getInputStream();
        FileOutputStream fileOutputStream = new FileOutputStream(writtenFile);

        byte[] readBytes = inputStream.readAllBytes();
        fileOutputStream.write(readBytes);

        inputStream.close();
        fileOutputStream.close();
    }

}