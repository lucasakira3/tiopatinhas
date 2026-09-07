package com.voltz.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Responsável por abrir a conexão com o banco de dados (SQLite) e garantir
 * que a tabela T_CRIPTOATIVO exista antes de qualquer operação de CRUD.
 */
public class ConexaoBD {

    private static final String URL = "jdbc:sqlite:voltz.db";

    public static Connection conectar() throws SQLException {
        Connection conexao = DriverManager.getConnection(URL);
        criarTabelaSeNaoExistir(conexao);
        return conexao;
    }

    private static void criarTabelaSeNaoExistir(Connection conexao) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS T_CRIPTOATIVO (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome TEXT NOT NULL," +
                "simbolo TEXT NOT NULL UNIQUE," +
                "quantidade REAL NOT NULL," +
                "valor_atual REAL NOT NULL" +
                ")";
        try (Statement stmt = conexao.createStatement()) {
            stmt.execute(sql);
        }
    }
}
