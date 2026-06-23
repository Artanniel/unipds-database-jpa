package br.com.unipds.artantech.ordersystem_jpa.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade JPA que representa um item de pedido (produto + quantidade).
 *
 * Mapeamento para a tabela 'order_items' no banco de dados MySQL.
 *
 * CONCEITOS JPA DEMONSTRADOS:
 * - Entidade intermediaria: Representa a relacao N:N entre Order e Product
 * - Relacionamento N:N com atributos extras (quantity, subtotal)
 * - Multiplos @ManyToOne: Um OrderItem pertence a um Order E a um Product
 * - Lado filho em relacionamento pai-filho: Order gerencia o ciclo de vida
 * - SEM cascade: OrderItem nao propaga operacoes para Order ou Product
 *
 * ESTRUTURA DE DADOS:
 * Order 1 ----< N Ord
 * erItem >---- 1 Product
 *
 * Esta e a forma correta de implementar N:N quando voce precisa armazenar
 * informacoes adicionais sobre a relacao (neste caso: quantity e subtotal).
 *
 * @see Order
 * @see Product
 * @see br.com.unipds.artantech.ordersystem_jpa.repository.repository.OrderItemRepository
 */
@Entity
@Table(name = "order_items")
public class OrderItem {

    /**
     * Identificador unico do item (chave primaria).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Quantidade do produto neste item.
     * Obrigatorio (nullable=false).
     */
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Subtotal do item (preco unitario * quantidade).
     *
     * IMPORTANTE: Este valor deve ser calculado na camada de servico
     * antes de persistir o item:
     * subtotal = product.getPrice().multiply(BigDecimal.valueOf(quantity))
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal;

    /**
     * Data e hora de criacao do item.
     * Gerenciado pelo banco via DEFAULT CURRENT_TIMESTAMP.
     */
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Produto incluido neste item.
     *
     * Relacionamento Many-to-One (N:1):
     * - Muitos itens podem referenciar o mesmo produto
     * - FetchType.LAZY: Product e carregado somente quando acessado
     * - optional=false: Item DEVE ter um produto
     * - SEM cascade: Operacoes em OrderItem NAO afetam Product
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * Pedido ao qual este item pertence.
     *
     * Relacionamento Many-to-One (N:1):
     * - Muitos itens pertencem a um pedido
     * - FetchType.LAZY: Order e carregado somente quando acessado
     * - optional=false: Item DEVE pertencer a um pedido
     * - SEM cascade: Operacoes em OrderItem NAO afetam Order
     *
     * IMPORTANTE: Order gerencia este relacionamento via cascade.
     * Nao crie/remova OrderItems diretamente, use Order.addItem()/removeItem().
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * Construtor padrao necessario para o JPA.
     */
    public OrderItem() {
    }

    /**
     * Construtor de conveniencia.
     *
     * @param quantity Quantidade do produto
     * @param product  Produto referenciado
     */
    public OrderItem(Integer quantity, Product product) {
        this.quantity = quantity;
        this.product = product;
    }

    // ==================== Getters and Setters ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(id, orderItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", subtotal=" + subtotal +
                ", createdAt=" + createdAt +
                ", productId=" + (product != null ? product.getId() : null) +
                ", orderId=" + (order != null ? order.getId() : null) +
                '}';
    }
}