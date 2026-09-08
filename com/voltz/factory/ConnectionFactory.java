package com.voltz.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final String URL = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:orcl";

    public static Connection getConnection(String user, String password) throws SQLException {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            return DriverManager.getConnection(URL, user, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC da Oracle não foi encontrado no classpath.", e);
        }
    }


    public static void main(String[] args) {
        java.util.Scanner scanner = new java.util.Scanner(System.in);

        System.out.print("Digite seu RM (ex: rm12345): ");
        String user = scanner.nextLine();

        System.out.print("Digite sua senha do Banco de Dados FIAP: ");
        String password = scanner.nextLine();

        try (Connection conn = getConnection(user, password)) {
            System.out.println("Conexão com banco de dados Oracle da FIAP estabelecida com sucesso!");
        } catch (SQLException e) {
            System.err.println("Falha ao conectar ao Banco de Dados FIAP:");
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}