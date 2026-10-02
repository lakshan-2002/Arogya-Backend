package com.lakshan.medical_records_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);
    private final Path fileStorageLocation;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        log.info("Initializing FileStorageService with upload directory: {}", this.fileStorageLocation);
        try {
            Files.createDirectories(this.fileStorageLocation);
            log.info("Upload directory created/verified successfully: {}", this.fileStorageLocation);
        } catch (Exception ex) {
            log.error("Failed to create upload directory at: {}", this.fileStorageLocation, ex);
            throw new RuntimeException("Could not create upload directory at: " + this.fileStorageLocation, ex);
        }
    }

    public String storeFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                : "";
        String fileName = UUID.randomUUID().toString() + fileExtension;

        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            log.debug("Storing file: {} to location: {}", originalFilename, targetLocation);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("Successfully stored file: {} as {}", originalFilename, fileName);
            return fileName;
        } catch (IOException ex) {
            log.error("Failed to store file: {} at location: {}", fileName, this.fileStorageLocation.resolve(fileName), ex);
            throw new RuntimeException("Could not store file " + fileName + " at " + this.fileStorageLocation, ex);
        }
    }

    public Path loadFile(String fileName) {
        Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
        log.debug("Loading file: {} from location: {}", fileName, filePath);
        
        if (!Files.exists(filePath)) {
            log.error("File not found: {} at location: {}", fileName, filePath);
            throw new RuntimeException("File not found: " + fileName + " at location: " + filePath);
        }
        
        if (!Files.isReadable(filePath)) {
            log.error("File not readable: {} at location: {}", fileName, filePath);
            throw new RuntimeException("File not readable: " + fileName + " at location: " + filePath);
        }
        
        return filePath;
    }

    public void deleteFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            log.debug("Attempting to delete file: {} at location: {}", fileName, filePath);
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                log.info("Successfully deleted file: {}", fileName);
            } else {
                log.warn("File not found for deletion: {} at location: {}", fileName, filePath);
            }
        } catch (IOException ex) {
            log.error("Failed to delete file: {} at location: {}", fileName, this.fileStorageLocation.resolve(fileName), ex);
            throw new RuntimeException("Could not delete file " + fileName + " at " + this.fileStorageLocation, ex);
        }
    }

    public boolean isValidFileType(String contentType) {
        return contentType != null && (
                contentType.equals("application/pdf") ||
                contentType.equals("application/msword") ||
                contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
                contentType.equals("image/jpeg") ||
                contentType.equals("image/png")
        );
    }
}
