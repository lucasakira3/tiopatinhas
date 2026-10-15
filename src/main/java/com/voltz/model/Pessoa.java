package com.voltz.model;

import java.time.LocalDate;

/** Classe base (herança). Espelha a tabela PESSOA. */
public class Pessoa {

    private int idPessoa;
    private String nome;
    private String cpf;
    private String email;
    private LocalDate dataNascimento;

    public Pessoa() {
    }

    public Pessoa(String nome, String cpf, String email, LocalDate dataNascimento) {
        this(0, nome, cpf, email, dataNascimento);
    }

    public Pessoa(int idPessoa, String nome, String cpf, String email, LocalDate dataNascimento) {
        this.idPessoa = idPessoa;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.dataNascimento = dataNascimento;
    }

    public int getIdPessoa() { return idPessoa; }
    public void setIdPessoa(int idPessoa) { this.idPessoa = idPessoa; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public void exibirInfo() {
        System.out.println("Pessoa: " + nome + " - " + email);
    }

    @Override
    public String toString() {
        return "[" + idPessoa + "] " + nome + " | CPF: " + cpf + " | " + email
                + " | Nasc.: " + dataNascimento;
    }
}
