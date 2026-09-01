package com.voltz.dao;

import com.voltz.db.ConexaoBD;
import com.voltz.model.CriptoAtivo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) responsável pela persistência da classe CriptoAtivo
 * no banco de dados. Implementa as operações de CRUD:
 * inserir, atualizar (alterar), excluir e exibir (buscar/listar).
 */
public class CriptoAtivoDAO {

    // -------------------------------------------------------
    // CREATE — insere um novo ativo e devolve o id gerado
    // -------------------------------------------------------
    public CriptoAtivo inserir(CriptoAtivo ativo) throws SQLException {
        String sql = "INSERT INTO T_CRIPTOATIVO (nome, simbolo, quantidade, valor_atual) VALUES (?, ?, ?, ?)";

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, ativo.getNome());
            stmt.setString(2, ativo.getSimbolo());
            stmt.setDouble(3, ativo.getQuantidade());
            stmt.setDouble(4, ativo.getValorAtual());
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
    // UPDATE — altera os dados de um ativo já existente (pelo id)
    // -------------------------------------------------------
    public boolean atualizar(CriptoAtivo ativo) throws SQLException {
        String sql = "UPDATE T_CRIPTOATIVO SET nome = ?, simbolo = ?, quantidade = ?, valor_atual = ? WHERE id = ?";

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, ativo.getNome());
            stmt.setString(2, ativo.getSimbolo());
            stmt.setDouble(3, ativo.getQuantidade());
            stmt.setDouble(4, ativo.getValorAtual());
            stmt.setInt(5, ativo.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    // -------------------------------------------------------
    // DELETE — exclui um ativo pelo id
    // -------------------------------------------------------
    public boolean excluir(int id) throws SQLException {
        String sql = "DELETE FROM T_CRIPTOATIVO WHERE id = ?";

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // -------------------------------------------------------
    // READ — busca um único ativo pelo id
    // -------------------------------------------------------
    public CriptoAtivo buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nome, simbolo, quantidade, valor_atual FROM T_CRIPTOATIVO WHERE id = ?";

        try (Connection conexao = ConexaoBD.conectar();
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
        String sql = "SELECT id, nome, simbolo, quantidade, valor_atual FROM T_CRIPTOATIVO ORDER BY id";
        List<CriptoAtivo> ativos = new ArrayList<>();

        try (Connection conexao = ConexaoBD.conectar();
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
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getString("simbolo"),
            rs.getDouble("quantidade"),
            rs.getDouble("valor_atual")
        );
    }
}
