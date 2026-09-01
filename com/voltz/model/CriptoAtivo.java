package com.voltz.model;

public class CriptoAtivo {

    private int id; // PK (gerado pelo banco de dados)
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

    public CriptoAtivo(int id, String nome, String simbolo, double quantidade, double valorAtual) {
        this.id = id;
        this.nome = nome;
        this.simbolo = simbolo;
        this.quantidade = quantidade;
        this.valorAtual = valorAtual;
    }

    public double calcularValorTotal() {
        return quantidade * valorAtual;
    }

    // Getters
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getSimbolo() { return simbolo; }
    public double getQuantidade() { return quantidade; }
    public double getValorAtual() { return valorAtual; }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public void setValorAtual(double valorAtual) {
        this.valorAtual = valorAtual;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s) | Qtd: %.4f | Valor unit.: R$ %.2f | Total: R$ %.2f",
            id, nome, simbolo, quantidade, valorAtual, calcularValorTotal());
    }
}
