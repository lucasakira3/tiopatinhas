package com.voltz;

import com.voltz.dao.AutenticacaoDAO;
import com.voltz.dao.CarteiraDAO;
import com.voltz.dao.CriptoAtivoDAO;
import com.voltz.dao.CrudDAO;
import com.voltz.dao.EmpresaDAO;
import com.voltz.dao.PermissaoDAO;
import com.voltz.dao.PessoaDAO;
import com.voltz.dao.RelatorioDAO;
import com.voltz.dao.TransacaoDAO;
import com.voltz.dao.UsuarioDAO;
import com.voltz.factory.ConnectionFactory;
import com.voltz.model.Autenticacao;
import com.voltz.model.Carteira;
import com.voltz.model.CriptoAtivo;
import com.voltz.model.Empresa;
import com.voltz.model.Permissao;
import com.voltz.model.Pessoa;
import com.voltz.model.Relatorio;
import com.voltz.model.Transacao;
import com.voltz.model.Usuario;
import com.voltz.service.OperacaoInvalidaException;
import com.voltz.service.OperacaoService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Teste de integração Java x Oracle (FIAP).
 *
 * Percorre as 9 tabelas na ordem das chaves estrangeiras
 * (permissao → pessoa → empresa → usuario → carteira → cripto_ativo →
 * transacao → autenticacao → relatorio) executando INSERT, SELECT e UPDATE,
 * demonstra a transação atômica (commit/rollback) e as consultas com JOIN e,
 * por fim, faz o DELETE na ordem inversa — deixando o banco como estava.
 */
public class Main {

    private static final PermissaoDAO permissaoDAO = new PermissaoDAO();
    private static final PessoaDAO pessoaDAO = new PessoaDAO();
    private static final EmpresaDAO empresaDAO = new EmpresaDAO();
    private static final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private static final CarteiraDAO carteiraDAO = new CarteiraDAO();
    private static final CriptoAtivoDAO criptoAtivoDAO = new CriptoAtivoDAO();
    private static final TransacaoDAO transacaoDAO = new TransacaoDAO();
    private static final AutenticacaoDAO autenticacaoDAO = new AutenticacaoDAO();
    private static final RelatorioDAO relatorioDAO = new RelatorioDAO();
    private static final OperacaoService operacaoService = new OperacaoService();

    // Registros criados pelo teste (usados também na limpeza final)
    private static Permissao permissao;
    private static Pessoa pessoa;
    private static Empresa empresa;
    private static Usuario usuario;
    private static Carteira carteira;
    private static CriptoAtivo cripto;
    private static Transacao transacao;
    private static Autenticacao autenticacao;
    private static Relatorio relatorio;

    public static void main(String[] args) {
        System.out.println("=======================================");
        System.out.println("   VOLTZ - TESTE DE INTEGRAÇÃO ORACLE  ");
        System.out.println("=======================================");

        boolean sucesso = false;
        try {
            testarConexao();
            testarPermissao();
            testarPessoa();
            testarEmpresa();
            testarUsuario();
            testarCarteira();
            testarCriptoAtivo();
            testarTransacao();
            testarAutenticacao();
            testarRelatorio();
            testarOperacoesAtomicas();
            testarConsultasComJoin();
            excluirTudo(true);
            sucesso = true;
        } catch (SQLException e) {
            System.err.println("\n[ERRO SQL] " + e.getMessage());
            System.err.println("  SQLState: " + e.getSQLState() + " | Código Oracle: " + e.getErrorCode());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("\n[ERRO] " + e);
            e.printStackTrace();
        } finally {
            // Garante que nada fique para trás no banco, mesmo se algum passo falhou.
            excluirTudo(false);
        }

        System.out.println("\n=======================================");
        System.out.println(sucesso ? "   TESTE EXECUTADO COM SUCESSO         "
                                   : "   TESTE INTERROMPIDO - VEJA O ERRO    ");
        System.out.println("=======================================");
    }

    // -------------------------------------------------------
    // 0. Conexão
    // -------------------------------------------------------
    private static void testarConexao() throws SQLException {
        secao("CONEXÃO");
        try (Connection c = ConnectionFactory.getConnection()) {
            System.out.println("Conexão com o Oracle da FIAP estabelecida: " + !c.isClosed());
        }
    }

    // -------------------------------------------------------
    // 1. PERMISSAO
    // -------------------------------------------------------
    private static void testarPermissao() throws SQLException {
        secao("TABELA PERMISSAO");
        permissao = permissaoDAO.inserir(new Permissao("TESTE_FASE6", "Permissão criada pelo Main"));
        System.out.println("[INSERT] " + permissao);
        System.out.println("[SELECT] " + permissaoDAO.buscarPorId(permissao.getIdPermissao()));

        permissao.setDescricao("Descrição alterada pelo Main");
        System.out.println("[UPDATE] " + permissaoDAO.atualizar(permissao)
                + " -> " + permissaoDAO.buscarPorId(permissao.getIdPermissao()));
        System.out.println("[SELECT *] " + permissaoDAO.listarTodos().size() + " registro(s) na tabela");
    }

    // -------------------------------------------------------
    // 2. PESSOA
    // -------------------------------------------------------
    private static void testarPessoa() throws SQLException {
        secao("TABELA PESSOA");
        pessoa = pessoaDAO.inserir(new Pessoa("Teste Fase 6", "99988877766",
                "teste.fase6@voltz.com", LocalDate.of(2000, 1, 15)));
        System.out.println("[INSERT] " + pessoa);
        System.out.println("[SELECT] " + pessoaDAO.buscarPorId(pessoa.getIdPessoa()));

        pessoa.setNome("Teste Fase 6 (alterado)");
        System.out.println("[UPDATE] " + pessoaDAO.atualizar(pessoa)
                + " -> " + pessoaDAO.buscarPorId(pessoa.getIdPessoa()));
        System.out.println("[SELECT *] " + pessoaDAO.listarTodos().size() + " registro(s) na tabela");
    }

    // -------------------------------------------------------
    // 3. EMPRESA
    // -------------------------------------------------------
    private static void testarEmpresa() throws SQLException {
        secao("TABELA EMPRESA");
        empresa = empresaDAO.inserir(new Empresa("Empresa Teste Fase 6 Ltda",
                "99888777000166", "contato@teste-fase6.com"));
        System.out.println("[INSERT] " + empresa);
        System.out.println("[SELECT] " + empresaDAO.buscarPorId(empresa.getIdEmpresa()));

        empresa.setEmailContato("novo.contato@teste-fase6.com");
        System.out.println("[UPDATE] " + empresaDAO.atualizar(empresa)
                + " -> " + empresaDAO.buscarPorId(empresa.getIdEmpresa()));
        System.out.println("[SELECT *] " + empresaDAO.listarTodos().size() + " registro(s) na tabela");
    }

    // -------------------------------------------------------
    // 4. USUARIO (herda de Pessoa; SELECT com JOIN)
    // -------------------------------------------------------
    private static void testarUsuario() throws SQLException {
        secao("TABELA USUARIO");
        usuario = usuarioDAO.inserir(new Usuario(0, "teste_fase6", "senha_teste", "ATIVO",
                permissao, empresa, pessoa));
        System.out.println("[INSERT] " + usuario);
        System.out.println("[SELECT] " + usuarioDAO.buscarPorId(usuario.getIdUsuario()));

        usuario.setStatus("INATIVO");
        System.out.println("[UPDATE] " + usuarioDAO.atualizar(usuario)
                + " -> " + usuarioDAO.buscarPorLogin("teste_fase6"));

        usuario.setStatus("ATIVO"); // volta a ativo para os próximos testes
        usuarioDAO.atualizar(usuario);
        System.out.println("[SELECT *] " + usuarioDAO.listarTodos().size() + " registro(s) na tabela");
    }

    // -------------------------------------------------------
    // 5. CARTEIRA
    // -------------------------------------------------------
    private static void testarCarteira() throws SQLException {
        secao("TABELA CARTEIRA");
        carteira = carteiraDAO.inserir(new Carteira(usuario.getIdUsuario(), new BigDecimal("10000.00")));
        System.out.println("[INSERT] " + carteira);
        System.out.println("[SELECT] " + carteiraDAO.buscarPorId(carteira.getIdCarteira()));

        carteira.creditar(new BigDecimal("5000.00"));
        System.out.println("[UPDATE] " + carteiraDAO.atualizar(carteira)
                + " -> " + carteiraDAO.buscarPorId(carteira.getIdCarteira()));
        System.out.println("[SELECT *] " + carteiraDAO.listarTodos().size() + " registro(s) na tabela");
    }

    // -------------------------------------------------------
    // 6. CRIPTO_ATIVO (PK IDENTITY gerada pelo banco)
    // -------------------------------------------------------
    private static void testarCriptoAtivo() throws SQLException {
        secao("TABELA CRIPTO_ATIVO");
        cripto = criptoAtivoDAO.inserir(new CriptoAtivo("Moeda Teste Fase 6", "TST6", new BigDecimal("100.00")));
        System.out.println("[INSERT] " + cripto + "  (id gerado pelo banco)");
        System.out.println("[SELECT] " + criptoAtivoDAO.buscarPorId(cripto.getId()));

        cripto.setValorAtual(new BigDecimal("125.50"));
        System.out.println("[UPDATE] " + criptoAtivoDAO.atualizar(cripto)
                + " -> " + criptoAtivoDAO.buscarPorSigla("TST6"));

        // Collections: o resultado do SELECT * é indexado por sigla em um HashMap
        List<CriptoAtivo> todos = criptoAtivoDAO.listarTodos();
        Map<String, CriptoAtivo> porSigla = new HashMap<>();
        for (CriptoAtivo a : todos) porSigla.put(a.getSimbolo(), a);
        System.out.println("[SELECT *] " + todos.size() + " registro(s); busca no HashMap por 'TST6': "
                + porSigla.get("TST6"));
    }

    // -------------------------------------------------------
    // 7. TRANSACAO (CRUD direto no DAO)
    // -------------------------------------------------------
    private static void testarTransacao() throws SQLException {
        secao("TABELA TRANSACAO");
        transacao = transacaoDAO.inserir(new Transacao(carteira.getIdCarteira(), cripto.getId(),
                Transacao.COMPRA, new BigDecimal("1.5"), cripto.getValorAtual(), LocalDateTime.now()));
        System.out.println("[INSERT] " + transacao);
        System.out.println("[SELECT] " + transacaoDAO.buscarPorId(transacao.getIdTransacao()));

        transacao.setQuantidade(new BigDecimal("2"));
        System.out.println("[UPDATE] " + transacaoDAO.atualizar(transacao)
                + " -> " + transacaoDAO.buscarPorId(transacao.getIdTransacao()));
        System.out.println("[SELECT *] " + transacaoDAO.listarTodos().size() + " registro(s) na tabela");
    }

    // -------------------------------------------------------
    // 8. AUTENTICACAO
    // -------------------------------------------------------
    private static void testarAutenticacao() throws SQLException {
        secao("TABELA AUTENTICACAO");
        Autenticacao regra = new Autenticacao();
        boolean ok = regra.autenticar(usuario, "teste_fase6", "senha_teste");

        if (ok) {
            autenticacao = autenticacaoDAO.inserir(new Autenticacao(usuario.getIdUsuario(),
                    Autenticacao.gerarToken(), LocalDateTime.now(), "127.0.0.1"));
            System.out.println("[INSERT] " + autenticacao);
            System.out.println("[SELECT] " + autenticacaoDAO.buscarPorId(autenticacao.getIdAutenticacao()));

            autenticacao.setIpOrigem("192.168.0.99");
            System.out.println("[UPDATE] " + autenticacaoDAO.atualizar(autenticacao)
                    + " -> " + autenticacaoDAO.buscarPorId(autenticacao.getIdAutenticacao()));
            System.out.println("[SELECT por usuário] "
                    + autenticacaoDAO.listarPorUsuario(usuario.getIdUsuario()).size() + " acesso(s)");
        }
        System.out.println("[SELECT *] " + autenticacaoDAO.listarTodos().size() + " registro(s) na tabela");
    }

    // -------------------------------------------------------
    // 9. RELATORIO (polimorfismo: overload de gerarRelatorio)
    // -------------------------------------------------------
    private static void testarRelatorio() throws SQLException {
        secao("TABELA RELATORIO");
        relatorio = new Relatorio();
        System.out.println(relatorio.gerarRelatorio(usuario));              // overload com Usuario
        System.out.println(new Relatorio().gerarRelatorio(empresa));        // overload com Empresa

        relatorioDAO.inserir(relatorio);
        System.out.println("[INSERT] " + relatorio);
        System.out.println("[SELECT] " + relatorioDAO.buscarPorId(relatorio.getIdRelatorio()));

        relatorio.setTipoRelatorio("USUARIO_DETALHADO");
        System.out.println("[UPDATE] " + relatorioDAO.atualizar(relatorio)
                + " -> " + relatorioDAO.buscarPorId(relatorio.getIdRelatorio()));
        System.out.println("[SELECT por usuário] "
                + relatorioDAO.listarPorUsuario(usuario.getIdUsuario()).size() + " relatório(s)");
        System.out.println("[SELECT *] " + relatorioDAO.listarTodos().size() + " registro(s) na tabela");
    }

    // -------------------------------------------------------
    // 10. Transação atômica: Transacao + saldo da Carteira (commit / rollback)
    // -------------------------------------------------------
    private static void testarOperacoesAtomicas() throws SQLException {
        secao("TRANSAÇÃO ATÔMICA (COMMIT / ROLLBACK)");

        // Estado inicial conhecido: saldo de R$ 10.000,00
        carteira.setSaldoReais(new BigDecimal("10000.00"));
        carteiraDAO.atualizar(carteira);
        System.out.println("Saldo inicial: R$ " + saldo());

        try {
            Transacao compra = operacaoService.registrarOperacao(carteira.getIdCarteira(), cripto.getId(),
                    Transacao.COMPRA, new BigDecimal("10"));
            System.out.println("[COMMIT] Compra registrada: " + compra);
            System.out.println("         Saldo após compra: R$ " + saldo() + "  (esperado 8745.00)");

            Transacao venda = operacaoService.registrarOperacao(carteira.getIdCarteira(), cripto.getId(),
                    Transacao.VENDA, new BigDecimal("3"));
            System.out.println("[COMMIT] Venda registrada:  " + venda);
            System.out.println("         Saldo após venda:  R$ " + saldo() + "  (esperado 9121.50)");
        } catch (OperacaoInvalidaException e) {
            System.out.println("[FALHA INESPERADA] " + e.getMessage());
        }

        // Falhas propositais: o rollback deve manter saldo e histórico intactos
        BigDecimal saldoAntes = carteiraDAO.buscarPorId(carteira.getIdCarteira()).getSaldoReais();
        int qtdAntes = transacaoDAO.listarPorCarteira(carteira.getIdCarteira()).size();

        tentarOperacao(Transacao.COMPRA, new BigDecimal("1000000"), "compra acima do saldo");
        tentarOperacao(Transacao.VENDA, new BigDecimal("1000"), "venda acima da posição");

        BigDecimal saldoDepois = carteiraDAO.buscarPorId(carteira.getIdCarteira()).getSaldoReais();
        int qtdDepois = transacaoDAO.listarPorCarteira(carteira.getIdCarteira()).size();
        boolean intacto = saldoAntes.compareTo(saldoDepois) == 0 && qtdAntes == qtdDepois;
        System.out.println("Saldo e histórico preservados após os rollbacks: " + intacto
                + " (saldo R$ " + saldoDepois + ", " + qtdDepois + " transações)");
    }

    private static void tentarOperacao(String tipo, BigDecimal quantidade, String descricao) throws SQLException {
        try {
            operacaoService.registrarOperacao(carteira.getIdCarteira(), cripto.getId(), tipo, quantidade);
            System.out.println("[FALHA INESPERADA] " + descricao + " foi aceita.");
        } catch (OperacaoInvalidaException e) {
            System.out.println("[ROLLBACK] " + descricao + " -> " + e.getMessage());
        }
    }

    private static BigDecimal saldo() throws SQLException {
        return carteiraDAO.buscarPorId(carteira.getIdCarteira()).getSaldoReais();
    }

    // -------------------------------------------------------
    // 11. Consultas com JOIN
    // -------------------------------------------------------
    private static void testarConsultasComJoin() throws SQLException {
        secao("CONSULTAS COM JOIN");

        System.out.println("[JOIN usuario x pessoa x empresa x permissao]");
        for (Usuario u : usuarioDAO.listarTodos()) {
            System.out.println("  " + u);
        }

        System.out.println("\n[JOIN transacao x cripto_ativo] histórico da carteira " + carteira.getIdCarteira() + ":");
        for (Transacao t : transacaoDAO.listarPorCarteira(carteira.getIdCarteira())) {
            System.out.println("  " + t);
        }

        System.out.println("\n[JOIN transacao x carteira x cripto_ativo] transações do usuário "
                + usuario.getLogin() + ": " + transacaoDAO.listarPorUsuario(usuario.getIdUsuario()).size());
    }

    // -------------------------------------------------------
    // 12. DELETE em ordem inversa das FKs (e limpeza de segurança)
    // -------------------------------------------------------
    private static void excluirTudo(boolean verbose) {
        if (verbose) secao("DELETE (ordem inversa das chaves estrangeiras)");

        try {
            if (relatorio != null && relatorio.getIdRelatorio() > 0) {
                excluir("relatorio", relatorio.getIdRelatorio(), relatorioDAO, verbose);
            }
            relatorio = null;

            if (autenticacao != null) excluir("autenticacao", autenticacao.getIdAutenticacao(), autenticacaoDAO, verbose);
            autenticacao = null;

            if (carteira != null && carteira.getIdCarteira() > 0) {
                int n = transacaoDAO.excluirPorCarteira(carteira.getIdCarteira());
                if (verbose) System.out.println("[DELETE] transacao: " + n + " registro(s) da carteira removido(s)");
            }
            transacao = null;

            if (carteira != null && carteira.getIdCarteira() > 0) excluir("carteira", carteira.getIdCarteira(), carteiraDAO, verbose);
            carteira = null;

            if (cripto != null && cripto.getId() > 0) excluir("cripto_ativo", cripto.getId(), criptoAtivoDAO, verbose);
            cripto = null;

            if (usuario != null && usuario.getIdUsuario() > 0) excluir("usuario", usuario.getIdUsuario(), usuarioDAO, verbose);
            usuario = null;

            if (empresa != null && empresa.getIdEmpresa() > 0) excluir("empresa", empresa.getIdEmpresa(), empresaDAO, verbose);
            empresa = null;

            if (pessoa != null && pessoa.getIdPessoa() > 0) excluir("pessoa", pessoa.getIdPessoa(), pessoaDAO, verbose);
            pessoa = null;

            if (permissao != null && permissao.getIdPermissao() > 0) excluir("permissao", permissao.getIdPermissao(), permissaoDAO, verbose);
            permissao = null;

        } catch (SQLException e) {
            System.err.println("[AVISO] Falha na limpeza dos dados de teste: " + e.getMessage()
                    + " — reexecute resources/sql/01_create_tables.sql e 02_insert_data.sql se restar algo.");
        }
    }

    private static void excluir(String tabela, int id, CrudDAO<?> dao, boolean verbose) throws SQLException {
        boolean removido = dao.excluir(id);
        if (verbose) {
            boolean sumiu = dao.buscarPorId(id) == null;
            System.out.println("[DELETE] " + tabela + " id " + id + " -> removido: " + removido
                    + " | confirmado por SELECT: " + sumiu);
        }
    }

    private static void secao(String titulo) {
        System.out.println("\n--- " + titulo + " ---");
    }
}
