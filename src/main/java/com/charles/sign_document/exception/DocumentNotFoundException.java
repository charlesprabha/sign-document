package com.charles.sign_document.exception;
//
//public class DocumentNotFoundException {
//
//}

//package com.charles.signdocument.exception;

public class DocumentNotFoundException extends RuntimeException {

    public DocumentNotFoundException(String message) {
        super(message);
    }
}