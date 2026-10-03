package com.lakshan.medical_records_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
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
            throw new RuntimeException("Could not initialize file storage", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String fileName = UUID.randomUUID().toString() + fileExtension;

        try {
            Path targetLocation = resolveWithinStorageRoot(fileName);
            log.debug("Storing file: {} to location: {}", originalFilename, targetLocation);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("Successfully stored file: {} as {}", originalFilename, fileName);
            return fileName;
        } catch (IOException ex) {
            log.error("Failed to store file: {} at location: {}", fileName, this.fileStorageLocation.resolve(fileName), ex);
            throw new RuntimeException("Could not store the uploaded file", ex);
        }
    }

    public Path loadFile(String fileName) {
        Path filePath = resolveWithinStorageRoot(fileName);
        log.debug("Loading file: {} from location: {}", fileName, filePath);

        if (!Files.exists(filePath)) {
            log.error("File not found: {} at location: {}", fileName, filePath);
            throw new RuntimeException("Stored file is missing");
        }

        if (!Files.isReadable(filePath)) {
            log.error("File not readable: {} at location: {}", fileName, filePath);
            throw new RuntimeException("Stored file is not readable");
        }

        return filePath;
    }

    public void deleteFile(String fileName) {
        try {
            Path filePath = resolveWithinStorageRoot(fileName);
            log.debug("Attempting to delete file: {} at location: {}", fileName, filePath);
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                log.info("Successfully deleted file: {}", fileName);
            } else {
                log.warn("File not found for deletion: {} at location: {}", fileName, filePath);
            }
        } catch (IOException ex) {
            log.error("Failed to delete file: {} at location: {}", fileName, this.fileStorageLocation.resolve(fileName), ex);
            throw new RuntimeException("Could not delete the stored file", ex);
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

    public boolean contentMatchesDeclaredType(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }
        byte[] header;
        try (InputStream is = file.getInputStream()) {
            header = is.readNBytes(8);
        }
        return switch (contentType) {
            case "application/pdf" -> startsWith(header, 0x25, 0x50, 0x44, 0x46);
            case "image/png" -> startsWith(header, 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A);
            case "image/jpeg" -> startsWith(header, 0xFF, 0xD8, 0xFF);
            case "application/msword" -> startsWith(header, 0xD0, 0xCF, 0x11, 0xE0, 0xA1, 0xB1, 0x1A, 0xE1);
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document" ->
                    startsWith(header, 0x50, 0x4B, 0x03, 0x04);
            default -> false;
        };
    }

    private boolean startsWith(byte[] actual, int... expected) {
        if (actual.length < expected.length) {
            return false;
        }
        for (int i = 0; i < expected.length; i++) {
            if ((actual[i] & 0xFF) != expected[i]) {
                return false;
            }
        }
        return true;
    }

    private Path resolveWithinStorageRoot(String fileName) {
        Path resolved = this.fileStorageLocation.resolve(fileName).normalize();
        if (!resolved.startsWith(this.fileStorageLocation)) {
            log.error("Rejected file path outside storage root: {}", resolved);
            throw new SecurityException("Resolved path escapes the configured storage root");
        }
        return resolved;
    }
}
