package com.voltz.model;

/**
 * Espelha a tabela USUARIO. Herda de Pessoa (herança): os dados pessoais
 * (nome, cpf, email, nascimento) vêm da superclasse. No banco, id_pessoa e
 * id_empresa são opcionais — um usuário pode ser só de empresa ou só pessoa.
 */
public class Usuario extends Pessoa {

    private int idUsuario;
    private String login;
    private String senha;
    private String status; // ATIVO ou INATIVO
    private Permissao permissao;
    private Empresa empresa; // opcional

    public Usuario(int idUsuario, String login, String senha, String status,
                   Permissao permissao, Empresa empresa, Pessoa pessoa) {
        super(pessoa != null ? pessoa.getIdPessoa() : 0,
              pessoa != null ? pessoa.getNome() : login,
              pessoa != null ? pessoa.getCpf() : null,
              pessoa != null ? pessoa.getEmail() : null,
              pessoa != null ? pessoa.getDataNascimento() : null);
        this.idUsuario = idUsuario;
        this.login = login;
        this.senha = senha;
        this.status = status;
        this.permissao = permissao;
        this.empresa = empresa;
    }

    /** Verifica as credenciais informadas contra login e senha do usuário. */
    public boolean login(String login, String senha) {
        if (login == null || senha == null) return false;
        return login.equals(this.login) && senha.equals(this.senha);
    }

    /** true se o usuário está vinculado a uma pessoa física cadastrada. */
    public boolean temPessoa() {
        return getIdPessoa() > 0;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Permissao getPermissao() { return permissao; }
    public void setPermissao(Permissao permissao) { this.permissao = permissao; }

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }

    @Override
    public void exibirInfo() {
        System.out.println(toString());
    }

    @Override
    public String toString() {
        return "Usuário [" + idUsuario + "] " + login + " (" + getNome() + ")"
                + " | Status: " + status
                + " | Permissão: " + (permissao != null ? permissao.getNome() : "-")
                + " | Empresa: " + (empresa != null ? empresa.getRazaoSocial() : "-");
    }
}
