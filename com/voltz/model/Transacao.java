package com.voltz.model;

import java.time.LocalDateTime;

public class Transacao {

    private int id; // PK
    private String tipo; // COMPRA ou VENDA
    private Usuario usuario; // FK
    private CriptoAtivo ativo; // FK
    private double valor;
    private LocalDateTime data;

    public Transacao(int id, String tipo, Usuario usuario, CriptoAtivo ativo, double valor, LocalDateTime data) {
        this.id = id;
        this.tipo = tipo;
        this.usuario = usuario;
        this.ativo = ativo;
        this.valor = valor;
        this.data = data;
    }

    // Getters
    public int getId() { return id; }
    public String getTipo() { return tipo; }
    public Usuario getUsuario() { return usuario; }
    public CriptoAtivo getAtivo() { return ativo; }
    public double getValor() { return valor; }
    public LocalDateTime getData() { return data; }

    // Setters
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setAtivo(CriptoAtivo ativo) {
        this.ativo = ativo;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }
}