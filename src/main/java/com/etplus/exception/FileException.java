package com.etplus.exception;

import com.etplus.common.CustomBadRequestException;
import com.etplus.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class FileException extends CustomBadRequestException {

    @AllArgsConstructor
    @Getter
    public enum FileExceptionCode implements ResponseCode {
        INVALID_FILE_EXTENSION("FE-001", "Invalid file extension. Allowed types: PDF, JPEG, JPG, PNG"),
        FILE_SIZE_EXCEEDED("FE-002", "File size exceeds the maximum limit of 5MB"),
        ;

        private String code;
        private String message;
    }

    public FileException(ResponseCode responseCode) {
        super(responseCode);
    }
}
