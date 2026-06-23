package br.com.unipds.artantech.ordersystem_jpa.repository;

import br.com.unipds.artantech.ordersystem_jpa.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository Spring Data JPA para a entidade OrderItem.
 *
 * CONCEITOS SPRING DATA JPA DEMONSTRADOS:
 * - JpaRepository<OrderItem, Long>: Interface generica que fornece CRUD
 * completo
 * - JpaSpecificationExecutor<OrderItem>: Permite queries dinamicas com Criteria
 * API
 * - Query Methods: Spring gera implementacao baseado no nome do metodo
 * - @Query com JPQL: Queries customizadas em Java Persistence Query Language
 * - @Query com nativeQuery: Queries SQL nativas do banco de dados
 * - Specifications: Queries dinamicas e reusaveis (Criteria API)
 *
 * COMPARACAO DAS ABORDAGENS:
 *
 * 1. QUERY METHOD (Mais simples):
 * - Vantagem: Sem codigo, apenas nome do metodo
 * - Desvantagem: Limitado, nomes ficam grandes
 * - Exemplo: findByOrderId(Long orderId)
 *
 * 2. JPQL - Java Persistence Query Language (Portavel):
 * - Vantagem: Orientado a objetos, independente do banco
 * - Desvantagem: Sintaxe propria para aprender
 * - Exemplo: @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId")
 *
 * 3. NATIVE QUERY (Especifico do banco):
 * - Vantagem: SQL puro, recursos especificos do MySQL
 * - Desvantagem: Nao portavel entre bancos
 * - Exemplo: @Query(value = "SELECT * FROM order_items WHERE order_id = ?",
 * nativeQuery = true)
 *
 * 4. SPECIFICATION (Queries dinamicas):
 * - Vantagem: Reusavel, composicao de criterios, type-safe
 * - Desvantagem: Mais verboso, curva de aprendizado
 * - Exemplo: findAll(OrderItemSpecifications.byOrder(orderId))
 *
 * @see OrderItem
 * @see br.com.unipds.ordersystemjpa.repository.specification.OrderItemSpecifications
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long>, JpaSpecificationExecutor<OrderItem> {

        // ==================== QUERY METHODS ====================

        /**
         * Busca todos os itens de um pedido especifico.
         *
         * Query Method: Spring navega o relacionamento @ManyToOne
         * e gera: SELECT * FROM order_items WHERE order_id = ?
         *
         * @param orderId ID do pedido
         * @return Lista de itens do pedido
         */
        List<OrderItem> findByOrderId(Long orderId);

        /**
         * Busca todos os itens que referenciam um produto especifico.
         *
         * Query Method com navegacao.
         * Util para relatorios: "Quantas vezes este produto foi vendido?"
         *
         * @param productId ID do produto
         * @return Lista de itens contendo o produto
         */
        List<OrderItem> findByProductId(Long productId);

        /**
         * Busca itens com quantidade maior que o valor especificado.
         *
         * Query Method com comparacao numerica.
         * Spring gera: SELECT * FROM order_items WHERE quantity > ?
         *
         * @param quantity Quantidade minima
         * @return Lista de itens com quantidade maior
         */
        List<OrderItem> findByQuantityGreaterThan(Integer quantity);

        // ==================== JPQL QUERIES ====================

        /**
         * Busca itens de um pedido usando JPQL.
         *
         * JPQL (Java Persistence Query Language):
         * - Usa nomes de ENTIDADES (OrderItem) ao inves de TABELAS (order_items)
         * - Navegacao de propriedade: oi.order.id acessa o ID do Order relacionado
         * - Independente do banco de dados (portavel)
         *
         * EQUIVALENTE Query Method: findByOrderId(Long orderId)
         *
         * EDUCACIONAL: Esta query faz exatamente o mesmo que findByOrderId().
         * Demonstra que Query Methods sao apenas atalhos para queries JPQL.
         *
         * @param orderId ID do pedido
         * @return Lista de itens do pedido
         */
        @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId")
        List<OrderItem> findItemsByOrderUsingJPQL(@Param("orderId") Long orderId);

        /**
         * Busca itens de pedidos pagos de um usuario usando JPQL.
         *
         * JPQL com multiplas navegacoes:
         * - oi.order.user.id: Navega OrderItem -> Order -> User -> id
         * - oi.order.status: Navega OrderItem -> Order -> status
         *
         * Demonstra navegacao em cadeia de relacionamentos.
         *
         * @param userId ID do usuario
         * @return Lista de itens de pedidos pagos do usuario
         */
        @Query("SELECT oi FROM OrderItem oi WHERE oi.order.user.id = :userId AND oi.order.status = 'PAID'")
        List<OrderItem> findPaidItemsByUser(@Param("userId") Long userId);

        /**
         * Calcula a quantidade total vendida de um produto usando JPQL.
         *
         * JPQL com agregacao SUM:
         * - SUM(oi.quantity): Soma todas as quantidades
         * - WHERE oi.product.id: Filtra por produto
         *
         * Util para relatorios de vendas.
         *
         * @param productId ID do produto
         * @return Quantidade total vendida
         */
        @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.product.id = :productId")
        Long calculateTotalQuantitySold(@Param("productId") Long productId);

        /**
         * Busca itens com produto e pedido carregados (JOIN FETCH).
         *
         * JPQL com multiplos JOIN FETCH:
         * - JOIN FETCH oi.product: Carrega Product relacionado
         * - JOIN FETCH oi.order: Carrega Order relacionado
         * - Evita problema N+1
         *
         * @param orderId ID do pedido
         * @return Lista de itens com relacionamentos carregados
         */
        @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.product JOIN FETCH oi.order WHERE oi.order.id = :orderId")
        List<OrderItem> findItemsWithRelationshipsByOrder(@Param("orderId") Long orderId);

        // ==================== NATIVE QUERIES ====================

        /**
         * Busca itens de um pedido usando SQL nativo.
         *
         * NATIVE QUERY:
         * - SQL puro do banco de dados (MySQL neste caso)
         * - Usa nomes de TABELAS (order_items) e COLUNAS (order_id)
         * - nativeQuery = true OBRIGATORIO
         *
         * @param orderId ID do pedido
         * @return Lista de itens do pedido
         */
        @Query(value = "SELECT * FROM order_items WHERE order_id = ?1", nativeQuery = true)
        List<OrderItem> findItemsByOrderNative(Long orderId);

        /**
         * Busca produtos mais vendidos usando SQL nativo.
         *
         * Native Query com JOIN, GROUP BY, ORDER BY e LIMIT:
         * - JOIN entre order_items e products
         * - SUM(quantity): Soma quantidade vendida
         * - GROUP BY: Agrupa por produto
         * - ORDER BY DESC: Ordena do mais vendido para o menos
         * - LIMIT: Retorna apenas os top N
         *
         * Retorna Object[] com [product_id, product_name, total_quantity].
         *
         * @param limit Quantidade de produtos a retornar
         * @return Lista de arrays [product_id, product_name, total_quantity]
         */
        @Query(value = "SELECT p.id, p.name, SUM(oi.quantity) as total_quantity " +
                        "FROM order_items oi " +
                        "JOIN products p ON oi.product_id = p.id " +
                        "JOIN orders o ON oi.order_id = o.id " +
                        "WHERE o.status = 'PAID' " +
                        "GROUP BY p.id, p.name " +
                        "ORDER BY total_quantity DESC " +
                        "LIMIT ?1", nativeQuery = true)
        List<Object[]> findTopSellingProducts(int limit);

        /**
         * Calcula receita por produto usando SQL nativo.
         *
         * Native Query com agregacao:
         * - SUM(subtotal): Soma total de receita por produto
         * - JOIN com orders para filtrar por status PAID
         *
         * Retorna Object[] com [product_id, product_name, revenue].
         *
         * @return Lista de arrays [product_id, product_name, revenue]
         */
        @Query(value = "SELECT p.id, p.name, SUM(oi.subtotal) as revenue " +
                        "FROM order_items oi " +
                        "JOIN products p ON oi.product_id = p.id " +
                        "JOIN orders o ON oi.order_id = o.id " +
                        "WHERE o.status = 'PAID' " +
                        "GROUP BY p.id, p.name " +
                        "ORDER BY revenue DESC", nativeQuery = true)
        List<Object[]> calculateRevenueByProduct();

        // ==================== SPECIFICATIONS ====================
        // Os metodos de Specification sao herdados de JpaSpecificationExecutor:
        // - findAll(Specification<OrderItem> spec)
        // - findOne(Specification<OrderItem> spec)
        // - count(Specification<OrderItem> spec)
        // - exists(Specification<OrderItem> spec)
        //
        // Uso:
        // orderItemRepository.findAll(OrderItemSpecifications.byOrder(orderId));
        // orderItemRepository.findAll(OrderItemSpecifications.byProduct(productId));
        //
        // Composicao (AND, OR):
        // orderItemRepository.findAll(
        // Specification.where(OrderItemSpecifications.byOrder(orderId))
        // .and(OrderItemSpecifications.quantityGreaterThan(5))
        // );
        //
        // Veja a classe OrderItemSpecifications para exemplos de implementacao.
}