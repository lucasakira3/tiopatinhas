package com.voltz.model;

public class CriptoAtivo {

    private String nome;
    private String simbolo;
    private double quantidade;
    private double valorAtual;

    public CriptoAtivo(String nome, String simbolo, double quantidade, double valorAtual) {
        this.nome = nome;
        this.simbolo = simbolo;
        this.quantidade = quantidade;
        this.valorAtual = valorAtual;
    }

    public double calcularValorTotal() {
        return quantidade * valorAtual;
    }

    // Getters
    public String getNome() { return nome; }
    public String getSimbolo() { return simbolo; }
    public double getQuantidade() { return quantidade; }
    public double getValorAtual() { return valorAtual; }

    // Setters
    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public void setValorAtual(double valorAtual) {
        this.valorAtual = valorAtual;
    }
}