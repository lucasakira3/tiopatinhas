package com.voltz.model;

public class Usuario extends Pessoa {

    private String senha;
    private Permissao permissao;
    private Empresa empresa;

    public Usuario(String nome, String email, String senha, Permissao permissao, Empresa empresa) {
        super(nome, email);
        this.senha = senha;
        this.permissao = permissao;
        this.empresa = empresa;
    }

    public boolean login(String email, String senha) {
        if (email == null || senha == null) return false;
        return email.equals(getEmail()) && senha.equals(this.senha);
    }

    // Getters e Setters
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Permissao getPermissao() {
        return permissao;
    }

    public void setPermissao(Permissao permissao) {
        this.permissao = permissao;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    @Override
    public void exibirInfo() {
        System.out.println(toString());
    }

    @Override
    public String toString() {
        return "Usuário: " + getNome() + " - " + getEmail() + " | Permissão: " + permissao;
    }
}