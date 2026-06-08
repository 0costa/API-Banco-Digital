package com.banco_digital.api.controller.exception;

public class TransacaoDuplicadaException extends RuntimeException {
    public TransacaoDuplicadaException(String message) {
        super(message);
    }
}
