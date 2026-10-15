package com.voltz.dao;

import com.voltz.factory.ConnectionFactory;
import com.voltz.model.Autenticacao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** DAO da tabela AUTENTICACAO. */
public class AutenticacaoDAO implements CrudDAO<Autenticacao> {

    private static final String COLUNAS = "id_autenticacao, id_usuario, token_sessao, data_login, ip_origem";

    @Override
    public Autenticacao inserir(Autenticacao a) throws SQLException {
        try (Connection c = ConnectionFactory.getConnection()) {
            return inserir(c, a);
        }
    }

    /** INSERT dentro de uma conexão/transação já aberta. O id é gerado pelo banco (IDENTITY). */
    public Autenticacao inserir(Connection c, Autenticacao a) throws SQLException {
        if (a.getDataLogin() == null) a.setDataLogin(LocalDateTime.now());
        String sql = "INSERT INTO autenticacao (id_usuario, token_sessao, data_login, ip_origem) VALUES (?, ?, ?, ?)";
        // Informar a coluna de PK faz o Oracle devolver o id gerado (e não o ROWID).
        try (PreparedStatement stmt = c.prepareStatement(sql, new String[]{"ID_AUTENTICACAO"})) {
            stmt.setInt(1, a.getIdUsuario());
            stmt.setString(2, a.getTokenSessao());
            stmt.setTimestamp(3, Timestamp.valueOf(a.getDataLogin()));
            stmt.setString(4, a.getIpOrigem());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) a.setIdAutenticacao(rs.getInt(1));
            }
        }
        return a;
    }

    @Override
    public boolean atualizar(Autenticacao a) throws SQLException {
        String sql = "UPDATE autenticacao SET id_usuario = ?, token_sessao = ?, data_login = ?, ip_origem = ? "
                   + "WHERE id_autenticacao = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setInt(1, a.getIdUsuario());
            stmt.setString(2, a.getTokenSessao());
            stmt.setTimestamp(3, Timestamp.valueOf(a.getDataLogin()));
            stmt.setString(4, a.getIpOrigem());
            stmt.setInt(5, a.getIdAutenticacao());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean excluir(int id) throws SQLException {
        String sql = "DELETE FROM autenticacao WHERE id_autenticacao = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Autenticacao buscarPorId(int id) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM autenticacao WHERE id_autenticacao = ?";
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
    public List<Autenticacao> listarTodos() throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM autenticacao ORDER BY id_autenticacao";
        List<Autenticacao> lista = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    /** SELECT dos acessos de um usuário, mais recentes primeiro. */
    public List<Autenticacao> listarPorUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM autenticacao WHERE id_usuario = ? ORDER BY data_login DESC";
        List<Autenticacao> lista = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private Autenticacao mapear(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("data_login");
        return new Autenticacao(rs.getInt("id_autenticacao"), rs.getInt("id_usuario"),
                rs.getString("token_sessao"), ts != null ? ts.toLocalDateTime() : null,
                rs.getString("ip_origem"));
    }
}
