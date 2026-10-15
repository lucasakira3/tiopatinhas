package com.voltz.dao;

import com.voltz.factory.ConnectionFactory;
import com.voltz.model.Permissao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** DAO da tabela PERMISSAO. */
public class PermissaoDAO implements CrudDAO<Permissao> {

    @Override
    public Permissao inserir(Permissao p) throws SQLException {
        try (Connection c = ConnectionFactory.getConnection()) {
            return inserir(c, p);
        }
    }

    /** INSERT dentro de uma conexão/transação já aberta. O id é gerado pelo banco (IDENTITY). */
    public Permissao inserir(Connection c, Permissao p) throws SQLException {
        String sql = "INSERT INTO permissao (nome, descricao) VALUES (?, ?)";
        // Informar a coluna de PK faz o Oracle devolver o id gerado (e não o ROWID).
        try (PreparedStatement stmt = c.prepareStatement(sql, new String[]{"ID_PERMISSAO"})) {
            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getDescricao());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) p.setIdPermissao(rs.getInt(1));
            }
        }
        return p;
    }

    @Override
    public boolean atualizar(Permissao p) throws SQLException {
        String sql = "UPDATE permissao SET nome = ?, descricao = ? WHERE id_permissao = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getDescricao());
            stmt.setInt(3, p.getIdPermissao());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean excluir(int id) throws SQLException {
        String sql = "DELETE FROM permissao WHERE id_permissao = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Permissao buscarPorId(int id) throws SQLException {
        String sql = "SELECT id_permissao, nome, descricao FROM permissao WHERE id_permissao = ?";
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
    public List<Permissao> listarTodos() throws SQLException {
        String sql = "SELECT id_permissao, nome, descricao FROM permissao ORDER BY id_permissao";
        List<Permissao> lista = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    private Permissao mapear(ResultSet rs) throws SQLException {
        return new Permissao(rs.getInt("id_permissao"), rs.getString("nome"), rs.getString("descricao"));
    }
}


