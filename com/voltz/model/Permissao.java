package com.voltz.model;

public enum Permissao {

    ADMIN_GERAL("Administrador Geral"),
    ADMIN_EMPRESARIAL("Administrador Empresarial"),
    INVESTIDOR("Investidor"),
    VISUALIZADOR("Visualizador");

    private String descricao;

    Permissao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}