package com.voltz.service;

import com.voltz.dao.CarteiraDAO;
import com.voltz.dao.CriptoAtivoDAO;
import com.voltz.dao.TransacaoDAO;
import com.voltz.factory.ConnectionFactory;
import com.voltz.model.Carteira;
import com.voltz.model.CriptoAtivo;
import com.voltz.model.Transacao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Registra uma compra ou venda de forma ATÔMICA: grava a Transacao e atualiza
 * o saldo da Carteira na mesma transação JDBC (commit se tudo der certo,
 * rollback se qualquer passo falhar).
 */
public class OperacaoService {

    private final CarteiraDAO carteiraDAO = new CarteiraDAO();
    private final CriptoAtivoDAO criptoAtivoDAO = new CriptoAtivoDAO();
    private final TransacaoDAO transacaoDAO = new TransacaoDAO();

    public Transacao registrarOperacao(int idCarteira, int idCripto, String tipoOperacao, BigDecimal quantidade)
            throws SQLException, OperacaoInvalidaException {

        if (quantidade == null || quantidade.signum() <= 0) {
            throw new OperacaoInvalidaException("A quantidade deve ser maior que zero.");
        }
        if (!Transacao.COMPRA.equals(tipoOperacao) && !Transacao.VENDA.equals(tipoOperacao)) {
            throw new OperacaoInvalidaException("Tipo de operação inválido: " + tipoOperacao);
        }

        try (Connection c = ConnectionFactory.getConnection()) {
            c.setAutoCommit(false);
            try {
                Carteira carteira = carteiraDAO.buscarPorIdParaAtualizar(c, idCarteira);
                if (carteira == null) {
                    throw new OperacaoInvalidaException("Carteira " + idCarteira + " não encontrada.");
                }
                CriptoAtivo ativo = criptoAtivoDAO.buscarPorId(c, idCripto);
                if (ativo == null) {
                    throw new OperacaoInvalidaException("Criptoativo " + idCripto + " não encontrado.");
                }

                Transacao transacao = new Transacao(idCarteira, idCripto, tipoOperacao,
                        quantidade, ativo.getValorAtual(), LocalDateTime.now());
                BigDecimal total = transacao.calcularValorTotal();

                if (Transacao.COMPRA.equals(tipoOperacao)) {
                    try {
                        carteira.debitar(total);
                    } catch (IllegalStateException e) {
                        throw new OperacaoInvalidaException(e.getMessage());
                    }
                } else {
                    BigDecimal posicao = transacaoDAO.posicaoDoAtivo(c, idCarteira, idCripto);
                    if (posicao.compareTo(quantidade) < 0) {
                        throw new OperacaoInvalidaException(String.format(
                                "Posição insuficiente de %s: possui %s, tentou vender %s",
                                ativo.getSimbolo(), posicao.stripTrailingZeros().toPlainString(),
                                quantidade.stripTrailingZeros().toPlainString()));
                    }
                    carteira.creditar(total);
                }

                transacaoDAO.inserir(c, transacao);
                carteiraDAO.atualizarSaldo(c, idCarteira, carteira.getSaldoReais());

                c.commit();
                return transacao;

            } catch (SQLException | OperacaoInvalidaException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }
}
