package com.voltz.dao;

import com.voltz.factory.ConnectionFactory;
import com.voltz.model.Relatorio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** DAO da tabela RELATORIO. */
public class RelatorioDAO implements CrudDAO<Relatorio> {

    private static final String COLUNAS = "id_relatorio, id_usuario, tipo_relatorio, data_geracao";

    @Override
    public Relatorio inserir(Relatorio r) throws SQLException {
        try (Connection c = ConnectionFactory.getConnection()) {
            return inserir(c, r);
        }
    }

    /** INSERT dentro de uma conexão/transação já aberta. O id é gerado pelo banco (IDENTITY). */
    public Relatorio inserir(Connection c, Relatorio r) throws SQLException {
        if (r.getDataGeracao() == null) r.setDataGeracao(LocalDateTime.now());
        String sql = "INSERT INTO relatorio (id_usuario, tipo_relatorio, data_geracao) VALUES (?, ?, ?)";
        // Informar a coluna de PK faz o Oracle devolver o id gerado (e não o ROWID).
        try (PreparedStatement stmt = c.prepareStatement(sql, new String[]{"ID_RELATORIO"})) {
            stmt.setInt(1, r.getIdUsuario());
            stmt.setString(2, r.getTipoRelatorio());
            stmt.setTimestamp(3, Timestamp.valueOf(r.getDataGeracao()));
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) r.setIdRelatorio(rs.getInt(1));
            }
        }
        return r;
    }

    @Override
    public boolean atualizar(Relatorio r) throws SQLException {
        String sql = "UPDATE relatorio SET id_usuario = ?, tipo_relatorio = ?, data_geracao = ? "
                   + "WHERE id_relatorio = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setInt(1, r.getIdUsuario());
            stmt.setString(2, r.getTipoRelatorio());
            stmt.setTimestamp(3, Timestamp.valueOf(r.getDataGeracao()));
            stmt.setInt(4, r.getIdRelatorio());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean excluir(int id) throws SQLException {
        String sql = "DELETE FROM relatorio WHERE id_relatorio = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Relatorio buscarPorId(int id) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM relatorio WHERE id_relatorio = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    @Override
    public List<Relatorio> listarTodos() throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM relatorio ORDER BY id_relatorio";
        List<Relatorio> lista = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    /** SELECT dos relatórios gerados por um usuário. */
    public List<Relatorio> listarPorUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM relatorio WHERE id_usuario = ? ORDER BY data_geracao DESC";
        List<Relatorio> lista = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private Relatorio mapear(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("data_geracao");
        return new Relatorio(rs.getInt("id_relatorio"), rs.getInt("id_usuario"),
                rs.getString("tipo_relatorio"), ts != null ? ts.toLocalDateTime() : null);
    }
}
