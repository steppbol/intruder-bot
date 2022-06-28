package com.ffanaticism.intruder.serviceprovider.exception;

public class NotDownloadableException extends Exception {
    public NotDownloadableException() {
        super("File is not downloadable");
    }
}
