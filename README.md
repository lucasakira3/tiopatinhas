# 💰 Voltz - Sistema de Gestão de Criptoativos

Projeto acadêmico desenvolvido em **Java** com foco em **Programação Orientada a Objetos**, **Modelagem Entidade-Relacionamento (ER)** e **manipulação de coleções e arquivos**.

---

## 🧠 Sobre o Projeto

O sistema simula uma plataforma de gerenciamento de criptoativos, permitindo:

* Cadastro de múltiplos usuários com diferentes níveis de permissão
* Associação de usuários a uma empresa
* Criação de carteiras com ativos e transações
* Registro de transações de compra e venda
* Controle de ativos digitais (criptomoedas)
* Indexação de ativos e usuários via `HashMap` para busca rápida
* Exportação do portfólio e do histórico de transações para arquivos `.txt`
* Autenticação de usuário e geração de relatórios

---

## 🏗️ Estrutura do Projeto

```
Voltz - Sprint 4/
├── com/voltz/
│   ├── Main.java              # Ponto de entrada da aplicação
│   ├── sources.txt            # Lista de fontes para compilação
│   └── model/
│       ├── Pessoa.java        # Classe base (nome, email)
│       ├── Usuario.java       # Herda de Pessoa; login, permissão, empresa
│       ├── Empresa.java       # Nome, CNPJ e lista de carteiras
│       ├── Carteira.java      # Agrupa ativos e transações
│       ├── CriptoAtivo.java   # Nome, símbolo, quantidade, valor
│       ├── Transacao.java     # Entidade associativa (Usuario + CriptoAtivo)
│       ├── Permissao.java     # Enum de níveis de acesso
│       ├── Autenticacao.java  # Validação de login
│       └── Relatorio.java     # Geração de relatórios (polimorfismo)
├── docs/
│   ├── Dicionário de Dados.pdf                                        # Descrição de cada atributo/tabela do modelo
│   ├── Normalização de Dados.pdf                                      # Justificativa das formas normais aplicadas (1FN–3FN)
│   └── Modelo Entidade-Relacionamento - Voltz ... (Modelo Relacional).pdf  # Diagrama ER e modelo relacional (Oracle Data Modeler)
└── README.md
```

---

## ⚙️ Tecnologias Utilizadas

* Java (Collections, `java.time`, `java.io`)
* Programação Orientada a Objetos (POO)
* Modelagem ER e Modelo Relacional
* Oracle Data Modeler

---

## 🧬 Conceitos Aplicados

### ✔ Herança

* `Usuario` herda de `Pessoa`

### ✔ Polimorfismo

* **Override**: método `exibirInfo()`
* **Overload**: `Relatorio.gerarRelatorio(Usuario)` / `Relatorio.gerarRelatorio(Empresa)`

### ✔ Encapsulamento

* Uso de getters e setters em todas as classes de modelo

### ✔ Entidade Associativa

* `Transacao` relaciona `Usuario` e `CriptoAtivo`, carregando atributos próprios (tipo, valor, data)

### ✔ Coleções (ArrayList e HashMap)

* `ArrayList<CriptoAtivo>` e `ArrayList<Transacao>` para armazenar os registros cadastrados
* `HashMap<String, CriptoAtivo>` indexado por símbolo, para busca O(1) de um ativo
* `HashMap<String, Usuario>` indexado por email, para busca O(1) de um usuário

### ✔ Manipulação de Arquivos

* `gravarArquivoAtivos(...)` grava `portfolio_ativos.txt` com a lista de ativos e o índice por símbolo
* `gravarArquivoTransacoes(...)` grava `portfolio_transacoes.txt` com o histórico de transações e o resumo por usuário

---

## 🗃️ Modelagem de Banco (ER)

O sistema foi modelado utilizando:

* Chaves Primárias (PK)
* Chaves Estrangeiras (FK)
* Relacionamentos 1:N
* Resolução de relacionamento N:N com entidade associativa (`Transacao`)

### Tabelas principais:

* T_EMPRESA
* T_USUARIO
* T_CARTEIRA
* T_CRIPTOATIVO
* T_TRANSACAO

A documentação completa da modelagem está disponível em [`docs/`](docs/):

* **Dicionário de Dados.pdf** — descrição de cada tabela, coluna, tipo e restrição do modelo
* **Normalização de Dados.pdf** — processo de normalização (1FN, 2FN, 3FN) aplicado ao modelo
* **Modelo Entidade-Relacionamento - Voltz Missão Tio Patinhas Sprint 4 (Modelo Relacional).pdf** — diagrama ER e modelo relacional gerado no Oracle Data Modeler

---

## ▶️ Como Executar

### Compilar:

```
javac com/voltz/model/*.java com/voltz/Main.java
```

### Executar:

```
java com.voltz.Main
```

Ao executar, o programa imprime no console os ativos, transações, buscas por índice, autenticação e relatórios, além de gerar dois arquivos na raiz do projeto: `portfolio_ativos.txt` e `portfolio_transacoes.txt`.

---

## 📌 Funcionalidades Demonstradas

* Criação de objetos e relacionamentos entre usuários, empresa, carteira, ativos e transações
* Armazenamento e iteração de registros com `ArrayList`
* Indexação e busca rápida com `HashMap`
* Simulação de transações de compra e venda
* Autenticação de usuário
* Geração de relatórios (polimorfismo)
* Exportação de dados para arquivos `.txt` (manipulação de arquivos)
* Exibição estruturada no terminal

---

## 🎯 Objetivo Acadêmico

Este projeto foi desenvolvido para aplicar conceitos de:

* Engenharia de Software
* Modelagem de Dados (ER, Dicionário de Dados, Normalização)
* Programação Orientada a Objetos
* Estruturas de dados (Collections) e manipulação de arquivos em Java
