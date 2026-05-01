package com.voltz;

import com.voltz.model.*;
import java.util.ArrayList;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {

        try {

            System.out.println("=======================================");
            System.out.println("        SISTEMA VOLTZ INICIADO         ");
            System.out.println("=======================================\n");

            // Empresa
            Empresa empresa = new Empresa("Voltz");

            // Usuário
            Usuario u = new Usuario(
                "Akira",
                "akira@email.com",
                "123",
                Permissao.ADMIN_GERAL,
                empresa
            );

            System.out.println("USUÁRIO:");
            u.exibirInfo();

            // Ativo
            CriptoAtivo btc = new CriptoAtivo("Bitcoin", "BTC", 0.5, 300000);

            // Carteira
            Carteira carteira = new Carteira("Carteira Principal", new ArrayList<>(), new ArrayList<>());
            carteira.adicionarAtivo(btc);

            // Transação (entidade associativa)
            Transacao t1 = new Transacao(
                1,
                "COMPRA",
                u,
                btc,
                150000,
                LocalDateTime.now()
            );

            carteira.adicionarTransacao(t1);

            // Exibindo carteira
            System.out.println("\nCARTEIRA:");
            System.out.println("Nome: " + carteira.getNome());

            System.out.println("\nATIVOS:");
            for (CriptoAtivo ativo : carteira.getAtivos()) {
                System.out.println("- " + ativo.getNome() +
                                   " (" + ativo.getSimbolo() + ")" +
                                   " | Total: R$ " + ativo.calcularValorTotal());
            }

            // Transações
            System.out.println("\nTRANSACOES:");
            for (Transacao t : carteira.getTransacoes()) {
                System.out.println("ID: " + t.getId());
                System.out.println("Tipo: " + t.getTipo());
                System.out.println("Usuario: " + t.getUsuario().getNome());
                System.out.println("Ativo: " + t.getAtivo().getNome());
                System.out.println("Valor: R$ " + t.getValor());
                System.out.println("Data: " + t.getData());
                System.out.println("-----------------------------");
            }

            // Autenticação
            System.out.println("\nAUTENTICACAO:");
            Autenticacao auth = new Autenticacao();
            auth.autenticar(u, "akira@email.com", "123");

            // Relatórios (polimorfismo)
            System.out.println("\nRELATORIOS:");
            Relatorio relatorio = new Relatorio();
            relatorio.gerarRelatorio(u);
            relatorio.gerarRelatorio(empresa);

            System.out.println("\n=======================================");
            System.out.println("     SISTEMA EXECUTADO COM SUCESSO     ");
            System.out.println("=======================================\n");

        } catch (Exception e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }
}