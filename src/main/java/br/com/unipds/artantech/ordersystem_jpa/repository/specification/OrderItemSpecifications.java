package br.com.unipds.artantech.ordersystem_jpa.repository.specification;

import br.com.unipds.artantech.ordersystem_jpa.entity.OrderItem;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

/**
 * Specifications (Criteria API) para a entidade OrderItem.
 *
 * @see OrderItem
 * @see br.com.unipds.ordersystemjpa.repository.OrderItemRepository
 */
public class OrderItemSpecifications {

    /**
     * Specification: Itens de um pedido especifico.
     *
     * Equivalente JPQL: WHERE oi.order.id = :orderId
     */
    public static Specification<OrderItem> byOrder(Long orderId) {
        return (root, query, criteriaBuilder) -> orderId == null ? null
                : criteriaBuilder.equal(root.get("order").get("id"), orderId);
    }

    /**
     * Specification: Itens de um produto especifico.
     *
     * Equivalente JPQL: WHERE oi.product.id = :productId
     */
    public static Specification<OrderItem> byProduct(Long productId) {
        return (root, query, criteriaBuilder) -> productId == null ? null
                : criteriaBuilder.equal(root.get("product").get("id"), productId);
    }

    /**
     * Specification: Itens com quantidade maior que o valor especificado.
     *
     * Equivalente JPQL: WHERE oi.quantity > :quantity
     */
    public static Specification<OrderItem> quantityGreaterThan(Integer quantity) {
        return (root, query, criteriaBuilder) -> quantity == null ? null
                : criteriaBuilder.greaterThan(root.get("quantity"), quantity);
    }

    /**
     * Specification: Itens com subtotal maior que o valor especificado.
     *
     * Equivalente JPQL: WHERE oi.subtotal > :subtotal
     */
    public static Specification<OrderItem> subtotalGreaterThan(BigDecimal subtotal) {
        return (root, query, criteriaBuilder) -> subtotal == null ? null
                : criteriaBuilder.greaterThan(root.get("subtotal"), subtotal);
    }

    /**
     * Specification: Itens de pedidos de um usuario especifico.
     *
     * Equivalente JPQL: WHERE oi.order.user.id = :userId
     *
     * Demonstra navegacao em multiplos relacionamentos.
     */
    public static Specification<OrderItem> byUser(Long userId) {
        return (root, query, criteriaBuilder) -> userId == null ? null
                : criteriaBuilder.equal(root.get("order").get("user").get("id"), userId);
    }
}