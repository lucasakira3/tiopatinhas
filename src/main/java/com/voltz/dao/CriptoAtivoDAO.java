package com.voltz.dao;

import com.voltz.factory.ConnectionFactory;
import com.voltz.model.CriptoAtivo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO da tabela CRIPTO_ATIVO (PK IDENTITY: o banco gera o id no INSERT).
 * Os métodos com parâmetro Connection permitem participar de uma transação externa.
 */
public class CriptoAtivoDAO implements CrudDAO<CriptoAtivo> {

    private static final String COLUNAS = "id_cripto, nome, sigla, cotacao_atual";

    @Override
    public CriptoAtivo inserir(CriptoAtivo ativo) throws SQLException {
        try (Connection c = ConnectionFactory.getConnection()) {
            return inserir(c, ativo);
        }
    }

    public CriptoAtivo inserir(Connection c, CriptoAtivo ativo) throws SQLException {
        String sql = "INSERT INTO cripto_ativo (nome, sigla, cotacao_atual) VALUES (?, ?, ?)";
        // Informar a coluna evita que o Oracle devolva ROWID em vez do id gerado.
        try (PreparedStatement stmt = c.prepareStatement(sql, new String[]{"ID_CRIPTO"})) {
            stmt.setString(1, ativo.getNome());
            stmt.setString(2, ativo.getSimbolo());
            stmt.setBigDecimal(3, ativo.getValorAtual());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) ativo.setId(rs.getInt(1));
            }
        }
        return ativo;
    }

    @Override
    public boolean atualizar(CriptoAtivo ativo) throws SQLException {
        String sql = "UPDATE cripto_ativo SET nome = ?, sigla = ?, cotacao_atual = ? WHERE id_cripto = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setString(1, ativo.getNome());
            stmt.setString(2, ativo.getSimbolo());
            stmt.setBigDecimal(3, ativo.getValorAtual());
            stmt.setInt(4, ativo.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean excluir(int id) throws SQLException {
        String sql = "DELETE FROM cripto_ativo WHERE id_cripto = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public CriptoAtivo buscarPorId(int id) throws SQLException {
        try (Connection c = ConnectionFactory.getConnection()) {
            return buscarPorId(c, id);
        }
    }

    public CriptoAtivo buscarPorId(Connection c, int id) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM cripto_ativo WHERE id_cripto = ?";
        try (PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    /** SELECT pela sigla (UNIQUE). */
    public CriptoAtivo buscarPorSigla(String sigla) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM cripto_ativo WHERE sigla = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setString(1, sigla);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    @Override
    public List<CriptoAtivo> listarTodos() throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM cripto_ativo ORDER BY id_cripto";
        List<CriptoAtivo> ativos = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) ativos.add(mapear(rs));
        }
        return ativos;
    }

    private CriptoAtivo mapear(ResultSet rs) throws SQLException {
        return new CriptoAtivo(rs.getInt("id_cripto"), rs.getString("nome"),
                rs.getString("sigla"), rs.getBigDecimal("cotacao_atual"));
    }
}
