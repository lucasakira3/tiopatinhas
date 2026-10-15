package com.voltz.dao;

import com.voltz.factory.ConnectionFactory;
import com.voltz.model.Empresa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** DAO da tabela EMPRESA. */
public class EmpresaDAO implements CrudDAO<Empresa> {

    private static final String COLUNAS = "id_empresa, razao_social, cnpj, email_contato";

    @Override
    public Empresa inserir(Empresa e) throws SQLException {
        try (Connection c = ConnectionFactory.getConnection()) {
            return inserir(c, e);
        }
    }

    /** INSERT dentro de uma conexão/transação já aberta. O id é gerado pelo banco (IDENTITY). */
    public Empresa inserir(Connection c, Empresa e) throws SQLException {
        String sql = "INSERT INTO empresa (razao_social, cnpj, email_contato) VALUES (?, ?, ?)";
        // Informar a coluna de PK faz o Oracle devolver o id gerado (e não o ROWID).
        try (PreparedStatement stmt = c.prepareStatement(sql, new String[]{"ID_EMPRESA"})) {
            stmt.setString(1, e.getRazaoSocial());
            stmt.setString(2, e.getCnpj());
            stmt.setString(3, e.getEmailContato());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) e.setIdEmpresa(rs.getInt(1));
            }
        }
        return e;
    }

    @Override
    public boolean atualizar(Empresa e) throws SQLException {
        String sql = "UPDATE empresa SET razao_social = ?, cnpj = ?, email_contato = ? WHERE id_empresa = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setString(1, e.getRazaoSocial());
            stmt.setString(2, e.getCnpj());
            stmt.setString(3, e.getEmailContato());
            stmt.setInt(4, e.getIdEmpresa());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean excluir(int id) throws SQLException {
        String sql = "DELETE FROM empresa WHERE id_empresa = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Empresa buscarPorId(int id) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM empresa WHERE id_empresa = ?";
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
    public List<Empresa> listarTodos() throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM empresa ORDER BY id_empresa";
        List<Empresa> lista = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    private Empresa mapear(ResultSet rs) throws SQLException {
        return new Empresa(rs.getInt("id_empresa"), rs.getString("razao_social"),
                rs.getString("cnpj"), rs.getString("email_contato"));
    }
}
