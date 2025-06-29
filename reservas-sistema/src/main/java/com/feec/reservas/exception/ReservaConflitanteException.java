package com.feec.reservas.exception;

// Cumpre o requisito de: Tratamento de Exceções (customizada)
public class ReservaConflitanteException extends Exception {
    public ReservaConflitanteException(String message) {
        super(message);
    }
}
