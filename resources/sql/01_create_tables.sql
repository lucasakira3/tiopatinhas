-- 1. Drop das tabelas (Realizada em ordem inversa de dependência)

DROP TABLE IF EXISTS transacao;
DROP TABLE IF EXISTS relatorio;
DROP TABLE IF EXISTS autenticacao;
DROP TABLE IF EXISTS carteira;
DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS pessoa;
DROP TABLE IF EXISTS empresa;
DROP TABLE IF EXISTS permissao;
DROP TABLE IF EXISTS criptoativo;

-- 2. Criação das tabelas independentes

CREATE TABLE pessoa (
    id_pessoa INT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(11) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE empresa (
    id_empresa INT PRIMARY KEY,
    razao_social VARCHAR(150) NOT NULL,
    cnpj VARCHAR(14) UNIQUE NOT NULL
);

CREATE TABLE permissao (
    id_permissao INT PRIMARY KEY,
    tipo_acesso VARCHAR(50) NOT NULL
);

CREATE TABLE criptoativo (
    id_criptoativo INT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL,
    sigla VARCHAR(10) UNIQUE NOT NULL,
    cotacao_atual DECIMAL(15, 2) NOT NULL
);

-- 3. Criação das tabelas dependentes

CREATE TABLE autenticacao (
    id_autenticacao INT PRIMARY KEY,
    id_usuario INT NOT NULL,
    token_sessao VARCHAR(255) NOT NULL,
    data_login TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE TABLE transacao (
    id_transacao INT PRIMARY KEY,
    id_carteira INT NOT NULL,
    id_criptoativo INT NOT NULL,
    tipo_operacao VARCHAR(20) NOT NULL,
    quantidade DECIMAL(18, 8) NOT NULL,
    valor_total DECIMAL(15, 2) NOT NULL,
    data_transacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_carteira) REFERENCES carteira(id_carteira),
    FOREIGN KEY (id_criptoativo) REFERENCES criptoativo(id_criptoativo)
);

CREATE TABLE relatorio (
    id_relatorio INT PRIMARY KEY,
    id_usuario INT NOT NULL,
    tipo_relatorio VARCHAR(50) NOT NULL,
    data_geracao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);