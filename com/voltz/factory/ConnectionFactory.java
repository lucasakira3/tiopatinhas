package com.voltz.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    // URL oficial da FIAP (SID em maiúsculo)
    private static final String URL = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL";

    // Preencha para testes locais no DAO sem precisar passar login toda vez
    private static final String USER = "rmXXXXX";
    private static final String PASSWORD = "sua_senha_fiap";


    public static Connection getConnection() throws SQLException {
        return getConnection(USER, PASSWORD);
    }

    /**
     * Obtém conexão informando usuário e senha dinamicamente.
     */
    public static Connection getConnection(String user, String password) throws SQLException {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            return DriverManager.getConnection(URL, user, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC da Oracle (ojdbc) não encontrado no classpath.", e);
        }
    }

    public static void main(String[] args) {
        java.util.Scanner scanner = new java.util.Scanner(System.in);

        System.out.print("Digite seu RM (ex: rm12345): ");
        String user = scanner.nextLine().trim();

        System.out.print("Digite sua senha do Banco de Dados FIAP: ");
        String password = scanner.nextLine().trim();

        try (Connection conn = getConnection(user, password)) {
            System.out.println("Conexão com o banco de dados Oracle da FIAP estabelecida com sucesso!");
        } catch (SQLException e) {
            System.err.println("Falha ao conectar ao Banco de Dados FIAP:");
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}