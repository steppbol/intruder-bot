package com.ffanaticism.intruder.serviceprovider.exception;

public class MemoryLimitExceededException extends Exception {
    public MemoryLimitExceededException() {
        super("Memory limit exceeded for stored files");
    }
}
