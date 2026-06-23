package br.com.unipds.artantech.ordersystem_jpa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidade JPA que representa um usuario do sistema de pedidos.
 *
 * Mapeamento para a tabela 'users' no banco de dados MySQL.
 *
 * CONCEITOS JPA DEMONSTRADOS:
 * - @Entity: Marca a classe como uma entidade JPA
 * - @Table: Define o nome da tabela no banco (padrao seria o nome da classe)
 * - @Id: Define a chave primaria
 * - @GeneratedValue: Configura auto-incremento (IDENTITY usa AUTO_INCREMENT do
 * MySQL)
 * - @Column: Personaliza o mapeamento de colunas (nullable, unique, length,
 * etc)
 * - @OneToMany: Relacionamento um-para-muitos com Order
 * - mappedBy: Indica que Order e o lado owner do relacionamento
 * - cascade: Propaga operacoes (persist, merge, remove) para os pedidos
 * - orphanRemoval: Remove pedidos orfaos (sem usuario) automaticamente
 *
 * @see Order
 * @see br.com.unipds.ordersystemjpa.repository.UserRepository
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * Identificador unico do usuario (chave primaria).
     * Gerado automaticamente pelo banco de dados (AUTO_INCREMENT).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nome completo do usuario.
     * Obrigatorio (nullable=false) com tamanho maximo de 100 caracteres.
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Email do usuario, usado como identificador unico.
     * Obrigatorio e unico no sistema (unique=true).
     * Tamanho maximo de 150 caracteres.
     */
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    /**
     * Data e hora de criacao do usuario.
     *
     * IMPORTANTE: insertable=false e updatable=false fazem com que o JPA
     * nao inclua este campo nos comandos INSERT e UPDATE.
     * O valor e gerenciado pelo banco de dados via DEFAULT CURRENT_TIMESTAMP.
     */
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Lista de pedidos associados a este usuario.
     *
     * Relacionamento bidirecional One-to-Many (1:N):
     * - Um usuario pode ter muitos pedidos
     * - mappedBy="user" indica que Order.user e o lado owner (tem a FK)
     * - cascade=ALL: Operacoes em User sao propagadas para Orders
     * - orphanRemoval=true: Se um Order for removido da lista, sera deletado do
     * banco
     * - FetchType padrao: LAZY (carrega pedidos somente quando acessados)
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    /**
     * Construtor padrao necessario para o JPA.
     * O JPA precisa deste construtor para instanciar objetos via reflection.
     */
    public User() {
    }

    /**
     * Construtor de conveniencia para criar um usuario com nome e email.
     *
     * @param name  Nome completo do usuario
     * @param email Email unico do usuario
     */
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // ==================== Getters and Setters ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    /**
     * Compara dois usuarios com base no ID.
     *
     * IMPORTANTE: Em entidades JPA, equals() e hashCode() devem ser baseados
     * apenas no ID (chave primaria) para garantir comportamento correto em
     * collections (Set, Map) e no gerenciamento de sessoes do Hibernate.
     *
     * @param o Objeto a ser comparado
     * @return true se os usuarios tem o mesmo ID
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    /**
     * Gera o hash code baseado no ID.
     * Deve ser consistente com equals().
     *
     * @return hash code do usuario
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Representacao em String do usuario.
     * Nao inclui a lista de pedidos para evitar loops infinitos em relacionamentos
     * bidirecionais.
     *
     * @return String formatada com os dados do usuario
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}