# 💰 Voltz - Sistema de Gestão de Criptoativos (Missão Tio Patinhas)

Projeto acadêmico em **Java** integrado ao **Banco de Dados Oracle da FIAP** via **JDBC**, com camada de
conexão, camada **DAO** e CRUD (INSERT, UPDATE, DELETE, SELECT) para **todas as tabelas** do modelo.

---

## 🏗️ Estrutura do Projeto

```
tiopatinhas/
├── com/voltz/
│   ├── Main.java                      # Teste de integração com o Oracle (as 9 tabelas)
│   ├── factory/
│   │   └── ConnectionFactory.java     # Conexão com o Oracle FIAP (credenciais fora do código)
│   ├── model/                         # Classes de domínio, espelhando as tabelas
│   │   ├── Permissao.java   Pessoa.java   Empresa.java   Usuario.java (herda de Pessoa)
│   │   ├── CriptoAtivo.java Carteira.java Transacao.java Autenticacao.java Relatorio.java
│   ├── dao/                           # Camada DAO
│   │   ├── CrudDAO.java               # Interface: inserir, atualizar, excluir, buscarPorId, listarTodos
│   │   ├── PermissaoDAO.java  PessoaDAO.java  EmpresaDAO.java  UsuarioDAO.java
│   │   └── CriptoAtivoDAO.java CarteiraDAO.java TransacaoDAO.java AutenticacaoDAO.java RelatorioDAO.java
│   └── service/
│       ├── OperacaoService.java       # Compra/venda atômica: Transacao + saldo da Carteira (commit/rollback)
│       └── OperacaoInvalidaException.java
├── resources/sql/
│   ├── 01_create_tables.sql           # DDL: DROP + CREATE + ALTER (PKs, FKs e CHECKs)
│   ├── 02_insert_data.sql             # Carga inicial, UPDATEs e DELETEs de manutenção
│   └── 03_queries.sql                 # SELECTs de consulta
├── db.properties.example              # Modelo do arquivo de credenciais
├── docs/                              # Dicionário de dados, MER e normalização (PDF)
├── lib/
├── pom.xml                            # Maven (ojdbc8) — fontes em com/voltz
└── README.md
```

---

## 🔌 Camada de conexão

`ConnectionFactory` usa a URL oficial `jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL`. **Usuário e senha não
ficam no código.** Ordem de busca:

1. variáveis de ambiente `FIAP_USER` e `FIAP_PASSWORD`;
2. arquivo `db.properties` (copie o `db.properties.example`);
3. digitação no console (uma vez por execução).

## 🗄️ Camada DAO

Todos os DAOs implementam `CrudDAO<T>`:

| DAO | Tabela | Extras |
|---|---|---|
| `PermissaoDAO` | `permissao` | — |
| `PessoaDAO` | `pessoa` | `LocalDate` ↔ `DATE` |
| `EmpresaDAO` | `empresa` | — |
| `UsuarioDAO` | `usuario` | SELECT com **JOIN** (pessoa, empresa, permissao); `buscarPorLogin` |
| `CriptoAtivoDAO` | `cripto_ativo` | id gerado pelo banco (IDENTITY); `buscarPorSigla` |
| `CarteiraDAO` | `carteira` | `SELECT … FOR UPDATE`; `atualizarSaldo` em transação |
| `TransacaoDAO` | `transacao` | **JOIN** por carteira e por usuário; posição do ativo |
| `AutenticacaoDAO` | `autenticacao` | `listarPorUsuario` |
| `RelatorioDAO` | `relatorio` | `listarPorUsuario` |

* Todas as consultas usam `PreparedStatement` e try-with-resources.
* `listarTodos()` devolve `List` (`ArrayList`), e o `Main` indexa resultados em `HashMap`.
* **Todas as PKs são `IDENTITY`** (`START WITH 1000`): o INSERT não informa o id, o banco o gera e o DAO o
  recupera via `getGeneratedKeys()` — sem risco de colisão em acessos simultâneos. A carga inicial usa os ids
  1..n, faixa que a numeração automática nunca alcança.
* Valores monetários usam `BigDecimal` (colunas `NUMBER(15,2)` / `NUMBER(18,8)`).

## 🔁 Controle transacional

`OperacaoService.registrarOperacao(...)` desliga o auto-commit, trava a carteira (`FOR UPDATE`), valida as regras
(saldo suficiente na compra, posição suficiente na venda), grava a `Transacao` e atualiza o `saldo_reais` na mesma
transação. Se qualquer passo falha → `rollback`.

---

## ▶️ Como executar

### 1. Preparar o banco

Rode no seu schema Oracle FIAP: `resources/sql/01_create_tables.sql` e depois `resources/sql/02_insert_data.sql`
(na primeira vez, os `DROP TABLE` do início acusam "tabela não existe" — pode ignorar).

### 2. Configurar as credenciais

```
cp db.properties.example db.properties     # e preencha RM e senha
```

### 3. Executar

```
mvn compile
mvn exec:java        # roda com.voltz.Main  (integração com o Oracle)
```

Sem Maven: compile os fontes de `com/voltz` com o `ojdbc8.jar` no classpath e execute `com.voltz.Main`.

### O que o `Main` faz

1. Testa a conexão.
2. Para **cada uma das 9 tabelas**, na ordem das FKs (permissao → pessoa → empresa → usuario → carteira →
   cripto_ativo → transacao → autenticacao → relatorio): **INSERT, SELECT, UPDATE** e lista (`SELECT *`).
3. Executa compras/vendas atômicas e mostra o **rollback** em duas falhas propositais.
4. Executa consultas com **JOIN**.
5. Faz o **DELETE** de tudo na ordem inversa das FKs, confirmando cada exclusão por SELECT — o banco volta ao
   estado original, então o `Main` pode ser executado várias vezes.

> O teste usa dados próprios (login `teste_fase6`, sigla `TST6`, CPF `99988877766`, CNPJ `99888777000166`).
> Se uma execução for interrompida à força (ex.: matar o processo), esses registros podem sobrar e a próxima
> execução acusará `ORA-00001`; basta reexecutar `01_create_tables.sql` e `02_insert_data.sql`.

---

## 🧬 Conceitos de POO aplicados

* **Herança:** `Usuario` herda de `Pessoa`.
* **Polimorfismo:** override de `exibirInfo()`; overload de `Relatorio.gerarRelatorio(Usuario | Empresa)`;
  interface `CrudDAO<T>` implementada por todos os DAOs.
* **Encapsulamento:** atributos privados com getters/setters; regras de saldo dentro de `Carteira`.
* **Entidade associativa:** `Transacao` (carteira × criptoativo).

---
