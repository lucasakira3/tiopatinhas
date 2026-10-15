package com.voltz.dao;

import com.voltz.factory.ConnectionFactory;
import com.voltz.model.CriptoAtivo;
import com.voltz.model.Transacao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO da tabela TRANSACAO. Além do CRUD, oferece consultas com JOIN
 * (por carteira e por usuário) e o cálculo da posição de um ativo na carteira.
 */
public class TransacaoDAO implements CrudDAO<Transacao> {

    private static final String COLUNAS =
            "id_transacao, id_carteira, id_cripto, tipo_operacao, quantidade, valor_unidade, data_transacao";

    private static final String SELECT_JOIN =
            "SELECT t.id_transacao, t.id_carteira, t.id_cripto, t.tipo_operacao, t.quantidade, "
          + "       t.valor_unidade, t.data_transacao, "
          + "       ca.nome AS cripto_nome, ca.sigla, ca.cotacao_atual "
          + "FROM transacao t "
          + "INNER JOIN cripto_ativo ca ON t.id_cripto = ca.id_cripto ";

    @Override
    public Transacao inserir(Transacao t) throws SQLException {
        try (Connection c = ConnectionFactory.getConnection()) {
            return inserir(c, t);
        }
    }

    /** INSERT dentro de uma conexão/transação já aberta. O id é gerado pelo banco (IDENTITY). */
    public Transacao inserir(Connection c, Transacao t) throws SQLException {
        if (t.getDataTransacao() == null) t.setDataTransacao(LocalDateTime.now());
        String sql = "INSERT INTO transacao (id_carteira, id_cripto, tipo_operacao, quantidade, valor_unidade, data_transacao) VALUES (?, ?, ?, ?, ?, ?)";
        // Informar a coluna de PK faz o Oracle devolver o id gerado (e não o ROWID).
        try (PreparedStatement stmt = c.prepareStatement(sql, new String[]{"ID_TRANSACAO"})) {
            stmt.setInt(1, t.getIdCarteira());
            stmt.setInt(2, t.getIdCripto());
            stmt.setString(3, t.getTipoOperacao());
            stmt.setBigDecimal(4, t.getQuantidade());
            stmt.setBigDecimal(5, t.getValorUnidade());
            stmt.setTimestamp(6, Timestamp.valueOf(t.getDataTransacao()));
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) t.setIdTransacao(rs.getInt(1));
            }
        }
        return t;
    }

    @Override
    public boolean atualizar(Transacao t) throws SQLException {
        String sql = "UPDATE transacao SET id_carteira = ?, id_cripto = ?, tipo_operacao = ?, "
                   + "quantidade = ?, valor_unidade = ?, data_transacao = ? WHERE id_transacao = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setInt(1, t.getIdCarteira());
            stmt.setInt(2, t.getIdCripto());
            stmt.setString(3, t.getTipoOperacao());
            stmt.setBigDecimal(4, t.getQuantidade());
            stmt.setBigDecimal(5, t.getValorUnidade());
            stmt.setTimestamp(6, Timestamp.valueOf(t.getDataTransacao()));
            stmt.setInt(7, t.getIdTransacao());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean excluir(int id) throws SQLException {
        String sql = "DELETE FROM transacao WHERE id_transacao = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /** DELETE de todas as transações de uma carteira (retorna quantas foram removidas). */
    public int excluirPorCarteira(int idCarteira) throws SQLException {
        String sql = "DELETE FROM transacao WHERE id_carteira = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setInt(1, idCarteira);
            return stmt.executeUpdate();
        }
    }

    @Override
    public Transacao buscarPorId(int id) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM transacao WHERE id_transacao = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs, false);
            }
        }
        return null;
    }

    @Override
    public List<Transacao> listarTodos() throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM transacao ORDER BY id_transacao";
        List<Transacao> lista = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs, false));
        }
        return lista;
    }

    /** SELECT com JOIN em cripto_ativo: histórico de uma carteira, mais recente primeiro. */
    public List<Transacao> listarPorCarteira(int idCarteira) throws SQLException {
        String sql = SELECT_JOIN + "WHERE t.id_carteira = ? ORDER BY t.data_transacao DESC, t.id_transacao DESC";
        List<Transacao> lista = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setInt(1, idCarteira);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs, true));
            }
        }
        return lista;
    }

    /** SELECT com JOIN em carteira e cripto_ativo: todas as transações de um usuário. */
    public List<Transacao> listarPorUsuario(int idUsuario) throws SQLException {
        String sql = SELECT_JOIN
                   + "INNER JOIN carteira cart ON t.id_carteira = cart.id_carteira "
                   + "WHERE cart.id_usuario = ? ORDER BY t.data_transacao DESC, t.id_transacao DESC";
        List<Transacao> lista = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs, true));
            }
        }
        return lista;
    }

    /** Posição atual: soma das COMPRAS menos soma das VENDAS de um ativo na carteira. */
    public BigDecimal posicaoDoAtivo(Connection c, int idCarteira, int idCripto) throws SQLException {
        String sql = "SELECT NVL(SUM(CASE WHEN tipo_operacao = 'COMPRA' THEN quantidade ELSE -quantidade END), 0) "
                   + "FROM transacao WHERE id_carteira = ? AND id_cripto = ?";
        try (PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setInt(1, idCarteira);
            stmt.setInt(2, idCripto);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getBigDecimal(1);
            }
        }
    }

    private Transacao mapear(ResultSet rs, boolean comAtivo) throws SQLException {
        Timestamp ts = rs.getTimestamp("data_transacao");
        Transacao t = new Transacao(
                rs.getInt("id_transacao"),
                rs.getInt("id_carteira"),
                rs.getInt("id_cripto"),
                rs.getString("tipo_operacao"),
                rs.getBigDecimal("quantidade"),
                rs.getBigDecimal("valor_unidade"),
                ts != null ? ts.toLocalDateTime() : null);
        if (comAtivo) {
            t.setAtivo(new CriptoAtivo(rs.getInt("id_cripto"), rs.getString("cripto_nome"),
                    rs.getString("sigla"), rs.getBigDecimal("cotacao_atual")));
        }
        return t;
    }
}
