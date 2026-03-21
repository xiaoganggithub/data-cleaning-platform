package com.ruoyi.system.domain.entity;

/**
 * 领域异常
 */
public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
