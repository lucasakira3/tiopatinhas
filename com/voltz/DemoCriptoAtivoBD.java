package com.voltz;

import com.voltz.dao.CriptoAtivoDAO;
import com.voltz.model.CriptoAtivo;

import java.sql.SQLException;
import java.util.List;

/**
 * Demonstra a integração da classe CriptoAtivo com o banco de dados (SQLite),
 * exercitando as quatro operações de CRUD: inserir, alterar, excluir e exibir.
 */
public class DemoCriptoAtivoBD {

    public static void main(String[] args) {
        CriptoAtivoDAO dao = new CriptoAtivoDAO();

        try {
            System.out.println("=======================================");
            System.out.println("   CRUD DE CRIPTOATIVO NO BANCO DE DADOS");
            System.out.println("=======================================\n");

            // --------------------------------------------------
            // CREATE — inserir novos ativos
            // --------------------------------------------------
            CriptoAtivo bitcoin = dao.inserir(new CriptoAtivo("Bitcoin", "BTC", 0.5, 350000.0));
            CriptoAtivo ethereum = dao.inserir(new CriptoAtivo("Ethereum", "ETH", 2.0, 18000.0));
            System.out.println("[INSERIR] " + bitcoin);
            System.out.println("[INSERIR] " + ethereum);

            // --------------------------------------------------
            // READ — exibir todos os ativos cadastrados
            // --------------------------------------------------
            System.out.println("\n[EXIBIR] Ativos cadastrados no banco:");
            for (CriptoAtivo ativo : dao.listarTodos()) {
                System.out.println("  " + ativo);
            }

            // --------------------------------------------------
            // UPDATE — alterar a cotação do Ethereum
            // --------------------------------------------------
            ethereum.setValorAtual(19500.0);
            ethereum.setQuantidade(2.5);
            boolean atualizado = dao.atualizar(ethereum);
            System.out.println("\n[ALTERAR] Ethereum atualizado: " + atualizado);
            System.out.println("  " + dao.buscarPorId(ethereum.getId()));

            // --------------------------------------------------
            // DELETE — excluir o Bitcoin
            // --------------------------------------------------
            boolean excluido = dao.excluir(bitcoin.getId());
            System.out.println("\n[EXCLUIR] Bitcoin removido: " + excluido);

            // --------------------------------------------------
            // READ — exibir estado final da tabela
            // --------------------------------------------------
            System.out.println("\n[EXIBIR] Estado final do banco de dados:");
            List<CriptoAtivo> restantes = dao.listarTodos();
            if (restantes.isEmpty()) {
                System.out.println("  (nenhum ativo cadastrado)");
            }
            for (CriptoAtivo ativo : restantes) {
                System.out.println("  " + ativo);
            }

            System.out.println("\n=======================================");
            System.out.println("        CRUD EXECUTADO COM SUCESSO      ");
            System.out.println("=======================================\n");

        } catch (SQLException e) {
            System.out.println("[ERRO] Falha ao acessar o banco de dados: " + e.getMessage());
        }
    }
}
