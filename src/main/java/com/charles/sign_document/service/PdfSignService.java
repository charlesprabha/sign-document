package com.charles.sign_document.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

@Service
public class PdfSignService {

    private static final Logger logger =
            LoggerFactory.getLogger(PdfSignService.class);

    private final Path uploadDirectory =
            Path.of("uploads");

    public Path signDocument(
            Path pdfPath,
            MultipartFile signature,
            int pageNumber,
            float x,
            float y,
            float width,
            float height) throws IOException {

        logger.info("========== PDF SIGN SERVICE ==========");
        logger.info("PDF Path: {}", pdfPath);
        logger.info("PDF exists: {}", Files.exists(pdfPath));
        logger.info("Signature name: {}", signature.getOriginalFilename());
        logger.info("Signature size: {}", signature.getSize());

        // Make sure uploads directory exists
        Files.createDirectories(uploadDirectory);

        try (PDDocument document =
                     Loader.loadPDF(pdfPath.toFile())) {

            logger.info("PDF loaded successfully");
            logger.info("Number of pages: {}",
                    document.getNumberOfPages());
            
            // ADDED — remove encryption so document.save() won't throw later
            if (document.isEncrypted()) {
                logger.info("PDF is encrypted, removing security restrictions");
                document.setAllSecurityToBeRemoved(true);
            }

            // Validate page
            if (pageNumber < 1 ||
                    pageNumber > document.getNumberOfPages()) {

                throw new IllegalArgumentException(
                        "Invalid page number: " + pageNumber);
            }

            PDPage page =
                    document.getPage(pageNumber - 1);

            logger.info("Page selected: {}", pageNumber);

            // Read signature image
            BufferedImage signatureImage;

            try (InputStream inputStream =
                         signature.getInputStream()) {

                signatureImage =
                        ImageIO.read(inputStream);
            }

            if (signatureImage == null) {

                throw new IllegalArgumentException(
                        "Invalid signature image");
            }

            logger.info(
                    "Signature image loaded: {} x {}",
                    signatureImage.getWidth(),
                    signatureImage.getHeight()
            );

            // Create PDF image
            PDImageXObject pdImage =
                    PDImageXObject.createFromByteArray(
                            document,
                            signature.getBytes(),
                            "signature"
                    );

            logger.info("Signature converted to PDF image");

            // Add signature to PDF
            try (PDPageContentStream contentStream =
                         new PDPageContentStream(
                                 document,
                                 page,
                                 PDPageContentStream.AppendMode.APPEND,
                                 true,
                                 true)) {

                contentStream.drawImage(
                        pdImage,
                        x,
                        y,
                        width,
                        height
                );
            }

            logger.info("Signature drawn on PDF");

            // Create signed filename
            String originalFileName =
                    pdfPath.getFileName().toString();

            String signedFileName =
                    originalFileName.replace(
                            ".pdf",
                            "_signed.pdf"
                    );

            Path signedPath =
                    uploadDirectory.resolve(
                            signedFileName
                    );
            

            logger.info("Saving signed PDF to: {}",
                    signedPath);

            logger.info("BEFORE SAVE");
            document.save(signedPath.toFile());

            logger.info("AFTER SAVE");
            logger.info("Signed PDF exists: {}", Files.exists(signedPath));
            logger.info("Signed PDF size: {}", Files.size(signedPath));

            return signedPath;
        }
    }
}