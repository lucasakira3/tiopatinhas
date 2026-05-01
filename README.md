# 💰 Voltz - Sistema de Gestão de Criptoativos

Projeto acadêmico desenvolvido em **Java** com foco em **Programação Orientada a Objetos** e **Modelagem Entidade-Relacionamento (ER)**.

---

## 🧠 Sobre o Projeto

O sistema simula uma plataforma de gerenciamento de criptoativos, permitindo:

* Cadastro de usuários
* Associação com empresas
* Criação de carteiras
* Registro de transações de compra e venda
* Controle de ativos digitais

---

## 🏗️ Estrutura do Projeto

```
Voltz/
├── src/
│   └── com/voltz/
│       ├── Main.java
│       └── model/
│           ├── Usuario.java
│           ├── Empresa.java
│           ├── Carteira.java
│           ├── Transacao.java
│           ├── CriptoAtivo.java
│           ├── Pessoa.java
│           ├── Relatorio.java
│           ├── Autenticacao.java
│           └── Permissao.java
├── bin/ (arquivos compilados)
└── README.md
```

---

## ⚙️ Tecnologias Utilizadas

* Java
* Programação Orientada a Objetos (POO)
* Modelagem ER
* Oracle Data Modeler

---

## 🧬 Conceitos Aplicados

### ✔ Herança

* `Usuario` herda de `Pessoa`

### ✔ Polimorfismo

* **Override**: método `exibirInfo()`
* **Overload**: métodos de relatório

### ✔ Encapsulamento

* Uso de getters e setters

### ✔ Entidade Associativa

* `Transacao` relaciona `Usuario` e `CriptoAtivo`

---

## 🗃️ Modelagem de Banco (ER)

O sistema foi modelado utilizando:

* Chaves Primárias (PK)
* Chaves Estrangeiras (FK)
* Relacionamentos 1:N
* Resolução de relacionamento N:N com entidade associativa

### Tabelas principais:

* T_EMPRESA
* T_USUARIO
* T_CARTEIRA
* T_CRIPTOATIVO
* T_TRANSACAO

---

## ▶️ Como Executar

### Compilar:

```
javac -d bin src/com/voltz/model/*.java src/com/voltz/Main.java
```

### Executar:

```
java -cp bin com.voltz.Main
```

---

## 📌 Funcionalidades Demonstradas

* Criação de objetos e relacionamentos
* Simulação de transações
* Autenticação de usuário
* Geração de relatórios
* Exibição estruturada no terminal

---

## 🎯 Objetivo Acadêmico

Este projeto foi desenvolvido para aplicar conceitos de:

* Engenharia de Software
* Modelagem de Dados
* Programação Orientada a Objetos

