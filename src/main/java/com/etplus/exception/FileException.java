package com.etplus.exception;

import com.etplus.common.CustomBadRequestException;
import com.etplus.common.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class FileException extends CustomBadRequestException {

    @AllArgsConstructor
    @Getter
    public enum FileExceptionCode implements ResponseCode {
        INVALID_FILE_EXTENSION("FE-001", "invalid file extension"),
        ;

        private String code;
        private String message;
    }

    public FileException(ResponseCode responseCode) {
        super(responseCode);
    }
}
