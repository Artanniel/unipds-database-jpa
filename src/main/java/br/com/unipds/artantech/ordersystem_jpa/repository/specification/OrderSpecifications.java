package br.com.unipds.artantech.ordersystem_jpa.repository.specification;

import br.com.unipds.artantech.ordersystem_jpa.entity.Order;
import br.com.unipds.artantech.ordersystem_jpa.enums.OrderStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Specifications (Criteria API) para a entidade Order.
 *
 * CONCEITOS DEMONSTRADOS - SPECIFICATIONS:
 *
 * O QUE SAO SPECIFICATIONS?
 * - Forma type-safe de construir queries dinamicas usando Criteria API
 * - Alternativa ao JPQL para queries complexas e condicionais
 * - Permite composicao de criterios (AND, OR, NOT)
 * - Reusaveis e testaveis
 *
 * QUANDO USAR SPECIFICATIONS?
 * 1. Queries dinamicas: Filtros opcionais baseados em input do usuario
 * 2. Criterios reutilizaveis: Mesma logica em multiplas queries
 * 3. Queries complexas: Multiplas condicoes combinadas
 * 4. Type-safety: Evita erros de string em JPQL
 *
 * EXEMPLO DE USO EM CONTROLLER/SERVICE:
 * ```
 * public List<Order> searchOrders(Long userId, OrderStatus status,
 * LocalDateTime from, LocalDateTime to) {
 * Specification<Order> spec = Specification.where(null);
 *
 * if (userId != null) {
 * spec = spec.and(OrderSpecifications.byUser(userId));
 * }
 *
 * if (status != null) {
 * spec = spec.and(OrderSpecifications.byStatus(status));
 * }
 *
 * if (from != null || to != null) {
 * spec = spec.and(OrderSpecifications.createdBetween(from, to));
 * }
 *
 * return orderRepository.findAll(spec);
 * }
 * ```
 *
 * @see Order
 * @see br.com.unipds.ordersystemjpa.repository.OrderRepository
 * @see org.springframework.data.jpa.domain.Specification
 */
public class OrderSpecifications {

    /**
     * Specification: Pedidos de um usuario especifico.
     *
     * Equivalente JPQL: WHERE o.user.id = :userId
     *
     * ESTRUTURA:
     * - root.get("user"): Acessa o relacionamento @ManyToOne com User
     * - .get("id"): Navega para o ID do User
     * - criteriaBuilder.equal(): Cria predicado de igualdade (=)
     *
     * USO:
     * ```
     * orderRepository.findAll(OrderSpecifications.byUser(userId));
     * ```
     *
     * @param userId ID do usuario
     * @return Specification que filtra por usuario
     */
    public static Specification<Order> byUser(Long userId) {
        return (root, query, criteriaBuilder) -> userId == null ? null
                : criteriaBuilder.equal(root.get("user").get("id"), userId);
    }

    /**
     * Specification: Pedidos com status especifico.
     *
     * Equivalente JPQL: WHERE o.status = :status
     *
     * ESTRUTURA:
     * - root.get("status"): Acessa propriedade status (enum)
     * - criteriaBuilder.equal(): Cria predicado de igualdade
     *
     * USO:
     * ```
     * orderRepository.findAll(OrderSpecifications.byStatus(OrderStatus.PAID));
     * ```
     *
     * @param status Status do pedido
     * @return Specification que filtra por status
     */
    public static Specification<Order> byStatus(OrderStatus status) {
        return (root, query, criteriaBuilder) -> status == null ? null
                : criteriaBuilder.equal(root.get("status"), status);
    }

    /**
     * Specification: Pedidos criados apos uma data especifica.
     *
     * Equivalente JPQL: WHERE o.createdAt > :date
     *
     * ESTRUTURA:
     * - root.get("createdAt"): Acessa propriedade createdAt
     * - criteriaBuilder.greaterThan(): Cria predicado > (maior que)
     *
     * USO:
     * ```
     * LocalDateTime lastWeek = LocalDateTime.now().minusDays(7);
     * orderRepository.findAll(OrderSpecifications.createdAfter(lastWeek));
     * ```
     *
     * @param date Data de referencia
     * @return Specification que filtra por data de criacao
     */
    public static Specification<Order> createdAfter(LocalDateTime date) {
        return (root, query, criteriaBuilder) -> date == null ? null
                : criteriaBuilder.greaterThan(root.get("createdAt"), date);
    }

    /**
     * Specification: Pedidos criados antes de uma data especifica.
     *
     * Equivalente JPQL: WHERE o.createdAt < :date
     *
     * @param date Data de referencia
     * @return Specification que filtra por data de criacao
     */
    public static Specification<Order> createdBefore(LocalDateTime date) {
        return (root, query, criteriaBuilder) -> date == null ? null
                : criteriaBuilder.lessThan(root.get("createdAt"), date);
    }

    /**
     * Specification: Pedidos criados em um intervalo de datas.
     *
     * Equivalente JPQL: WHERE o.createdAt BETWEEN :startDate AND :endDate
     *
     * COMPOSICAO:
     * Este metodo usa criteriaBuilder.between() para criar o predicado.
     *
     * USO:
     * ```
     * LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0);
     * LocalDateTime end = LocalDateTime.of(2025, 12, 31, 23, 59);
     * orderRepository.findAll(OrderSpecifications.createdBetween(start, end));
     * ```
     *
     * @param startDate Data inicial
     * @param endDate   Data final
     * @return Specification que filtra por intervalo de datas
     */
    public static Specification<Order> createdBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null && endDate == null)
                return null;
            if (startDate == null)
                return criteriaBuilder.lessThan(root.get("createdAt"), endDate);
            if (endDate == null)
                return criteriaBuilder.greaterThan(root.get("createdAt"), startDate);
            return criteriaBuilder.between(root.get("createdAt"), startDate, endDate);
        };
    }

    /**
     * Specification: Pedidos com total maior que o valor especificado.
     *
     * Equivalente JPQL: WHERE o.total > :total
     *
     * ESTRUTURA:
     * - root.get("total"): Acessa propriedade total (BigDecimal)
     * - criteriaBuilder.greaterThan(): Cria predicado >
     *
     * USO:
     * ```
     * BigDecimal minTotal = BigDecimal.valueOf(1000);
     * orderRepository.findAll(OrderSpecifications.totalGreaterThan(minTotal));
     * ```
     *
     * @param total Valor minimo do pedido
     * @return Specification que filtra por total
     */
    public static Specification<Order> totalGreaterThan(BigDecimal total) {
        return (root, query, criteriaBuilder) -> total == null ? null
                : criteriaBuilder.greaterThan(root.get("total"), total);
    }

    /**
     * Specification: Pedidos com total menor que o valor especificado.
     *
     * Equivalente JPQL: WHERE o.total < :total
     *
     * @param total Valor maximo do pedido
     * @return Specification que filtra por total
     */
    public static Specification<Order> totalLessThan(BigDecimal total) {
        return (root, query, criteriaBuilder) -> total == null ? null
                : criteriaBuilder.lessThan(root.get("total"), total);
    }

    /**
     * Specification: Pedidos em uma faixa de valores.
     *
     * Equivalente JPQL: WHERE o.total BETWEEN :minTotal AND :maxTotal
     *
     * @param minTotal Valor minimo
     * @param maxTotal Valor maximo
     * @return Specification que filtra por faixa de valores
     */
    public static Specification<Order> totalBetween(BigDecimal minTotal, BigDecimal maxTotal) {
        return (root, query, criteriaBuilder) -> {
            if (minTotal == null && maxTotal == null)
                return null;
            if (minTotal == null)
                return criteriaBuilder.lessThanOrEqualTo(root.get("total"), maxTotal);
            if (maxTotal == null)
                return criteriaBuilder.greaterThanOrEqualTo(root.get("total"), minTotal);
            return criteriaBuilder.between(root.get("total"), minTotal, maxTotal);
        };
    }

    // ==================== EXEMPLO DE COMPOSICAO ====================

    /**
     * EXEMPLO EDUCACIONAL: Composicao de multiplos criterios.
     *
     * Este metodo NAO precisa existir aqui, e apenas um exemplo de como
     * COMPOR Specifications no codigo que usa o repository.
     *
     * Busca pedidos:
     * - De um usuario especifico
     * - Com status especifico
     * - Criados apos uma data
     * - Com total acima de um valor
     *
     * USO (no Service ou Controller):
     * ```
     * Specification<Order> spec = Specification
     * .where(OrderSpecifications.byUser(userId))
     * .and(OrderSpecifications.byStatus(OrderStatus.PAID))
     * .and(OrderSpecifications.createdAfter(lastMonth))
     * .and(OrderSpecifications.totalGreaterThan(BigDecimal.valueOf(100)));
     *
     * List<Order> orders = orderRepository.findAll(spec);
     * ```
     *
     * EXEMPLO COM FILTROS OPCIONAIS:
     * ```
     * public List<Order> searchOrders(OrderSearchDTO filters) {
     * Specification<Order> spec = Specification.where(null);
     *
     * if (filters.getUserId() != null) {
     * spec = spec.and(OrderSpecifications.byUser(filters.getUserId()));
     * }
     *
     * if (filters.getStatus() != null) {
     * spec = spec.and(OrderSpecifications.byStatus(filters.getStatus()));
     * }
     *
     * if (filters.getStartDate() != null || filters.getEndDate() != null) {
     * spec = spec.and(OrderSpecifications.createdBetween(
     * filters.getStartDate(), filters.getEndDate()));
     * }
     *
     * if (filters.getMinTotal() != null || filters.getMaxTotal() != null) {
     * spec = spec.and(OrderSpecifications.totalBetween(
     * filters.getMinTotal(), filters.getMaxTotal()));
     * }
     *
     * return orderRepository.findAll(spec);
     * }
     * ```
     *
     * VANTAGENS DA COMPOSICAO:
     * - Criterios sao opcionais (if userId != null)
     * - Reutilizacao de logica (byUser, byStatus, etc)
     * - Type-safe (erros em compile time)
     * - Testavel (pode testar cada Specification separadamente)
     */
}