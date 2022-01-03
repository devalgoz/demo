package com.upstreampay.demo.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super("Not found");
    }
}
