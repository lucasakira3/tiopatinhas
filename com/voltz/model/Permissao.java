package com.voltz.model;

/** Espelha a tabela PERMISSAO (id_permissao, nome, descricao). */
public class Permissao {

    private int idPermissao;
    private String nome;
    private String descricao;

    public Permissao() {
    }

    public Permissao(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    public Permissao(int idPermissao, String nome, String descricao) {
        this.idPermissao = idPermissao;
        this.nome = nome;
        this.descricao = descricao;
    }

    public int getIdPermissao() { return idPermissao; }
    public void setIdPermissao(int idPermissao) { this.idPermissao = idPermissao; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    @Override
    public String toString() {
        return "[" + idPermissao + "] " + nome + " - " + descricao;
    }
}
