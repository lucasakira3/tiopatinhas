package com.voltz.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Espelha a tabela CRIPTO_ATIVO (id_cripto, nome, sigla, cotacao_atual). */
public class CriptoAtivo {

    private int id;
    private String nome;
    private String simbolo;
    private BigDecimal valorAtual; // cotacao_atual

    public CriptoAtivo(String nome, String simbolo, BigDecimal valorAtual) {
        this(0, nome, simbolo, valorAtual);
    }

    public CriptoAtivo(int id, String nome, String simbolo, BigDecimal valorAtual) {
        this.id = id;
        this.nome = nome;
        this.simbolo = simbolo;
        this.valorAtual = valorAtual;
    }

    /** Valor de mercado de uma certa quantidade deste ativo. */
    public BigDecimal calcularValorTotal(BigDecimal quantidade) {
        return quantidade.multiply(valorAtual).setScale(2, RoundingMode.HALF_UP);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getSimbolo() { return simbolo; }
    public void setSimbolo(String simbolo) { this.simbolo = simbolo; }

    public BigDecimal getValorAtual() { return valorAtual; }
    public void setValorAtual(BigDecimal valorAtual) { this.valorAtual = valorAtual; }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s) | Cotação: R$ %.2f", id, nome, simbolo, valorAtual);
    }
}
