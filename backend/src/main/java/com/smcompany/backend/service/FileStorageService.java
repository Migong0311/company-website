package com.smcompany.backend.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private Path fileStoragePath;
    private Path thumbnailStoragePath;

    @PostConstruct
    public void init() {
        this.fileStoragePath = Paths.get(uploadDir, "files").toAbsolutePath().normalize();
        this.thumbnailStoragePath = Paths.get(uploadDir, "thumbnails").toAbsolutePath().normalize();
        try {
            Files.createDirectories(fileStoragePath);
            Files.createDirectories(thumbnailStoragePath);
        } catch (IOException e) {
            throw new RuntimeException("업로드 디렉토리를 생성할 수 없습니다.", e);
        }
    }

    public String storeFile(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        String extension = getExtension(originalFileName);
        String storedFileName = UUID.randomUUID() + extension;

        try {
            Path targetLocation = fileStoragePath.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return storedFileName;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장에 실패했습니다: " + originalFileName, e);
        }
    }

    public String storeThumbnail(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        String extension = getExtension(originalFileName);
        String storedFileName = "thumb_" + UUID.randomUUID() + extension;

        try {
            Path targetLocation = thumbnailStoragePath.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return storedFileName;
        } catch (IOException e) {
            throw new RuntimeException("썸네일 저장에 실패했습니다: " + originalFileName, e);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = fileStoragePath.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            }
            throw new RuntimeException("파일을 찾을 수 없습니다: " + fileName);
        } catch (MalformedURLException e) {
            throw new RuntimeException("파일을 찾을 수 없습니다: " + fileName, e);
        }
    }

    public Resource loadThumbnailAsResource(String fileName) {
        try {
            Path filePath = thumbnailStoragePath.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            }
            throw new RuntimeException("썸네일을 찾을 수 없습니다: " + fileName);
        } catch (MalformedURLException e) {
            throw new RuntimeException("썸네일을 찾을 수 없습니다: " + fileName, e);
        }
    }

    public void deleteFile(String fileName) {
        try {
            Path filePath = fileStoragePath.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제에 실패했습니다: " + fileName, e);
        }
    }

    public void deleteThumbnail(String fileName) {
        try {
            Path filePath = thumbnailStoragePath.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("썸네일 삭제에 실패했습니다: " + fileName, e);
        }
    }

    private String getExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf("."));
        }
        return "";
    }
}
