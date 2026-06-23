package br.com.unipds.artantech.ordersystem_jpa.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.unipds.artantech.ordersystem_jpa.enums.OrderStatus;

/**
 * Entidade JPA que representa um pedido no sistema.
 *
 * Mapeamento para a tabela 'orders' no banco de dados MySQL.
 *
 * CONCEITOS JPA DEMONSTRADOS:
 * - @Enumerated(EnumType.STRING): Armazena o nome do enum ao inves do ordinal
 * - @ManyToOne: Relacionamento muitos-para-um com User
 * - FetchType.LAZY: Carregamento preguicoso para otimizar performance
 * - @JoinColumn: Define a coluna de chave estrangeira (FK)
 * - optional=false: Torna o relacionamento obrigatorio (NOT NULL na FK)
 * - Relacionamento pai-filho: Order e o pai, OrderItem e o filho
 * - Cascade e OrphanRemoval: Gerenciamento completo do ciclo de vida dos filhos
 *
 * @see User
 * @see OrderItem
 * @see OrderStatus
 * @see br.com.unipds.ordersystemjpa.repository.OrderRepository
 */
@Entity
@Table(name = "orders")
public class Order {

    /**
     * Identificador unico do pedido (chave primaria).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Valor total do pedido.
     * Geralmente calculado como a soma dos subtotais dos itens.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    /**
     * Status atual do pedido.
     *
     * @Enumerated(EnumType.STRING) armazena 'CREATED', 'PAID' ou 'CANCELLED'
     *                              ao inves de 0, 1, 2 (que seria
     *                              EnumType.ORDINAL).
     *                              Isso torna o banco de dados mais legivel e
     *                              seguro contra reordenacao do enum.
     */
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('CREATED', 'PAID', 'CANCELLED') DEFAULT 'CREATED'")
    private OrderStatus status;

    /**
     * Data e hora de criacao do pedido.
     * Gerenciado automaticamente pelo banco via DEFAULT CURRENT_TIMESTAMP.
     */
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Usuario que realizou o pedido.
     *
     * Relacionamento Many-to-One (N:1):
     * - Muitos pedidos pertencem a um usuario
     * - FetchType.LAZY: User e carregado somente quando acessado (evita N+1)
     * - optional=false: Pedido DEVE ter um usuario (NOT NULL)
     * - @JoinColumn(name="user_id"): Define a FK na tabela orders
     *
     * IMPORTANTE: Este e o lado OWNER do relacionamento (tem a FK).
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Lista de itens incluidos neste pedido.
     *
     * Relacionamento One-to-Many pai-filho (1:N):
     * - mappedBy="order": OrderItem.order e o lado owner
     * - cascade=ALL: Todas operacoes (persist, merge, remove) sao propagadas
     * - orphanRemoval=true: Itens removidos da lista sao deletados do banco
     * - FetchType padrao: LAZY
     *
     * Use SEMPRE os metodos addItem() e removeItem() para manipular esta lista!
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    /**
     * Construtor padrao necessario para o JPA.
     */
    public Order() {
    }

    /**
     * Construtor de conveniencia.
     *
     * @param total Valor total do pedido
     * @param user  Usuario que realizou o pedido
     */
    public Order(BigDecimal total, User user) {
        this.total = total;
        this.user = user;
    }

    /**
     * Metodos auxiliares para gerenciar o relacionamento bidirecional Order <->
     * OrderItem.
     *
     * Estes metodos sao necessarios para:
     * 1. Manter a sincronizacao entre ambos os lados do relacionamento (Order.items
     * e OrderItem.order)
     * 2. Garantir a integridade referencial antes da persistencia no banco
     * 3. Facilitar o uso correto do cascade e orphanRemoval configurados
     * em @OneToMany
     *
     * IMPORTANTE: Sempre use estes metodos ao inves de manipular diretamente a
     * lista items.
     *
     * Exemplo de uso correto:
     * 
     * <pre>
     * Order order = new Order(total, user);
     * OrderItem item = new OrderItem(quantity, product);
     * order.addItem(item); // Sincroniza automaticamente item.setOrder(this)
     * entityManager.persist(order); // O item sera persistido pelo cascade
     * </pre>
     *
     * Uso INCORRETO (NAO fazer):
     * 
     * <pre>
     * order.getItems().add(item); // Nao sincroniza item.order - causa inconsistencia!
     * </pre>
     */

    /**
     * Adiciona um item ao pedido e sincroniza o relacionamento bidirecional.
     *
     * @param item O item a ser adicionado ao pedido
     */
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    /**
     * Remove um item do pedido e sincroniza o relacionamento bidirecional.
     * Com orphanRemoval=true, o item sera automaticamente deletado do banco.
     *
     * @param item O item a ser removido do pedido
     */
    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }

    // ==================== Getters and Setters ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", total=" + total +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", userId=" + (user != null ? user.getId() : null) +
                '}';
    }
}