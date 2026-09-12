# 💰 Voltz - Sistema de Gestão de Criptoativos

Projeto acadêmico desenvolvido em **Java** com foco em **Programação Orientada a Objetos**, **Modelagem Entidade-Relacionamento (ER)**, **manipulação de coleções e arquivos** e **integração com Banco de Dados Oracle**.

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
* Persistência de `CriptoAtivo` em um Banco de Dados Oracle (FIAP), com CRUD completo

---

## 🏗️ Estrutura do Projeto

```
tiopatinhas-main/
├── com/voltz/
│   ├── dao/
│   │   └── CriptoAtivoDAO.java        # CRUD de CriptoAtivo no Oracle (inserir/atualizar/excluir/listar)
│   ├── db/
│   │   └── ConexaoBD.java            # [legado] conexão SQLite antiga, não é mais usada por nenhuma classe
│   ├── factory/
│   │   └── ConnectionFactory.java    # Fábrica de conexões com o Oracle da FIAP — usada pelo DAO
│   ├── model/
│   │   ├── Autenticacao.java         # Validação de login
│   │   ├── Carteira.java             # Agrupa ativos e transações
│   │   ├── CriptoAtivo.java          # Id, nome, símbolo, quantidade, valor
│   │   ├── Empresa.java              # Nome, CNPJ e lista de carteiras
│   │   ├── Permissao.java            # Enum de níveis de acesso
│   │   ├── Pessoa.java               # Classe base (nome, email)
│   │   ├── Relatorio.java            # Geração de relatórios (polimorfismo)
│   │   ├── Transacao.java            # Entidade associativa (Usuario + CriptoAtivo)
│   │   └── Usuario.java              # Herda de Pessoa; login, permissão, empresa
│   ├── Main.java                     # Ponto de entrada: simulação em memória + teste de CRUD no Oracle
│   └── sources.txt                   # Lista de fontes para compilação
├── docs/
│   ├── Dicionário de Dados.pdf
│   ├── Modelo Entidade-Relacionamento - Voltz Missão Tio Patinhas Sprint 4 (Modelo Relacional).pdf
│   └── Normalização de Dados.pdf
├── lib/
│   └── sqlite-jdbc-3.46.1.3.jar      # [legado] driver do ConexaoBD.java antigo, não é mais necessário
├── resources/sql/
│   ├── 01_create_tables.sql          # DDL: DROP + CREATE + ALTER (PKs, FKs e CHECKs)
│   ├── 02_insert_data.sql            # DML: INSERT, UPDATE e DELETE de carga/manutenção
│   └── 03_queries.sql                # DML: SELECTs de consulta sobre o modelo
├── pom.xml                           # Maven — dependência do driver Oracle (ojdbc8)
├── .gitignore
└── README.md
```

> **Nota sobre os itens marcados como [legado]:** `com/voltz/db/ConexaoBD.java` e `lib/sqlite-jdbc-3.46.1.3.jar` eram usados por uma versão anterior do projeto, que persistia `CriptoAtivo` em SQLite. Essa versão foi substituída pela integração com o Oracle da FIAP (`ConnectionFactory` + `CriptoAtivoDAO`); os dois arquivos ficaram no repositório, mas nenhuma classe atual os importa ou executa.

---

## ⚙️ Tecnologias Utilizadas

* Java (Collections, `java.time`, `java.io`, JDBC)
* Programação Orientada a Objetos (POO)
* Modelagem ER e Modelo Relacional
* Oracle Data Modeler
* Oracle Database (FIAP) via JDBC (`ojdbc8`)
* Maven (gerenciamento de dependências)

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

### ✔ Integração com Banco de Dados (JDBC / Oracle)

* `CriptoAtivo` é a classe integrada ao banco de dados, através da tabela `cripto_ativo` criada em `resources/sql/01_create_tables.sql`
* `com.voltz.factory.ConnectionFactory` abre a conexão com o Oracle da FIAP (`jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL`)
* `com.voltz.dao.CriptoAtivoDAO` implementa o CRUD completo sobre essa conexão:
  * `inserir(CriptoAtivo)` — insere um novo ativo e devolve o id gerado (coluna `IDENTITY`)
  * `atualizar(CriptoAtivo)` — altera os dados de um ativo existente
  * `excluir(int id)` — remove um ativo pelo id
  * `buscarPorId(int id)` / `listarTodos()` — exibem os ativos cadastrados
* `com.voltz.Main` chama o método `testarCrudCriptoAtivo()`, que exercita as quatro operações em sequência dentro do próprio `main()`

---

## 🗃️ Modelagem de Banco (ER)

O sistema foi modelado utilizando:

* Chaves Primárias (PK)
* Chaves Estrangeiras (FK)
* Restrições `CHECK` para regras de negócio (status do usuário, tipo de operação)
* Relacionamentos 1:N
* Resolução de relacionamento N:N com entidade associativa (`Transacao`)

### Tabelas criadas em `01_create_tables.sql`:

* `permissao`
* `pessoa`
* `empresa`
* `usuario`
* `cripto_ativo`
* `carteira`
* `transacao`
* `autenticacao`
* `relatorio`

A documentação de apoio à modelagem está disponível em [`docs/`](docs/):

* **Dicionário de Dados.pdf** — descrição de tabelas, colunas, tipos e restrições
* **Normalização de Dados.pdf** — processo de normalização (1FN, 2FN, 3FN) aplicado ao modelo
* **Modelo Entidade-Relacionamento - Voltz Missão Tio Patinhas Sprint 4 (Modelo Relacional).pdf** — diagrama ER e modelo relacional gerado no Oracle Data Modeler

> Esses três documentos foram produzidos na Fase 4, sobre uma versão anterior do modelo (6 tabelas, sem `permissao`, `pessoa`, `autenticacao` e `relatorio` como entidades próprias). O DDL atual evoluiu para 9 tabelas; a documentação ainda não foi atualizada para refletir essa mudança.

---

## ▶️ Como Executar

### 1. Configurar o banco Oracle

Antes de tudo, rode `resources/sql/01_create_tables.sql` no seu schema Oracle da FIAP (ele já inclui `DROP`, `CREATE` e `ALTER`), e em seguida `resources/sql/02_insert_data.sql` para popular as tabelas.

Depois, edite `com/voltz/factory/ConnectionFactory.java` e preencha `USER` e `PASSWORD` com o seu RM e senha do Oracle FIAP.

### 2. Compilar e executar com Maven

```
mvn compile
mvn exec:java -Dexec.mainClass="com.voltz.Main"
```

(Se o plugin `exec` não estiver configurado no seu ambiente, basta compilar com `mvn compile` e rodar a classe gerada em `target/classes` apontando o classpath para o `.jar` do `ojdbc8` baixado pelo Maven.)

Ao executar, o programa imprime no console os ativos, transações, buscas por índice, autenticação e relatórios; gera os arquivos `portfolio_ativos.txt` e `portfolio_transacoes.txt` na raiz do projeto; e, na sequência, roda o teste de CRUD de `CriptoAtivo` direto no Oracle (inserir, exibir, alterar, excluir e exibir o estado final da tabela).

---

## 📌 Funcionalidades Demonstradas

* Criação de objetos e relacionamentos entre usuários, empresa, carteira, ativos e transações
* Armazenamento e iteração de registros com `ArrayList`
* Indexação e busca rápida com `HashMap`
* Simulação de transações de compra e venda
* Autenticação de usuário
* Geração de relatórios (polimorfismo)
* Exportação de dados para arquivos `.txt` (manipulação de arquivos)
* CRUD completo de `CriptoAtivo` em um Banco de Dados Oracle, testado dentro da própria `Main`
* Exibição estruturada no terminal

---

## 🎯 Objetivo Acadêmico

Este projeto foi desenvolvido para aplicar conceitos de:

* Engenharia de Software
* Modelagem de Dados (ER, Dicionário de Dados, Normalização)
* Programação Orientada a Objetos
* Estruturas de dados (Collections) e manipulação de arquivos em Java
* Integração de aplicações Java com Banco de Dados Oracle via JDBC
