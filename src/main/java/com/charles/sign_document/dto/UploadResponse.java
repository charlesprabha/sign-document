package com.charles.sign_document.dto;
//
//public class UploadResponse {
//
//}

//package com.charles.signdocument.dto;

public class UploadResponse {

    private String documentId;
    private String fileName;
    private String message;

    public UploadResponse() {
    }

    public UploadResponse(String documentId, String fileName, String message) {
        this.documentId = documentId;
        this.fileName = fileName;
        this.message = message;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}