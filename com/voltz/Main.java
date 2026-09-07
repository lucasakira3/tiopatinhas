package com.voltz;

import com.voltz.model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        try {

            System.out.println("=======================================");
            System.out.println("        SISTEMA VOLTZ INICIADO         ");
            System.out.println("=======================================\n");

            // -------------------------------------------------------
            // CONFIGURAÇÃO INICIAL
            // -------------------------------------------------------
            Empresa empresa = new Empresa("Voltz", "12.345.678/0001-99", null);

            Usuario u1 = new Usuario("Akira",  "akira@email.com",  "123", Permissao.ADMIN_GERAL,       empresa);
            Usuario u2 = new Usuario("Marina", "marina@email.com", "456", Permissao.INVESTIDOR,         empresa);
            Usuario u3 = new Usuario("Carlos", "carlos@email.com", "789", Permissao.ADMIN_EMPRESARIAL,  empresa);

            // -------------------------------------------------------
            // ARRAYLIST 1 — CriptoAtivo
            // -------------------------------------------------------
            ArrayList<CriptoAtivo> ativos = new ArrayList<>();
            ativos.add(new CriptoAtivo("Bitcoin",  "BTC", 0.5,  350000.0));
            ativos.add(new CriptoAtivo("Ethereum", "ETH", 2.0,  18000.0));
            ativos.add(new CriptoAtivo("Solana",   "SOL", 10.0, 800.0));

            System.out.println("=== ATIVOS CADASTRADOS (ArrayList<CriptoAtivo>) ===");
            for (CriptoAtivo ativo : ativos) {
                System.out.printf("  %-10s (%s) | Qtd: %.2f | Valor unit.: R$ %.2f | Total: R$ %.2f%n",
                    ativo.getNome(), ativo.getSimbolo(),
                    ativo.getQuantidade(), ativo.getValorAtual(),
                    ativo.calcularValorTotal());
            }

            // -------------------------------------------------------
            // ARRAYLIST 2 — Transacao
            // -------------------------------------------------------
            ArrayList<Transacao> transacoes = new ArrayList<>();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            transacoes.add(new Transacao(1, "COMPRA", u1, ativos.get(0), 175000.0, LocalDateTime.now()));
            transacoes.add(new Transacao(2, "COMPRA", u2, ativos.get(1),  36000.0, LocalDateTime.now().minusDays(1)));
            transacoes.add(new Transacao(3, "VENDA",  u1, ativos.get(1),  20000.0, LocalDateTime.now().minusHours(3)));
            transacoes.add(new Transacao(4, "COMPRA", u3, ativos.get(2),   8000.0, LocalDateTime.now().minusDays(2)));

            System.out.println("\n=== TRANSAÇÕES (ArrayList<Transacao>) ===");
            for (Transacao t : transacoes) {
                System.out.printf("  [%d] %s | %s comprou/vendeu %s | R$ %.2f | %s%n",
                    t.getId(), t.getTipo(),
                    t.getUsuario().getNome(), t.getAtivo().getNome(),
                    t.getValor(), t.getData().format(fmt));
            }

            // -------------------------------------------------------
            // HASHMAP 1 — CriptoAtivo por símbolo
            // -------------------------------------------------------
            HashMap<String, CriptoAtivo> ativosPorSimbolo = new HashMap<>();
            for (CriptoAtivo ativo : ativos) {
                ativosPorSimbolo.put(ativo.getSimbolo(), ativo);
            }

            System.out.println("\n=== BUSCA POR SÍMBOLO (HashMap<String, CriptoAtivo>) ===");
            String[] busca = {"BTC", "SOL", "ADA"};
            for (String simbolo : busca) {
                CriptoAtivo encontrado = ativosPorSimbolo.get(simbolo);
                if (encontrado != null) {
                    System.out.printf("  %s → %s | Total: R$ %.2f%n",
                        simbolo, encontrado.getNome(), encontrado.calcularValorTotal());
                } else {
                    System.out.printf("  %s → Ativo não encontrado.%n", simbolo);
                }
            }

            // -------------------------------------------------------
            // HASHMAP 2 — Usuario por email
            // -------------------------------------------------------
            HashMap<String, Usuario> usuariosPorEmail = new HashMap<>();
            usuariosPorEmail.put(u1.getEmail(), u1);
            usuariosPorEmail.put(u2.getEmail(), u2);
            usuariosPorEmail.put(u3.getEmail(), u3);

            System.out.println("\n=== BUSCA DE USUÁRIO POR EMAIL (HashMap<String, Usuario>) ===");
            String emailBusca = "marina@email.com";
            Usuario usuarioEncontrado = usuariosPorEmail.get(emailBusca);
            if (usuarioEncontrado != null) {
                System.out.println("  Encontrado: " + usuarioEncontrado);
            }

            // -------------------------------------------------------
            // CARTEIRA — usa os ArrayLists criados
            // -------------------------------------------------------
            Carteira carteira = new Carteira("Carteira Principal", new ArrayList<>(ativos), new ArrayList<>(transacoes));
            empresa.adicionarCarteira(carteira);

            // -------------------------------------------------------
            // AUTENTICAÇÃO
            // -------------------------------------------------------
            System.out.println("\n=== AUTENTICAÇÃO ===");
            Autenticacao auth = new Autenticacao();
            auth.autenticar(u1, "akira@email.com", "123");

            // -------------------------------------------------------
            // RELATÓRIOS (polimorfismo)
            // -------------------------------------------------------
            System.out.println("\n=== RELATÓRIOS ===");
            Relatorio relatorio = new Relatorio();
            relatorio.gerarRelatorio(u1);
            relatorio.gerarRelatorio(empresa);

            // -------------------------------------------------------
            // MANIPULAÇÃO DE ARQUIVOS — escreve ativos e transações
            // -------------------------------------------------------
            gravarArquivoAtivos(ativos, ativosPorSimbolo);
            gravarArquivoTransacoes(transacoes, usuariosPorEmail, fmt);

            System.out.println("\n=======================================");
            System.out.println("     SISTEMA EXECUTADO COM SUCESSO     ");
            System.out.println("=======================================\n");

        } catch (Exception e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // Grava portfolio_ativos.txt a partir do ArrayList e HashMap
    // -------------------------------------------------------
    private static void gravarArquivoAtivos(ArrayList<CriptoAtivo> ativos,
                                            HashMap<String, CriptoAtivo> ativosPorSimbolo) {
        String arquivo = "portfolio_ativos.txt";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo))) {
            bw.write("=== PORTFÓLIO DE ATIVOS - VOLTZ ===");
            bw.newLine();
            bw.write("Gerado em: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            bw.newLine();
            bw.write("-----------------------------------");
            bw.newLine();

            bw.write("LISTA DE ATIVOS (ArrayList<CriptoAtivo>):");
            bw.newLine();
            for (CriptoAtivo a : ativos) {
                bw.write(String.format("  Nome: %-10s | Simbolo: %-5s | Qtd: %-8.4f | Valor Unit: R$ %-10.2f | Total: R$ %.2f",
                    a.getNome(), a.getSimbolo(), a.getQuantidade(), a.getValorAtual(), a.calcularValorTotal()));
                bw.newLine();
            }

            bw.newLine();
            bw.write("ÍNDICE POR SÍMBOLO (HashMap<String, CriptoAtivo>):");
            bw.newLine();
            for (HashMap.Entry<String, CriptoAtivo> entry : ativosPorSimbolo.entrySet()) {
                CriptoAtivo a = entry.getValue();
                bw.write(String.format("  [%s] → %s (Total: R$ %.2f)", entry.getKey(), a.getNome(), a.calcularValorTotal()));
                bw.newLine();
            }

            System.out.println("\n[ARQUIVO] '" + arquivo + "' gerado com sucesso.");
        } catch (IOException e) {
            System.out.println("[ERRO] Falha ao gravar " + arquivo + ": " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // Grava portfolio_transacoes.txt a partir do ArrayList e HashMap
    // -------------------------------------------------------
    private static void gravarArquivoTransacoes(ArrayList<Transacao> transacoes,
                                                HashMap<String, Usuario> usuariosPorEmail,
                                                DateTimeFormatter fmt) {
        String arquivo = "portfolio_transacoes.txt";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo))) {
            bw.write("=== HISTÓRICO DE TRANSAÇÕES - VOLTZ ===");
            bw.newLine();
            bw.write("Gerado em: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            bw.newLine();
            bw.write("---------------------------------------");
            bw.newLine();

            bw.write("TRANSAÇÕES (ArrayList<Transacao>):");
            bw.newLine();
            for (Transacao t : transacoes) {
                bw.write(String.format("  ID: %d | Tipo: %-6s | Usuario: %-10s | Ativo: %-10s | Valor: R$ %-10.2f | Data: %s",
                    t.getId(), t.getTipo(),
                    t.getUsuario().getNome(), t.getAtivo().getNome(),
                    t.getValor(), t.getData().format(fmt)));
                bw.newLine();
            }

            bw.newLine();
            bw.write("USUÁRIOS COM TRANSAÇÕES (HashMap<String, Usuario>):");
            bw.newLine();
            for (HashMap.Entry<String, Usuario> entry : usuariosPorEmail.entrySet()) {
                Usuario u = entry.getValue();
                long qtd = transacoes.stream().filter(t -> t.getUsuario().getEmail().equals(entry.getKey())).count();
                bw.write(String.format("  [%s] → %s | Permissão: %s | Transações: %d",
                    entry.getKey(), u.getNome(), u.getPermissao(), qtd));
                bw.newLine();
            }

            System.out.println("[ARQUIVO] '" + arquivo + "' gerado com sucesso.");
        } catch (IOException e) {
            System.out.println("[ERRO] Falha ao gravar " + arquivo + ": " + e.getMessage());
        }
    }
}
