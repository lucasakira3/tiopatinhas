-- =====
-- CONSULTAS E RELATÓRIOS (SELECTS)
-- =====

-- 1. Relatório de Portfólio de Transações por Investidor
-- Objetivo: Listar o histórico de compras/vendas com o valor total calculado por operação
SELECT
    p.nome AS investidor,
    ca.nome AS criptoativo,
    t.tipo_operacao,
    t.quantidade,
    t.valor_unidade,
    (t.quantidade * t.valor_unidade) AS valor_total_operacao,
    t.data_transacao
FROM transacao t
         INNER JOIN carteira c ON t.id_carteira = c.id_carteira
         INNER JOIN usuario u ON c.id_usuario = u.id_usuario
         INNER JOIN pessoa p ON u.id_pessoa = p.id_pessoa
         INNER JOIN cripto_ativo ca ON t.id_cripto = ca.id_cripto
WHERE u.login = 'pat_invest'
ORDER BY t.data_transacao DESC;


-- 2. Saldo de Carteira de Usuários Ativos
-- Objetivo: Filtrar apenas os usuários ativos no sistema e exibir seu saldo em BRL
SELECT
    u.id_usuario,
    u.login,
    c.saldo_reais
FROM carteira c
         INNER JOIN usuario u ON c.id_usuario = u.id_usuario
WHERE u.status = 'ATIVO';


-- 3. Criptoativos Relevantes (Cotação Superior a R$ 100)
-- Objetivo: Listar as moedas com maior valor de mercado ordenadas de forma decrescente
SELECT
    sigla,
    nome,
    cotacao_atual
FROM cripto_ativo
WHERE cotacao_atual > 100.00
ORDER BY cotacao_atual DESC;


-- 4. Auditoria de Sessões e Logins por Usuário
-- Objetivo: Mapear os acessos de usuários exibindo a data, IP e perfil de acesso
SELECT
    u.login,
    pm.nome AS perfil_permissao,
    a.data_login,
    a.ip_origem
FROM autenticacao a
         INNER JOIN usuario u ON a.id_usuario = u.id_usuario
         INNER JOIN permissao pm ON u.id_permissao = pm.id_permissao
ORDER BY a.data_login DESC;