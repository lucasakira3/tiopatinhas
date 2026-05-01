package com.voltz.model;

import java.util.List;
import java.util.ArrayList;

public class Carteira {

    private String nome;
    private List<CriptoAtivo> ativos;
    private List<Transacao> transacoes;

    public Carteira(String nome, List<CriptoAtivo> ativos, List<Transacao> transacoes) {
        this.nome = nome;
        this.ativos = (ativos != null) ? ativos : new ArrayList<>();
        this.transacoes = (transacoes != null) ? transacoes : new ArrayList<>();
    }

    public void adicionarAtivo(CriptoAtivo ativo) {
        this.ativos.add(ativo);
    }

    public void adicionarTransacao(Transacao transacao) {
        this.transacoes.add(transacao);
    }

    // Getters
    public String getNome() { return nome; }
    public List<CriptoAtivo> getAtivos() { return ativos; }
    public List<Transacao> getTransacoes() { return transacoes; }

    // Setters
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setAtivos(List<CriptoAtivo> ativos) {
        this.ativos = ativos;
    }

    public void setTransacoes(List<Transacao> transacoes) {
        this.transacoes = transacoes;
    }
}