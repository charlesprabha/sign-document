package com.charles.sign_document.service;
//
//public class DocumentService {
//
//}

//package com.charles.signdocument.service;

import com.charles.sign_document.dto.UploadResponse;
import com.charles.sign_document.exception.DocumentNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class DocumentService {

    private final Path uploadDirectory =
            Paths.get("uploads");

    public DocumentService() {

        try {

            Files.createDirectories(uploadDirectory);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Could not create upload directory", e);
        }
    }

    public UploadResponse uploadDocument(MultipartFile file)
            throws IOException {

        // Check empty file
        if (file.isEmpty()) {

            throw new IllegalArgumentException(
                    "Please select a document");
        }

        // Check file size
        if (file.getSize() > 5 * 1024 * 1024) {

            throw new IllegalArgumentException(
                    "File size must not exceed 5 MB");
        }

        // Check PDF
        String contentType = file.getContentType();

        if (!"application/pdf".equalsIgnoreCase(contentType)) {

            throw new IllegalArgumentException(
                    "Only PDF files are supported");
        }

        // Generate unique ID
        String documentId = UUID.randomUUID().toString();

        String originalFileName = file.getOriginalFilename();

        String storedFileName =
                documentId + ".pdf";

        Path filePath =
                uploadDirectory.resolve(storedFileName);

        // Save file
        Files.copy(
                file.getInputStream(),
                filePath
        );

        return new UploadResponse(
                documentId,
                originalFileName,
                "Document uploaded successfully"
        );
    }

    public Path getDocumentPath(String documentId) {

        Path path = uploadDirectory.resolve(
                documentId + ".pdf"
        );

        if (!Files.exists(path)) {

            throw new DocumentNotFoundException(
                    "Document not found: " + documentId);
        }

        return path;
    }
}