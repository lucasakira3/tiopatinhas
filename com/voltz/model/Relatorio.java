package com.voltz.model;

public class Relatorio {

    // Overload 1
    public void gerarRelatorio(Usuario usuario) {
        System.out.println("Relatório do usuário: " + usuario.getNome());
    }

    // Overload 2
    public void gerarRelatorio(Empresa empresa) {
        System.out.println("Relatório da empresa: " + empresa.getNome());
    }
}