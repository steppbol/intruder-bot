package com.ffanaticism.intruder.serviceprovider.exception;

public class NotSupportedPlatformException extends Exception {
    public NotSupportedPlatformException() {
        super("Platform is not supported");
    }
}
