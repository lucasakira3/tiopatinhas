package com.voltz.factory;

import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

/**
 * Fábrica de conexões com o Oracle da FIAP.
 *
 * As credenciais NÃO ficam no código. Ordem de busca:
 *   1. variáveis de ambiente FIAP_USER e FIAP_PASSWORD (e, opcional, FIAP_URL);
 *   2. arquivo db.properties na pasta de execução (chaves db.user, db.password, db.url);
 *   3. digitação no console, uma única vez por execução.
 */
public class ConnectionFactory {

    private static final String URL_PADRAO = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL";
    private static final String ARQUIVO_CONFIG = "db.properties";

    private static String url = URL_PADRAO;
    private static String usuario;
    private static String senha;

    private ConnectionFactory() {
    }

    public static Connection getConnection() throws SQLException {
        carregarCredenciais();
        return getConnection(usuario, senha);
    }

    /** Obtém conexão informando usuário e senha dinamicamente. */
    public static Connection getConnection(String user, String password) throws SQLException {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC da Oracle (ojdbc) não encontrado no classpath.", e);
        }
    }

    private static synchronized void carregarCredenciais() throws SQLException {
        if (usuario != null && senha != null) return;

        // 1. Variáveis de ambiente
        String envUser = System.getenv("FIAP_USER");
        String envPass = System.getenv("FIAP_PASSWORD");
        String envUrl = System.getenv("FIAP_URL");
        if (envUrl != null && !envUrl.isBlank()) url = envUrl.trim();
        if (envUser != null && envPass != null) {
            usuario = envUser.trim();
            senha = envPass;
            return;
        }

        // 2. Arquivo db.properties
        Path arquivo = Path.of(ARQUIVO_CONFIG);
        if (Files.exists(arquivo)) {
            Properties props = new Properties();
            try (InputStream in = Files.newInputStream(arquivo)) {
                props.load(in);
            } catch (IOException e) {
                throw new SQLException("Não foi possível ler " + ARQUIVO_CONFIG + ": " + e.getMessage(), e);
            }
            String u = props.getProperty("db.user");
            String p = props.getProperty("db.password");
            String l = props.getProperty("db.url");
            if (l != null && !l.isBlank()) url = l.trim();
            if (u != null && p != null) {
                usuario = u.trim();
                senha = p;
                return;
            }
        }

        // 3. Console
        Console console = System.console();
        if (console != null) {
            usuario = console.readLine("Digite seu RM (ex: rm12345): ").trim();
            senha = new String(console.readPassword("Digite sua senha do Banco de Dados FIAP: "));
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Digite seu RM (ex: rm12345): ");
            usuario = scanner.nextLine().trim();
            System.out.print("Digite sua senha do Banco de Dados FIAP: ");
            senha = scanner.nextLine().trim();
        }
    }

    /** Teste rápido de conexão: java -cp ... com.voltz.factory.ConnectionFactory */
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("Conexão com o banco de dados Oracle da FIAP estabelecida com sucesso!");
        } catch (SQLException e) {
            System.err.println("Falha ao conectar ao Banco de Dados FIAP:");
            e.printStackTrace();
        }
    }
}
