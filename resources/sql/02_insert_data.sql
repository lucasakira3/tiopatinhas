-- =====
-- 1. Inserts (Dados iniciais)
-- =====

-- Tabela de Permissões
INSERT INTO permissao (id_permissao, nome, descricao) VALUES (1, 'ADMIN', 'Administrador geral do sistema com acesso total');
INSERT INTO permissao (id_permissao, nome, descricao) VALUES (2, 'INVESTIDOR', 'Usuário padrão que realiza operações de compra e venda');

-- Tabela de Pessoas (Físicas)
INSERT INTO pessoa (id_pessoa, nome, cpf, email, data_nascimento) VALUES (1, 'Patrícia Souza', '11122233344', 'patricia.souza@email.com', TO_DATE('1990-05-15', 'YYYY-MM-DD'));
INSERT INTO pessoa (id_pessoa, nome, cpf, email, data_nascimento) VALUES (2, 'Carlos Eduardo Gomes', '55566677788', 'carlos.gomes@email.com', TO_DATE('1985-10-20', 'YYYY-MM-DD'));

-- Tabela de Empresas (Pessoas jurídicas)
INSERT INTO empresa (id_empresa, razao_social, cnpj, email_contato) VALUES (1, 'Voltz Capital S.A', '12345678000199', 'contato@voltzcapital.com');

-- Tabela de Usuários (Relacionando Pessoa/Empresa e Permissão)
INSERT INTO usuario (id_usuario, login, senha, id_pessoa, id_empresa, id_permissao, status) VALUES (1, 'admin_voltz', 'hash_senha_admin', NULL, 1, 1, 'ATIVO');
INSERT INTO usuario (id_usuario, login, senha, id_pessoa, id_empresa, id_permissao, status) VALUES (2, 'pat_invest', 'hash_senha_pat', 1, NULL, 2, 'ATIVO');
INSERT INTO usuario (id_usuario, login, senha, id_pessoa, id_empresa, id_permissao, status) VALUES (3, 'carlos_invest', 'hash_senha_carlos', 2, NULL, 2, 'ATIVO');

-- Tabela de Criptoativos
INSERT INTO cripto_ativo (id_cripto, nome, sigla, cotacao_atual) VALUES (1, 'Bitcoin', 'BTC', 315000.00);
INSERT INTO cripto_ativo (id_cripto, nome, sigla, cotacao_atual) VALUES (2, 'Ethereum', 'ETH', 17200.00);
INSERT INTO cripto_ativo (id_cripto, nome, sigla, cotacao_atual) VALUES (3, 'VoltzCoin', 'VTZ', 5.50);

-- Tabela de Carteiras
INSERT INTO carteira (id_carteira, id_usuario, saldo_reais) VALUES (1, 2, 50000.00);
INSERT INTO carteira (id_carteira, id_usuario, saldo_reais) VALUES (2, 3, 20000.00);

-- Tabela de Transações (Registrando operações nas carteiras)
INSERT INTO transacao (id_transacao, id_carteira, id_cripto, tipo_operacao, quantidade, valor_unidade, data_transacao) VALUES (1, 1, 1, 'COMPRA', 0.1, 315000.00, CURRENT_TIMESTAMP);
INSERT INTO transacao (id_transacao, id_carteira, id_cripto, tipo_operacao, quantidade, valor_unidade, data_transacao) VALUES (2, 1, 3, 'COMPRA', 1000, 5.00, CURRENT_TIMESTAMP);
INSERT INTO transacao (id_transacao, id_carteira, id_cripto, tipo_operacao, quantidade, valor_unidade, data_transacao) VALUES (3, 2, 2, 'COMPRA', 0.5, 17200.00, CURRENT_TIMESTAMP);

-- Tabela de Autenticação (Logs e Sessões ativas)
INSERT INTO autenticacao (id_autenticacao, id_usuario, token_sessao, data_login, ip_origem) VALUES (1, 2, 'abc123tokenXYZ', CURRENT_TIMESTAMP, '192.168.0.10');
INSERT INTO autenticacao (id_autenticacao, id_usuario, token_sessao, data_login, ip_origem) VALUES (2, 3, 'def456tokenUVW', CURRENT_TIMESTAMP, '192.168.0.15');

COMMIT;

-- =====
-- 2. Updates
-- =====

-- Atualizando a cotação de um CriptoAtivo
UPDATE cripto_ativo
SET cotacao_atual = 320000.00
WHERE sigla = 'BTC';

-- Descontando o saldo em reais da carteira
UPDATE carteira
SET saldo_reais = saldo_reais - 36500.00
WHERE id_usuario = 2;

-- Inativando um usuário por segurança
UPDATE usuario
SET status = 'INATIVO'
WHERE login = 'carlos_invest';

COMMIT;

-- =====
-- 3. DELETES (Exclusão de registros)
-- =====

-- Excluindo tokens de sessão de usuários inativos
DELETE FROM autenticacao
WHERE id_usuario IN (SELECT id_usuario FROM usuario WHERE status = 'INATIVO');

-- Deletando um relatório obsoleto gerado erroneamente
DELETE FROM relatorio
WHERE id_relatorio = 1;

COMMIT;

-- =====
-- 4. SELECTS (Consultas de validação e relatórios)
-- =====

-- Consulta 1: Listar o portfólio completo de transações da Patrícia
SELECT
    p.nome AS investidor,
    ca.nome AS criptoativo,
    t.tipo_operacao,
    t.quantidade,
    t.valor_unidade,
    (t.quantidade * t.valor_unidade) AS valor_total_operacao
FROM transacao t
         INNER JOIN carteira c ON t.id_carteira = c.id_carteira
         INNER JOIN usuario u ON c.id_usuario = u.id_usuario
         INNER JOIN pessoa p ON u.id_pessoa = p.id_pessoa
         INNER JOIN cripto_ativo ca ON t.id_cripto = ca.id_cripto
WHERE u.login = 'pat_invest';

-- Consulta 2: Verificar o saldo em reais disponível nas carteiras de usuários ativos
SELECT
    u.login,
    c.saldo_reais
FROM carteira c
         INNER JOIN usuario u ON c.id_usuario = u.id_usuario
WHERE u.status = 'ATIVO';

-- Consulta 3: Visualizar as moedas com cotação superior a 100 reais, ordenadas pela mais cara
SELECT
    sigla,
    nome,
    cotacao_atual
FROM cripto_ativo
WHERE cotacao_atual > 100.00
ORDER BY cotacao_atual DESC;