package com.voltz.model;

import java.time.LocalDateTime;

/** Espelha a tabela RELATORIO e mantém a sobrecarga (polimorfismo) de gerarRelatorio. */
public class Relatorio {

    private int idRelatorio;
    private int idUsuario;
    private String tipoRelatorio;
    private LocalDateTime dataGeracao;

    public Relatorio() {
    }

    public Relatorio(int idUsuario, String tipoRelatorio, LocalDateTime dataGeracao) {
        this(0, idUsuario, tipoRelatorio, dataGeracao);
    }

    public Relatorio(int idRelatorio, int idUsuario, String tipoRelatorio, LocalDateTime dataGeracao) {
        this.idRelatorio = idRelatorio;
        this.idUsuario = idUsuario;
        this.tipoRelatorio = tipoRelatorio;
        this.dataGeracao = dataGeracao;
    }

    // Overload 1
    public String gerarRelatorio(Usuario usuario) {
        this.idUsuario = usuario.getIdUsuario();
        this.tipoRelatorio = "USUARIO";
        this.dataGeracao = LocalDateTime.now();
        return "Relatório do usuário: " + usuario.getNome() + " (" + usuario.getLogin() + ")";
    }

    // Overload 2
    public String gerarRelatorio(Empresa empresa) {
        this.tipoRelatorio = "EMPRESA";
        this.dataGeracao = LocalDateTime.now();
        return "Relatório da empresa: " + empresa.getRazaoSocial();
    }

    public int getIdRelatorio() { return idRelatorio; }
    public void setIdRelatorio(int idRelatorio) { this.idRelatorio = idRelatorio; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getTipoRelatorio() { return tipoRelatorio; }
    public void setTipoRelatorio(String tipoRelatorio) { this.tipoRelatorio = tipoRelatorio; }

    public LocalDateTime getDataGeracao() { return dataGeracao; }
    public void setDataGeracao(LocalDateTime dataGeracao) { this.dataGeracao = dataGeracao; }

    @Override
    public String toString() {
        return "Relatório [" + idRelatorio + "] usuário " + idUsuario
                + " | Tipo: " + tipoRelatorio + " | " + dataGeracao;
    }
}
