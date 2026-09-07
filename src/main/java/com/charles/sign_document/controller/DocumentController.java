package com.charles.sign_document.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.charles.sign_document.dto.UploadResponse;
import com.charles.sign_document.service.DocumentService;
import com.charles.sign_document.service.PdfSignService;

import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "https://sign-document-frontend.onrender.com")
public class DocumentController {

    private final DocumentService documentService;

    private final PdfSignService pdfSigningService;
    
    private static final Logger logger =
            LoggerFactory.getLogger(DocumentController.class);

    public DocumentController(
            DocumentService documentService,
            PdfSignService pdfSigningService) {

        this.documentService = documentService;
        this.pdfSigningService = pdfSigningService;
    }

    // ==========================================
    // 1. UPLOAD DOCUMENT
    // ==========================================

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UploadResponse> uploadDocument(
            @RequestParam("file") MultipartFile file)
            throws Exception {
    	logger.info("UPLOAD API CALLED - file: {}", file.getOriginalFilename()); 

        UploadResponse response =
                documentService.uploadDocument(file);

        return ResponseEntity.ok(response);
    }


    // ==========================================
    // 2. SIGN DOCUMENT
    // ==========================================

    @PostMapping(
            value = "/{documentId}/sign",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Resource> signDocument(

            @PathVariable String documentId,

            @RequestParam("signature")
            MultipartFile signature,

            @RequestParam("page")
            int page,

            @RequestParam("x")
            float x,

            @RequestParam("y")
            float y,

            @RequestParam("width")
            float width,

            @RequestParam("height")
            float height

    ) throws Exception {

    	    logger.info("SIGN API CALLED");
    	    logger.info("Document ID: {}", documentId);
    	    logger.info("Page: {}", page);
    	    logger.info("X: {}", x);
    	    logger.info("Y: {}", y);
    	    logger.info("Width: {}", width);
    	    logger.info("Height: {}", height);
    	    logger.info("Signature file: {}", signature.getOriginalFilename());
    	
        // Get original PDF
    	    
    	    logger.info("Signature file: {}", signature.getOriginalFilename());
    	    logger.info("BEFORE GET DOCUMENT PATH");
    	    
        Path pdfPath =
                documentService.getDocumentPath(documentId);
        
        logger.info("AFTER GET DOCUMENT PATH");
        logger.info("PDF PATH: {}", pdfPath);
        // Add signature
        Path signedPath =
                pdfSigningService.signDocument(
                        pdfPath,
                        signature,
                        page,
                        x,
                        y,
                        width,
                        height
                );

        Resource resource =
                new FileSystemResource(signedPath);

        return ResponseEntity.ok()
                .contentType(
                        MediaType.APPLICATION_PDF
                )
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"signed-document.pdf\""
                )
                .body(resource);
    }


    // ==========================================
    // 3. PREVIEW / GET PDF
    // ==========================================

    @GetMapping("/{documentId}")
    public ResponseEntity<Resource> getDocument(
            @PathVariable String documentId) {

        Path path =
                documentService.getDocumentPath(documentId);

        Resource resource =
                new FileSystemResource(path);

        return ResponseEntity.ok()
                .contentType(
                        MediaType.APPLICATION_PDF
                )
                .body(resource);
    }
}