package com.voltz.model;

import java.time.LocalDateTime;
import java.util.UUID;

/** Espelha a tabela AUTENTICACAO (registro de sessão/login do usuário). */
public class Autenticacao {

    private int idAutenticacao;
    private int idUsuario;
    private String tokenSessao;
    private LocalDateTime dataLogin;
    private String ipOrigem;

    public Autenticacao() {
    }

    public Autenticacao(int idUsuario, String tokenSessao, LocalDateTime dataLogin, String ipOrigem) {
        this(0, idUsuario, tokenSessao, dataLogin, ipOrigem);
    }

    public Autenticacao(int idAutenticacao, int idUsuario, String tokenSessao,
                        LocalDateTime dataLogin, String ipOrigem) {
        this.idAutenticacao = idAutenticacao;
        this.idUsuario = idUsuario;
        this.tokenSessao = tokenSessao;
        this.dataLogin = dataLogin;
        this.ipOrigem = ipOrigem;
    }

    /** Valida login/senha e informa o resultado no console. */
    public boolean autenticar(Usuario usuario, String login, String senha) {
        if (usuario == null) {
            System.out.println("Usuário inválido");
            return false;
        }
        boolean ok = "ATIVO".equals(usuario.getStatus()) && usuario.login(login, senha);
        System.out.println(ok ? "Login realizado com sucesso!" : "Login ou senha incorretos (ou usuário inativo).");
        return ok;
    }

    public static String gerarToken() {
        return UUID.randomUUID().toString();
    }

    public int getIdAutenticacao() { return idAutenticacao; }
    public void setIdAutenticacao(int idAutenticacao) { this.idAutenticacao = idAutenticacao; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getTokenSessao() { return tokenSessao; }
    public void setTokenSessao(String tokenSessao) { this.tokenSessao = tokenSessao; }

    public LocalDateTime getDataLogin() { return dataLogin; }
    public void setDataLogin(LocalDateTime dataLogin) { this.dataLogin = dataLogin; }

    public String getIpOrigem() { return ipOrigem; }
    public void setIpOrigem(String ipOrigem) { this.ipOrigem = ipOrigem; }

    @Override
    public String toString() {
        return "Autenticação [" + idAutenticacao + "] usuário " + idUsuario
                + " | IP: " + ipOrigem + " | " + dataLogin;
    }
}
