package com.voltz.dao;

import com.voltz.factory.ConnectionFactory;
import com.voltz.model.Carteira;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** DAO da tabela CARTEIRA. Possui métodos que aceitam Connection para uso em transações. */
public class CarteiraDAO implements CrudDAO<Carteira> {

    private static final String COLUNAS = "id_carteira, id_usuario, saldo_reais";

    @Override
    public Carteira inserir(Carteira carteira) throws SQLException {
        try (Connection c = ConnectionFactory.getConnection()) {
            return inserir(c, carteira);
        }
    }

    /** INSERT dentro de uma conexão/transação já aberta. O id é gerado pelo banco (IDENTITY). */
    public Carteira inserir(Connection c, Carteira carteira) throws SQLException {
        String sql = "INSERT INTO carteira (id_usuario, saldo_reais) VALUES (?, ?)";
        // Informar a coluna de PK faz o Oracle devolver o id gerado (e não o ROWID).
        try (PreparedStatement stmt = c.prepareStatement(sql, new String[]{"ID_CARTEIRA"})) {
            stmt.setInt(1, carteira.getIdUsuario());
            stmt.setBigDecimal(2, carteira.getSaldoReais());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) carteira.setIdCarteira(rs.getInt(1));
            }
        }
        return carteira;
    }

    @Override
    public boolean atualizar(Carteira carteira) throws SQLException {
        String sql = "UPDATE carteira SET id_usuario = ?, saldo_reais = ? WHERE id_carteira = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setInt(1, carteira.getIdUsuario());
            stmt.setBigDecimal(2, carteira.getSaldoReais());
            stmt.setInt(3, carteira.getIdCarteira());
            return stmt.executeUpdate() > 0;
        }
    }

    /** UPDATE apenas do saldo, dentro de uma transação já aberta. */
    public boolean atualizarSaldo(Connection c, int idCarteira, BigDecimal novoSaldo) throws SQLException {
        String sql = "UPDATE carteira SET saldo_reais = ? WHERE id_carteira = ?";
        try (PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setBigDecimal(1, novoSaldo);
            stmt.setInt(2, idCarteira);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean excluir(int id) throws SQLException {
        String sql = "DELETE FROM carteira WHERE id_carteira = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Carteira buscarPorId(int id) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM carteira WHERE id_carteira = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    /** SELECT ... FOR UPDATE: trava a linha até o commit/rollback da transação. */
    public Carteira buscarPorIdParaAtualizar(Connection c, int id) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM carteira WHERE id_carteira = ? FOR UPDATE";
        try (PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    @Override
    public List<Carteira> listarTodos() throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM carteira ORDER BY id_carteira";
        List<Carteira> lista = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    private Carteira mapear(ResultSet rs) throws SQLException {
        return new Carteira(rs.getInt("id_carteira"), rs.getInt("id_usuario"), rs.getBigDecimal("saldo_reais"));
    }
}
