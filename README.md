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
│       └── application.yml
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

As credenciais já estão configuradas para apontar para o container:

```yaml
# src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/unipds-database
    username: usuarioSeguro
    password: s3nH4SuP3rS3gur4
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

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

ER do Banco de Dados com flyway:
![ER do Banco de Dados com flyway](src/main/resources/doc/Database_EER_With_Flyway_2026-06-24%2000-11-29.png)

Flyway tables:
![Flyway tables](src/main/resources/doc/Database_Flyway_2026-06-24%2000-11-29.png)

Users table:
![Users table](src/main/resources/doc/Database_Users_2026-06-24%2000-11-29.png)



## 👨‍💻 Autor

**Artanniel Fortes** — Aluno UniPDS  
Disciplina: Banco de Dados com JPA

---

> 📚 *Projeto desenvolvido com fins educacionais para demonstração de padrões de persistência com Spring Data JPA.*
