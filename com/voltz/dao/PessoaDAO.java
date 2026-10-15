package com.voltz.dao;

import com.voltz.factory.ConnectionFactory;
import com.voltz.model.Pessoa;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/** DAO da tabela PESSOA. */
public class PessoaDAO implements CrudDAO<Pessoa> {

    private static final String COLUNAS = "id_pessoa, nome, cpf, email, data_nascimento";

    @Override
    public Pessoa inserir(Pessoa p) throws SQLException {
        try (Connection c = ConnectionFactory.getConnection()) {
            return inserir(c, p);
        }
    }

    /** INSERT dentro de uma conexão/transação já aberta. O id é gerado pelo banco (IDENTITY). */
    public Pessoa inserir(Connection c, Pessoa p) throws SQLException {
        String sql = "INSERT INTO pessoa (nome, cpf, email, data_nascimento) VALUES (?, ?, ?, ?)";
        // Informar a coluna de PK faz o Oracle devolver o id gerado (e não o ROWID).
        try (PreparedStatement stmt = c.prepareStatement(sql, new String[]{"ID_PESSOA"})) {
            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getCpf());
            stmt.setString(3, p.getEmail());
            setData(stmt, 4, p);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) p.setIdPessoa(rs.getInt(1));
            }
        }
        return p;
    }

    @Override
    public boolean atualizar(Pessoa p) throws SQLException {
        String sql = "UPDATE pessoa SET nome = ?, cpf = ?, email = ?, data_nascimento = ? WHERE id_pessoa = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getCpf());
            stmt.setString(3, p.getEmail());
            setData(stmt, 4, p);
            stmt.setInt(5, p.getIdPessoa());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean excluir(int id) throws SQLException {
        String sql = "DELETE FROM pessoa WHERE id_pessoa = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Pessoa buscarPorId(int id) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM pessoa WHERE id_pessoa = ?";
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
    public List<Pessoa> listarTodos() throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM pessoa ORDER BY id_pessoa";
        List<Pessoa> lista = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    private void setData(PreparedStatement stmt, int indice, Pessoa p) throws SQLException {
        if (p.getDataNascimento() != null) {
            stmt.setDate(indice, Date.valueOf(p.getDataNascimento()));
        } else {
            stmt.setNull(indice, Types.DATE);
        }
    }

    private Pessoa mapear(ResultSet rs) throws SQLException {
        Date nascimento = rs.getDate("data_nascimento");
        return new Pessoa(
                rs.getInt("id_pessoa"),
                rs.getString("nome"),
                rs.getString("cpf"),
                rs.getString("email"),
                nascimento != null ? nascimento.toLocalDate() : null);
    }
}
