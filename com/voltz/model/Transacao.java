package com.voltz.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Entidade associativa entre CARTEIRA e CRIPTO_ATIVO. Espelha a tabela TRANSACAO.
 * O campo "ativo" só vem preenchido quando o DAO faz JOIN com cripto_ativo.
 */
public class Transacao {

    public static final String COMPRA = "COMPRA";
    public static final String VENDA = "VENDA";

    private int idTransacao;
    private int idCarteira;
    private int idCripto;
    private String tipoOperacao; // COMPRA ou VENDA
    private BigDecimal quantidade;
    private BigDecimal valorUnidade;
    private LocalDateTime dataTransacao;
    private CriptoAtivo ativo; // opcional (JOIN)

    public Transacao(int idCarteira, int idCripto, String tipoOperacao,
                     BigDecimal quantidade, BigDecimal valorUnidade, LocalDateTime dataTransacao) {
        this(0, idCarteira, idCripto, tipoOperacao, quantidade, valorUnidade, dataTransacao);
    }

    public Transacao(int idTransacao, int idCarteira, int idCripto, String tipoOperacao,
                     BigDecimal quantidade, BigDecimal valorUnidade, LocalDateTime dataTransacao) {
        this.idTransacao = idTransacao;
        this.idCarteira = idCarteira;
        this.idCripto = idCripto;
        this.tipoOperacao = tipoOperacao;
        this.quantidade = quantidade;
        this.valorUnidade = valorUnidade;
        this.dataTransacao = dataTransacao;
    }

    public BigDecimal calcularValorTotal() {
        return quantidade.multiply(valorUnidade).setScale(2, RoundingMode.HALF_UP);
    }

    public int getIdTransacao() { return idTransacao; }
    public void setIdTransacao(int idTransacao) { this.idTransacao = idTransacao; }

    public int getIdCarteira() { return idCarteira; }
    public void setIdCarteira(int idCarteira) { this.idCarteira = idCarteira; }

    public int getIdCripto() { return idCripto; }
    public void setIdCripto(int idCripto) { this.idCripto = idCripto; }

    public String getTipoOperacao() { return tipoOperacao; }
    public void setTipoOperacao(String tipoOperacao) { this.tipoOperacao = tipoOperacao; }

    public BigDecimal getQuantidade() { return quantidade; }
    public void setQuantidade(BigDecimal quantidade) { this.quantidade = quantidade; }

    public BigDecimal getValorUnidade() { return valorUnidade; }
    public void setValorUnidade(BigDecimal valorUnidade) { this.valorUnidade = valorUnidade; }

    public LocalDateTime getDataTransacao() { return dataTransacao; }
    public void setDataTransacao(LocalDateTime dataTransacao) { this.dataTransacao = dataTransacao; }

    public CriptoAtivo getAtivo() { return ativo; }
    public void setAtivo(CriptoAtivo ativo) { this.ativo = ativo; }

    @Override
    public String toString() {
        return String.format("Transação [%d] %s | carteira %d | %s | Qtd: %s x R$ %.2f = R$ %.2f | %s",
                idTransacao, tipoOperacao, idCarteira,
                ativo != null ? ativo.getSimbolo() : "cripto " + idCripto,
                quantidade.stripTrailingZeros().toPlainString(), valorUnidade,
                calcularValorTotal(), dataTransacao);
    }
}
