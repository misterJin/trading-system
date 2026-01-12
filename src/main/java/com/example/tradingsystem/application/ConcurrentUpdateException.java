package com.example.tradingsystem.application;

/**
 * 并发更新异常
 */
public class ConcurrentUpdateException extends BusinessException {

    public ConcurrentUpdateException(String message) {
        super(message);
    }
}
