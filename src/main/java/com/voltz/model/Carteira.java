package com.voltz.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Espelha a tabela CARTEIRA (id_carteira, id_usuario, saldo_reais). */
public class Carteira {

    private int idCarteira;
    private int idUsuario;
    private BigDecimal saldoReais;
    private List<Transacao> transacoes = new ArrayList<>(); // apoio em memória (não é coluna)

    public Carteira(int idUsuario, BigDecimal saldoReais) {
        this(0, idUsuario, saldoReais);
    }

    public Carteira(int idCarteira, int idUsuario, BigDecimal saldoReais) {
        this.idCarteira = idCarteira;
        this.idUsuario = idUsuario;
        this.saldoReais = saldoReais;
    }

    public void creditar(BigDecimal valor) {
        if (valor.signum() <= 0) throw new IllegalArgumentException("Valor deve ser positivo.");
        saldoReais = saldoReais.add(valor);
    }

    public void debitar(BigDecimal valor) {
        if (valor.signum() <= 0) throw new IllegalArgumentException("Valor deve ser positivo.");
        if (saldoReais.compareTo(valor) < 0) {
            throw new IllegalStateException(String.format(
                    "Saldo insuficiente: disponível R$ %.2f, necessário R$ %.2f", saldoReais, valor));
        }
        saldoReais = saldoReais.subtract(valor);
    }

    public void adicionarTransacao(Transacao transacao) {
        transacoes.add(transacao);
    }

    public int getIdCarteira() { return idCarteira; }
    public void setIdCarteira(int idCarteira) { this.idCarteira = idCarteira; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public BigDecimal getSaldoReais() { return saldoReais; }
    public void setSaldoReais(BigDecimal saldoReais) { this.saldoReais = saldoReais; }

    public List<Transacao> getTransacoes() { return transacoes; }
    public void setTransacoes(List<Transacao> transacoes) { this.transacoes = transacoes; }

    @Override
    public String toString() {
        return String.format("Carteira [%d] usuário %d | Saldo: R$ %.2f", idCarteira, idUsuario, saldoReais);
    }
}
