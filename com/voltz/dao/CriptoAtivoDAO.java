package com.voltz.dao;

import com.voltz.factory.ConnectionFactory;
import com.voltz.model.CriptoAtivo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) responsável pela persistência da classe CriptoAtivo
 * no Banco de Dados Oracle da FIAP, através da tabela cripto_ativo criada em
 * resources/sql/01_create_tables.sql.
 *
 * Implementa as quatro operações de CRUD pedidas no exercício:
 * inserir, atualizar (alterar), excluir e exibir (buscar/listar).
 */

public class CriptoAtivoDAO {

    // -------------------------------------------------------
    // CREATE — insere um novo ativo e devolve o id gerado (IDENTITY)
    // -------------------------------------------------------

    public CriptoAtivo inserir(CriptoAtivo ativo) throws SQLException {
        String sql = "INSERT INTO cripto_ativo (nome, sigla, cotacao_atual) VALUES (?, ?, ?)";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, ativo.getNome());
            stmt.setString(2, ativo.getSimbolo());
            stmt.setDouble(3, ativo.getValorAtual());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    ativo.setId(rs.getInt(1));
                }
            }
        }
        return ativo;
    }

    // -------------------------------------------------------
    // UPDATE — altera os dados de um ativo já existente (pelo id_cripto)
    // -------------------------------------------------------

    public boolean atualizar(CriptoAtivo ativo) throws SQLException {
        String sql = "UPDATE cripto_ativo SET nome = ?, sigla = ?, cotacao_atual = ? WHERE id_cripto = ?";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, ativo.getNome());
            stmt.setString(2, ativo.getSimbolo());
            stmt.setDouble(3, ativo.getValorAtual());
            stmt.setInt(4, ativo.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    // -------------------------------------------------------
    // DELETE — exclui um ativo pelo id_cripto
    // -------------------------------------------------------

    public boolean excluir(int id) throws SQLException {
        String sql = "DELETE FROM cripto_ativo WHERE id_cripto = ?";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // -------------------------------------------------------
    // READ — busca um único ativo pelo id_cripto
    // -------------------------------------------------------

    public CriptoAtivo buscarPorId(int id) throws SQLException {
        String sql = "SELECT id_cripto, nome, sigla, cotacao_atual FROM cripto_ativo WHERE id_cripto = ?";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearAtivo(rs);
                }
            }
        }
        return null;
    }

    // -------------------------------------------------------
    // READ — lista (exibe) todos os ativos cadastrados
    // -------------------------------------------------------

    public List<CriptoAtivo> listarTodos() throws SQLException {
        String sql = "SELECT id_cripto, nome, sigla, cotacao_atual FROM cripto_ativo ORDER BY id_cripto";
        List<CriptoAtivo> ativos = new ArrayList<>();

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ativos.add(mapearAtivo(rs));
            }
        }
        return ativos;
    }

    private CriptoAtivo mapearAtivo(ResultSet rs) throws SQLException {
        return new CriptoAtivo(
            rs.getInt("id_cripto"),
            rs.getString("nome"),
            rs.getString("sigla"),
            0.0, // quantidade não é uma coluna de cripto_ativo (pois pertence à carteira)
            rs.getDouble("cotacao_atual")
        );
    }
}
