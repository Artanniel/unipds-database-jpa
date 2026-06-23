package br.com.unipds.artantech.ordersystem_jpa.repository;

import br.com.unipds.artantech.ordersystem_jpa.entity.Order;
import br.com.unipds.artantech.ordersystem_jpa.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository Spring Data JPA para a entidade Order.
 *
 * CONCEITOS SPRING DATA JPA DEMONSTRADOS:
 * - JpaRepository<Order, Long>: Interface generica que fornece CRUD completo
 * - JpaSpecificationExecutor<Order>: Permite queries dinamicas com Criteria API
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
 * - Exemplo: findByUserId(Long userId)
 *
 * 2. JPQL - Java Persistence Query Language (Portavel):
 * - Vantagem: Orientado a objetos, independente do banco
 * - Desvantagem: Sintaxe propria para aprender
 * - Exemplo: @Query("SELECT o FROM Order o WHERE o.user.id = :userId")
 *
 * 3. NATIVE QUERY (Especifico do banco):
 * - Vantagem: SQL puro, recursos especificos do MySQL
 * - Desvantagem: Nao portavel entre bancos
 * - Exemplo: @Query(value = "SELECT * FROM orders WHERE user_id = ?",
 * nativeQuery = true)
 *
 * 4. SPECIFICATION (Queries dinamicas):
 * - Vantagem: Reusavel, composicao de criterios, type-safe
 * - Desvantagem: Mais verboso, curva de aprendizado
 * - Exemplo: findAll(OrderSpecifications.byUser(userId))
 *
 * @see Order
 * @see br.com.unipds.ordersystemjpa.repository.specification.OrderSpecifications
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    // ==================== QUERY METHODS ====================

    /**
     * Busca todos os pedidos de um usuario especifico.
     *
     * Query Method com navegacao: Spring acessa o relacionamento @ManyToOne
     * e gera: SELECT * FROM orders WHERE user_id = ?
     *
     * @param userId ID do usuario
     * @return Lista de pedidos do usuario
     */
    List<Order> findByUserId(Long userId);

    /**
     * Busca pedidos por status.
     *
     * Query Method com enum: Spring usa o valor String do enum.
     *
     * Exemplo: findByStatus(OrderStatus.PAID) busca pedidos pagos.
     *
     * @param status Status do pedido
     * @return Lista de pedidos com o status especificado
     */
    List<Order> findByStatus(OrderStatus status);

    /**
     * Busca pedidos de um usuario com um status especifico.
     *
     * Query Method composto: navegacao + enum.
     * Spring gera: SELECT * FROM orders WHERE user_id = ? AND status = ?
     *
     * @param userId ID do usuario
     * @param status Status do pedido
     * @return Lista de pedidos que atendem ambos os criterios
     */
    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);

    /**
     * Busca pedidos com total maior que o valor especificado.
     *
     * Query Method com comparacao numerica.
     * Spring gera: SELECT * FROM orders WHERE total > ?
     *
     * @param total Valor minimo do pedido
     * @return Lista de pedidos com total maior
     */
    List<Order> findByTotalGreaterThan(BigDecimal total);

    // ==================== JPQL QUERIES ====================

    /**
     * Busca pedidos de um usuario com status especifico usando JPQL.
     *
     * JPQL (Java Persistence Query Language):
     * - Usa nomes de ENTIDADES (Order) ao inves de TABELAS (orders)
     * - Navegacao de propriedade: o.user.id acessa o ID do User relacionado
     * - Independente do banco de dados (portavel)
     *
     * EQUIVALENTE Query Method: findByUserIdAndStatus(Long userId, OrderStatus
     * status)
     *
     * @param userId ID do usuario
     * @param status Status do pedido
     * @return Lista de pedidos que atendem ambos os criterios
     */
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.status = :status")
    List<Order> findByUserIdAndStatusUsingJPQL(@Param("userId") Long userId, @Param("status") OrderStatus status);

    /**
     * Busca pedidos criados dentro de um intervalo de datas usando JPQL.
     *
     * JPQL com BETWEEN: Busca registros entre startDate e endDate (inclusive).
     * Util para relatorios e dashboards.
     *
     * @param startDate Data inicial
     * @param endDate   Data final
     * @return Lista de pedidos no intervalo
     */
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findOrdersByDateRange(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Busca pedidos com seus itens carregados (JOIN FETCH).
     *
     * JPQL com JOIN FETCH:
     * - Carrega Order E seus OrderItems em uma unica query
     * - Evita problema N+1 (multiplas queries para relacionamentos)
     * - FETCH torna o join EAGER para esta query especifica
     * - DISTINCT remove duplicatas (Order aparece N vezes se tem N itens)
     *
     * IMPORTANTE: Use JOIN FETCH apenas quando for usar os itens do pedido.
     * Nao use em queries de listagem simples (desperdicio de memoria).
     *
     * @param userId ID do usuario
     * @return Lista de pedidos com itens carregados
     */
    @Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.items WHERE o.user.id = :userId")
    List<Order> findOrdersWithItemsByUserId(@Param("userId") Long userId);

    /**
     * Calcula o total de vendas de um usuario usando JPQL.
     *
     * JPQL com agregacao SUM.
     * Retorna BigDecimal ao inves de uma List.
     *
     * @param userId ID do usuario
     * @return Total de vendas do usuario
     */
    @Query("SELECT SUM(o.total) FROM Order o WHERE o.user.id = :userId AND o.status = 'PAID'")
    BigDecimal calculateTotalSalesByUser(@Param("userId") Long userId);

    // ==================== NATIVE QUERIES ====================

    /**
     * Busca pedidos de um mes/ano especifico usando SQL nativo.
     *
     * NATIVE QUERY:
     * - SQL puro do banco de dados (MySQL neste caso)
     * - Usa nomes de TABELAS (orders) e COLUNAS (user_id, created_at)
     * - MONTH() e YEAR() sao funcoes do MySQL
     * - nativeQuery = true OBRIGATORIO
     *
     * QUANDO USAR:
     * - Funcoes especificas do MySQL (MONTH, YEAR, etc)
     * - Queries muito complexas dificeis em JPQL
     * - Otimizacoes especificas do banco
     *
     * @param month Mes (1-12)
     * @param year  Ano
     * @return Lista de pedidos do mes/ano
     */
    @Query(value = "SELECT * FROM orders WHERE MONTH(created_at) = ?1 AND YEAR(created_at) = ?2", nativeQuery = true)
    List<Order> findOrdersByMonthAndYear(int month, int year);

    /**
     * Conta pedidos por status usando SQL nativo.
     *
     * Native Query com COUNT e GROUP BY.
     * Retorna Object[] com [status, count].
     *
     * IMPORTANTE: Retorna List<Object[]> onde cada elemento e:
     * - Object[0] = status (String)
     * - Object[1] = count (Long)
     *
     * @return Lista de arrays [status, count]
     */
    @Query(value = "SELECT status, COUNT(*) as count FROM orders GROUP BY status", nativeQuery = true)
    List<Object[]> countOrdersByStatus();

    /**
     * Calcula estatisticas de vendas por periodo usando SQL nativo.
     *
     * Native Query com multiplas funcoes de agregacao:
     * - COUNT(*): Total de pedidos
     * - SUM(total): Soma total de vendas
     * - AVG(total): Ticket medio
     * - MIN(total): Menor pedido
     * - MAX(total): Maior pedido
     *
     * IMPORTANTE: Retorna Object[] com [count, sum, avg, min, max].
     *
     * @param startDate Data inicial
     * @param endDate   Data final
     * @return Array com estatisticas [count, sum, avg, min, max]
     */
    @Query(value = "SELECT COUNT(*), SUM(total), AVG(total), MIN(total), MAX(total) " +
            "FROM orders WHERE created_at BETWEEN ?1 AND ?2 AND status = 'PAID'", nativeQuery = true)
    Object[] getSalesStatistics(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Busca top usuarios por valor total de pedidos usando SQL nativo.
     *
     * Native Query com JOIN, GROUP BY, ORDER BY e LIMIT:
     * - JOIN entre orders e users
     * - GROUP BY para agrupar por usuario
     * - ORDER BY DESC para ordenar do maior para o menor
     * - LIMIT para retornar apenas os top N
     *
     * Retorna Object[] com [user_id, user_name, total].
     *
     * @param limit Quantidade de usuarios a retornar
     * @return Lista de arrays [user_id, user_name, total_vendas]
     */
    @Query(value = "SELECT u.id, u.name, SUM(o.total) as total_vendas " +
            "FROM orders o " +
            "JOIN users u ON o.user_id = u.id " +
            "WHERE o.status = 'PAID' " +
            "GROUP BY u.id, u.name " +
            "ORDER BY total_vendas DESC " +
            "LIMIT ?1", nativeQuery = true)
    List<Object[]> findTopUsersBySales(int limit);

    // ==================== SPECIFICATIONS ====================
    // Os metodos de Specification sao herdados de JpaSpecificationExecutor:
    // - findAll(Specification<Order> spec)
    // - findOne(Specification<Order> spec)
    // - count(Specification<Order> spec)
    // - exists(Specification<Order> spec)
    //
    // Uso:
    // orderRepository.findAll(OrderSpecifications.byUser(userId));
    // orderRepository.findAll(OrderSpecifications.byStatus(OrderStatus.PAID));
    //
    // Composicao (AND, OR):
    // orderRepository.findAll(
    // Specification.where(OrderSpecifications.byUser(userId))
    // .and(OrderSpecifications.byStatus(OrderStatus.PAID))
    // .and(OrderSpecifications.createdAfter(date))
    // );
    //
    // Veja a classe OrderSpecifications para exemplos de implementacao.
}