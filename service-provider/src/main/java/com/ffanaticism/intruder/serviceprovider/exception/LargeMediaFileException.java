package com.ffanaticism.intruder.serviceprovider.exception;

public class LargeMediaFileException extends Exception {
    public LargeMediaFileException() {
        super("File too large to save");
    }
}
