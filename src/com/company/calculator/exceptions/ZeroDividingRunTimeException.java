package com.company.calculator.exceptions;

public class ZeroDividingRunTimeException extends RuntimeException {
    public ZeroDividingRunTimeException() {
        super("Невозможно деление на ноль");
    }
}
