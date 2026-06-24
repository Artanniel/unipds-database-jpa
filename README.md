# 🗄️ OrderSystem JPA

> Projeto acadêmico desenvolvido para a disciplina de **Banco de Dados** da **UniPDS**, demonstrando o uso do **Spring Data JPA** com **MySQL** para persistência, consultas JPQL e Native Queries em um sistema de pedidos completo.

---

## 📋 Sobre o Projeto

O **OrderSystem JPA** é uma aplicação Spring Boot que modela um sistema de pedidos com as seguintes entidades: `User`, `Product`, `Order` e `OrderItem`. Ao inicializar, o `DatabaseSeeder` popula automaticamente o banco com dados de exemplo e executa consultas para demonstrar os recursos do JPA.

---

## 🛠️ Tecnologias Utilizadas

| Tecnologia | Versão | Papel |
|---|---|---|
| ☕ Java | 21 | Linguagem principal |
| 🍃 Spring Boot | 3.5.15 | Framework base |
| 🔗 Spring Data JPA | (gerenciado pelo Boot) | Camada de persistência (ORM) |
| 🐬 MySQL Connector/J | (gerenciado pelo Boot) | Driver JDBC para MySQL |
| 🏗️ Hibernate | (gerenciado pelo Boot) | Implementação JPA |
| 🧪 Liquibase | (gerenciado pelo Boot) | Versionamento e migração do banco de dados |
| 🔧 Maven | 3.x | Gerenciador de dependências e build |
| 🐳 Podman Compose | 1.x | Orquestração do container MySQL |

---

## 🏛️ Arquitetura do Projeto

```
unipds-database-jpa/
│
├── 📁 src/main/
│   ├── 📁 docker/                          # Infraestrutura (Maven Standard Layout)
│   │   └── podman-compose.yml              # Container MySQL via Podman
│   │
│   ├── 📁 java/.../ordersystem_jpa/
│   │   ├── 📄 OrdersystemJpaApplication.java   # Ponto de entrada da aplicação
│   │   ├── 📄 DatabaseSeeder.java              # Seed automático via CommandLineRunner
│   │   ├── 📁 entity/                          # Entidades JPA
│   │   │   ├── Order.java
│   │   │   ├── OrderItem.java
│   │   │   ├── Product.java
│   │   │   ├── ProductReview.java
│   │   │   └── User.java
│   │   ├── 📁 enums/
│   │   │   └── OrderStatus.java
│   │   └── 📁 repository/
│   │       ├── OrderRepository.java
│   │       ├── ProductRepository.java
│   │       └── UserRepository.java
│   │
│   └── 📁 resources/
│       ├── application.yml
│       └── 📁 db/changelog/                # Migrations do Liquibase
│           ├── db.changelog-master.yml     # Arquivo orquestrador (entry point)
│           ├── 01-create-schema.yml        # Criação de tabelas, índices e FKs
│           ├── 02-seed-users.sql           # Seed: 20 usuários
│           ├── 03-seed-products.sql        # Seed: 20 produtos
│           ├── 04-seed-orders.sql          # Seed: 20 pedidos
│           ├── 05-seed-order-items.sql     # Seed: itens dos pedidos
│           └── 06-seed-product-reviews.sql # Seed: avaliações de produtos
│
├── pom.xml
└── README.md
```

---

## ⚙️ Configuração e Execução

### Pré-requisitos

- JDK 21+
- [Podman](https://podman.io/) + [Podman Compose](https://github.com/containers/podman-compose) **ou** MySQL 8+ instalado localmente
- Maven 3.x (ou usar o wrapper `./mvnw` incluso)

### 1. Suba o banco de dados com Podman

O projeto inclui um `podman-compose.yml` em `src/main/docker/` que provisiona um container MySQL 9 com as credenciais já alinhadas ao `application.yml`:

```yaml
# src/main/docker/podman-compose.yml
services:
  mysql:
    image: mysql:9.7.1
    container_name: unipds_mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: s3nH4SuP3rS3gur4
      MYSQL_DATABASE: unipds-database
      MYSQL_USER: usuarioSeguro
      MYSQL_PASSWORD: s3nH4SuP3rS3gur4
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
```

Para subir o container, execute a partir da raiz do projeto:

```bash
# Subir o MySQL em segundo plano
podman-compose -f src/main/docker/podman-compose.yml up -d

# Verificar se o container está rodando
podman ps

# Ver logs do MySQL (opcional)
podman logs unipds_mysql

# Parar o container quando terminar
podman-compose -f src/main/docker/podman-compose.yml down
```

> **💡 Dica:** O volume `mysql_data` persiste os dados entre reinicializações do container. Para resetar o banco completamente, use `podman-compose ... down -v`.

### 2. Verifique o `application.yml`

As credenciais e as configurações do Liquibase já estão prontas:

```yaml
# src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ordersystem_db?useSSL=true&serverTimezone=UTC
    username: root
    password: s3nH4SuP3rS3gur4
    hikari:
      maximum-pool-size: 20
      minimum-idle: 10

  liquibase:
    enabled: true                                          # Ativa o Liquibase
    change-log: classpath:db/changelog/db.changelog-master.yml  # Entry point
    drop-first: false                                      # Nunca dropa o banco em produção!

  jpa:
    hibernate:
      ddl-auto: validate   # Hibernate apenas valida o schema; o Liquibase que cria
    show-sql: true
```

> **⚠️ Importante:** Com Liquibase ativo, o `ddl-auto` deve ser `validate` (ou `none`). O Hibernate **não** cria nem altera tabelas — essa responsabilidade passa a ser 100% do Liquibase.

### 3. Execute a aplicação

```bash
# Usando o Maven Wrapper (recomendado)
./mvnw spring-boot:run
```

### 4. O que acontece ao iniciar?

O Spring Boot detecta automaticamente o `DatabaseSeeder` (que implementa `CommandLineRunner`) e executa a seguinte sequência:

```
Aplicação inicia
  → Contexto Spring carregado
    → DatabaseSeeder.run() chamado automaticamente
      → ✅ Usuários criados / verificados
      → ✅ Produtos criados / verificados
      → ✅ Pedidos criados / verificados
      → ✅ Queries JPQL executadas
      → ✅ Native Queries executadas
```

---

## 🧪 Liquibase — Versionamento de Banco de Dados

O **Liquibase** é uma ferramenta de **Database Version Control** integrada ao Spring Boot. Em vez de criar o schema manualmente ou depender do `ddl-auto: create` do Hibernate, o Liquibase controla cada alteração no banco por meio de arquivos de migração chamados **ChangeSets**.

### Por que usar o Liquibase?

| Situação | Sem Liquibase | Com Liquibase |
|---|---|---|
| Novo desenvolvedor entra no projeto | Precisa rodar scripts manualmente | `./mvnw spring-boot:run` já aplica tudo |
| Schema muda em produção | SQL manual com risco de erro | Migration versionada e auditada |
| Rollback necessário | Complexo e arriscado | Suporte nativo a `rollback` |
| Ambientes diferentes (dev/prod) | Scripts inconsistentes | Um único changelog para todos |

---

### 📦 Dependência no `pom.xml`

```xml
<dependency>
    <groupId>org.liquibase</groupId>
    <artifactId>liquibase-core</artifactId>
    <!-- Versão gerenciada pelo Spring Boot Parent -->
</dependency>
```

---

### 🗂️ Estrutura dos Changelogs

O Liquibase é configurado para buscar o arquivo entry point em `classpath:db/changelog/db.changelog-master.yml`.
Esse arquivo **orquestra** todos os demais na ordem correta:

```yaml
# db.changelog-master.yml
databaseChangeLog:
  # STEP 1: Cria o schema (tabelas, índices, FKs)
  - include:
      file: db/changelog/01-create-schema.yml

  # STEP 2: Seed de dados base (tabelas independentes)
  - include:
      file: db/changelog/02-seed-users.sql
  - include:
      file: db/changelog/03-seed-products.sql

  # STEP 3: Seed relacional (tabelas com FK)
  - include:
      file: db/changelog/04-seed-orders.sql
  - include:
      file: db/changelog/05-seed-order-items.sql
  - include:
      file: db/changelog/06-seed-product-reviews.sql
```

> **⚠️ Regra de Ouro:** Nunca altere a **ordem** dos includes após o primeiro deploy. O Liquibase rastreia cada arquivo pelo hash — reordenar quebra a consistência entre ambientes.

---

### 📝 Conceitos Fundamentais

#### ChangeSet
A unidade mínima de migração. Cada `changeSet` tem um `id` único e um `author`, formando uma identidade imutável.

```yaml
# Exemplo de changeSet em 01-create-schema.yml
- changeSet:
    id: 1
    author: ordersystem
    changes:
      - createTable:
          tableName: users
          columns:
            - column:
                name: id
                type: BIGINT
                autoIncrement: true
                constraints:
                  primaryKey: true
```

#### DATABASECHANGELOG
Tabela interna criada automaticamente pelo Liquibase no banco. Registra cada `changeSet` executado com seu hash MD5, data e autor — garantindo que cada migration rode **apenas uma vez**.

| Coluna | Descrição |
|---|---|
| `ID` | ID do changeSet |
| `AUTHOR` | Autor declarado |
| `FILENAME` | Arquivo de origem |
| `DATEEXECUTED` | Data de execução |
| `MD5SUM` | Hash do conteúdo — detecta alterações indevidas |
| `EXECTYPE` | `EXECUTED` ou `RERAN` |

#### DATABASECHANGELOGLOCK
Tabela de lock para garantir que apenas **uma instância** da aplicação rode migrations ao mesmo tempo (essencial em deploys com múltiplas réplicas).

---

### 🔄 Fluxo de Execução

Cada vez que a aplicação sobe, o Liquibase executa o seguinte ciclo:

```
Aplicação inicia
  → Liquibase adquire o LOCK no banco
    → Lê db.changelog-master.yml
      → Para cada changeSet:
          ✅ Já aplicado (hash igual)?  → Pula
          ⚠️  Hash diferente?           → Lança ERRO (alteração indevida)
          🆕 Ainda não aplicado?        → Executa e registra no DATABASECHANGELOG
    → Libera o LOCK
      → Spring Boot continua a inicialização
        → Hibernate valida o schema (ddl-auto: validate)
          → DatabaseSeeder executa as queries de demonstração
```

---

### 📋 ChangeSets do Projeto

O arquivo `01-create-schema.yml` contém **14 ChangeSets** que criam todo o schema:

| ID | Descrição |
|---|---|
| 1 | Cria tabela `users` |
| 2 | Cria tabela `products` |
| 3 | Cria tabela `orders` |
| 4 | FK `orders.user_id → users.id` (CASCADE) |
| 5 | Índice em `orders.user_id` |
| 6 | Cria tabela `order_items` |
| 7 | FK `order_items.product_id → products.id` (CASCADE) |
| 8 | FK `order_items.order_id → orders.id` (CASCADE) |
| 9 | Índices em `order_items` (product_id, order_id) |
| 10 | Cria tabela `product_reviews` (PK composta) |
| 11 | Adiciona PK composta `(user_id, product_id)` |
| 12 | FK `product_reviews.user_id → users.id` (CASCADE) |
| 13 | FK `product_reviews.product_id → products.id` (CASCADE) |
| 14 | Índice em `product_reviews.product_id` |

---

### 🖼️ Evidências do Liquibase em Execução

ER do banco gerado via Liquibase:

![ER do Banco de Dados com Liquibase](src/main/resources/doc/Database_liquibase_diagram_2026-06-24%2000-11-29.png)

Tabela `DATABASECHANGELOG` no MySQL:

![Tabela DATABASECHANGELOG](src/main/resources/doc/Database_liquibase_table_2026-06-24%2000-11-29.png)

Logs de execução das migrations:

![Logs de execução com Liquibase](src/main/resources/doc/Database_run_liquibase_2026-06-24%2000-11-29.png)

---

### 🔧 Comandos Úteis do Liquibase via Maven

```bash
# Ver o status de todas as migrations (quais foram aplicadas)
./mvnw liquibase:status

# Gerar um changelog a partir do banco atual (útil para documentar)
./mvnw liquibase:generateChangeLog

# Validar o changelog sem executar
./mvnw liquibase:validate

# Rollback da última migration (requer rollbackTag)
./mvnw liquibase:rollback -Dliquibase.rollbackTag=1.0
```

---

## 🔍 Demonstração: JPQL e Native Queries

O `DatabaseSeeder` demonstra dois estilos de consulta avançada:

### JPQL (Java Persistence Query Language)
Consultas orientadas a objetos que operam sobre as entidades JPA:

```java
// Buscar produtos com estoque disponível
@Query("SELECT p FROM Product p WHERE p.stock > 0")
List<Product> findAvailableProducts();

// Buscar usuário por e-mail
@Query("SELECT u FROM User u WHERE u.email = :email")
Optional<User> findByEmailUsingJPQL(@Param("email") String email);
```

### Native Queries
Consultas SQL puras executadas diretamente no banco:

```java
// Calcular valor total em inventário
@Query(value = "SELECT IFNULL(SUM(price * stock), 0) FROM products", nativeQuery = true)
BigDecimal calculateTotalInventoryValue();

// Contar pedidos agrupados por status
@Query(value = "SELECT status, COUNT(*) as count FROM orders GROUP BY status", nativeQuery = true)
List<Object[]> countOrdersByStatus();
```

---

## 🖥️ Exemplo de Execução

A imagem abaixo mostra o terminal do IntelliJ IDEA durante a execução do projeto, com o `DatabaseSeeder` em ação — exibindo as queries SQL geradas pelo Hibernate e os resultados das consultas JPQL e Native:

![Execução do DatabaseSeeder no terminal do VS Code](src/main/resources/doc/DatabaseRun_2026-06-23%2023-25-48.png)

> **Destaques da saída:**
> - **Hibernate** gera e executa o SQL automaticamente para as queries JPQL
> - `[JPQL]` Produtos disponíveis encontrados: **Notebook Dell** e **Mouse Sem Fio**
> - `[JPQL]` Usuário localizado por e-mail: **Maria Silva**
> - `[NATIVE]` Valor total do inventário: **R$ 153.000,00**
> - `[NATIVE]` Pedidos com status `PENDING`: **3 pedidos**

---

## 🧩 Modelo de Dados

```
┌─────────────┐       ┌──────────────┐       ┌────────────────┐
│    users    │       │    orders    │       │  order_items   │
│─────────────│       │──────────────│       │────────────────│
│ id (PK)     │──────<│ id (PK)      │──────<│ id (PK)        │
│ name        │       │ total        │       │ quantity       │
│ email       │       │ status       │       │ subtotal       │
│ created_at  │       │ user_id (FK) │       │ order_id (FK)  │
└──────┬──────┘       │ created_at   │       │ product_id(FK) │
       │              └──────────────┘       └───────┬────────┘
       │                                             │
       │              ┌───────────────┐              │
       │         ┌───<│   products    │──────────────┘
       │         │    │───────────────│
       │         │    │ id (PK)       │
       │         │    │ name          │
       │         │    │ price         │
       │         │    │ stock         │
       │         │    │ created_at    │
       │         │    └───────────────┘
       │         │
       │         │    ┌──────────────────┐
       └────────>│    │  product_reviews  │
                 └───<│──────────────────│
                      │ product_id (FK) ─┤ PK composta
                      │ user_id (FK)    ─┘
                      │ comment          │
                      │ rating           │
                      │ created_at       │
                      └──────────────────┘
```

---

![ER do Banco de Dados](src/main/resources/doc/Database_EER_2026-06-24%2000-11-29.png)

## 👨‍💻 Autor

**Artanniel Fortes** — Aluno UniPDS  
Disciplina: Banco de Dados com JPA

---

> 📚 *Projeto desenvolvido com fins educacionais para demonstração de padrões de persistência com Spring Data JPA.*
