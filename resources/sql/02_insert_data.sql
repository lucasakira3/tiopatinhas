-- =====
-- 0. Limpeza preventiva de dados (Garante coesão ao reexecutar)
-- =====
DELETE FROM relatorio;
DELETE FROM autenticacao;
DELETE FROM transacao;
DELETE FROM carteira;
DELETE FROM usuario;
DELETE FROM empresa;
DELETE FROM pessoa;
DELETE FROM permissao;
DELETE FROM cripto_ativo;
COMMIT;

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

-- Tabela de Relatórios (o 3º é um rascunho, removido na seção de DELETE)
INSERT INTO relatorio (id_relatorio, id_usuario, tipo_relatorio, data_geracao) VALUES (1, 2, 'PORTFOLIO', CURRENT_TIMESTAMP);
INSERT INTO relatorio (id_relatorio, id_usuario, tipo_relatorio, data_geracao) VALUES (2, 3, 'PORTFOLIO', CURRENT_TIMESTAMP);
INSERT INTO relatorio (id_relatorio, id_usuario, tipo_relatorio, data_geracao) VALUES (3, 3, 'RASCUNHO', CURRENT_TIMESTAMP);

COMMIT;

-- =====
-- 2. Updates
-- =====

-- Atualiza a cotação do Bitcoin
UPDATE cripto_ativo
SET cotacao_atual = 320000.00
WHERE sigla = 'BTC';

-- Débito das compras da carteira 1 (Patrícia):
--   0,1 BTC x 315.000,00 = 31.500,00  +  1.000 VTZ x 5,00 = 5.000,00  ->  36.500,00
UPDATE carteira
SET saldo_reais = saldo_reais - 36500.00
WHERE id_carteira = 1;

-- Débito da compra da carteira 2 (Carlos): 0,5 ETH x 17.200,00 = 8.600,00
UPDATE carteira
SET saldo_reais = saldo_reais - 8600.00
WHERE id_carteira = 2;

-- Atualiza o e-mail de contato da empresa
UPDATE empresa
SET email_contato = 'atendimento@voltzcapital.com'
WHERE cnpj = '12345678000199';

COMMIT;

-- =====
-- 3. Deletes
-- =====

-- Remove os relatórios em rascunho (afeta apenas o id 3)
DELETE FROM relatorio
WHERE tipo_relatorio = 'RASCUNHO';

COMMIT;
