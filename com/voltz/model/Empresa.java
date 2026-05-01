package com.voltz.model;

import java.util.List;
import java.util.ArrayList;

public class Empresa {

    private String nome;
    private String cnpj;
    private List<Carteira> carteiras;

    public Empresa(String nome, String cnpj, List<Carteira> carteiras) {
        this.nome = nome;
        this.cnpj = cnpj;
        this.carteiras = (carteiras != null) ? carteiras : new ArrayList<>();
    }

    // construtor simples
    public Empresa(String nome) {
        this.nome = nome;
        this.carteiras = new ArrayList<>();
    }

    public void adicionarCarteira(Carteira carteira) {
        this.carteiras.add(carteira);
    }

    // Getters
    public String getNome() { return nome; }
    public String getCnpj() { return cnpj; }
    public List<Carteira> getCarteiras() { return carteiras; }

    // Setters
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public void setCarteiras(List<Carteira> carteiras) {
        this.carteiras = carteiras;
    }
}