package com.voltz.model;

/** Espelha a tabela EMPRESA (id_empresa, razao_social, cnpj, email_contato). */
public class Empresa {

    private int idEmpresa;
    private String razaoSocial;
    private String cnpj;
    private String emailContato;

    public Empresa() {
    }

    public Empresa(String razaoSocial, String cnpj, String emailContato) {
        this(0, razaoSocial, cnpj, emailContato);
    }

    public Empresa(int idEmpresa, String razaoSocial, String cnpj, String emailContato) {
        this.idEmpresa = idEmpresa;
        this.razaoSocial = razaoSocial;
        this.cnpj = cnpj;
        this.emailContato = emailContato;
    }

    public int getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(int idEmpresa) { this.idEmpresa = idEmpresa; }

    public String getRazaoSocial() { return razaoSocial; }
    public void setRazaoSocial(String razaoSocial) { this.razaoSocial = razaoSocial; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getEmailContato() { return emailContato; }
    public void setEmailContato(String emailContato) { this.emailContato = emailContato; }

    @Override
    public String toString() {
        return "[" + idEmpresa + "] " + razaoSocial + " | CNPJ: " + cnpj + " | " + emailContato;
    }
}
