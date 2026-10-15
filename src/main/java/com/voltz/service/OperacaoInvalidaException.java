package com.voltz.service;

/** Violação de regra de negócio (saldo insuficiente, posição insuficiente, dados inexistentes...). */
public class OperacaoInvalidaException extends Exception {

    private static final long serialVersionUID = 1L;

    public OperacaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
