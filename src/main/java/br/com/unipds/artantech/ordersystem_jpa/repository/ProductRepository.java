package br.com.unipds.artantech.ordersystem_jpa.repository;

import br.com.unipds.artantech.ordersystem_jpa.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository Spring Data JPA para a entidade Product.
 *
 * CONCEITOS SPRING DATA JPA DEMONSTRADOS:
 * - JpaRepository<Product, Long>: Interface generica que fornece CRUD completo
 * - JpaSpecificationExecutor<Product>: Permite queries dinamicas com Criteria
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
 * - Exemplo: findByNameContainingIgnoreCase(String name)
 *
 * 2. JPQL - Java Persistence Query Language (Portavel):
 * - Vantagem: Orientado a objetos, independente do banco
 * - Desvantagem: Sintaxe propria para aprender
 * - Exemplo: @Query("SELECT p FROM Product p WHERE p.stock > 0")
 *
 * 3. NATIVE QUERY (Especifico do banco):
 * - Vantagem: SQL puro, recursos especificos do MySQL
 * - Desvantagem: Nao portavel entre bancos
 * - Exemplo: @Query(value = "SELECT * FROM products WHERE stock > 0",
 * nativeQuery = true)
 *
 * 4. SPECIFICATION (Queries dinamicas):
 * - Vantagem: Reusavel, composicao de criterios, type-safe
 * - Desvantagem: Mais verboso, curva de aprendizado
 * - Exemplo: findAll(ProductSpecifications.priceGreaterThan(value))
 *
 * @see Product
 * @see br.com.unipds.ordersystemjpa.repository.specification.ProductSpecifications
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    // ==================== QUERY METHODS ====================

    /**
     * Busca produtos cujo nome contem o texto especificado (case-insensitive).
     *
     * Query Method: Spring gera automaticamente:
     * SELECT * FROM products WHERE LOWER(name) LIKE LOWER('%?%')
     *
     * Exemplo: findByNameContainingIgnoreCase("lap") retorna "Laptop", "LAPTOP
     * PRO", etc.
     *
     * @param name Texto a buscar no nome
     * @return Lista de produtos encontrados
     */
    List<Product> findByNameContainingIgnoreCase(String name);

    /**
     * Busca produtos com estoque abaixo ou igual ao limite especificado.
     *
     * Query Method com comparacao numerica.
     * Spring gera: SELECT * FROM products WHERE stock <= ?
     *
     * @param threshold Limite de estoque
     * @return Lista de produtos com estoque baixo
     */
    List<Product> findByStockLessThanEqual(Integer threshold);

    /**
     * Busca produtos em uma faixa de preco.
     *
     * Query Method com Between.
     * Spring gera: SELECT * FROM products WHERE price BETWEEN ? AND ?
     *
     * @param minPrice Preco minimo
     * @param maxPrice Preco maximo
     * @return Lista de produtos na faixa de preco
     */
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // ==================== JPQL QUERIES ====================

    /**
     * Busca produtos com estoque disponivel usando JPQL.
     *
     * JPQL (Java Persistence Query Language):
     * - Usa nomes de ENTIDADES (Product) ao inves de TABELAS (products)
     * - Usa nomes de PROPRIEDADES (stock) ao inves de COLUNAS
     * - Independente do banco de dados (portavel)
     *
     * EQUIVALENTE Query Method: findByStockGreaterThan(0)
     *
     * @return Lista de produtos disponiveis
     */
    @Query("SELECT p FROM Product p WHERE p.stock > 0")
    List<Product> findAvailableProducts();

    /**
     * Busca produtos com preco maior que o valor especificado usando JPQL.
     *
     * JPQL com Named Parameter :price.
     * 
     * @Param vincula o parametro do metodo ao named parameter da query.
     *
     * @param price Preco minimo
     * @return Lista de produtos com preco maior
     */
    @Query("SELECT p FROM Product p WHERE p.price > :price")
    List<Product> findProductsMoreExpensiveThan(@Param("price") BigDecimal price);

    /**
     * Busca produtos ordenados por preco decrescente usando JPQL.
     *
     * JPQL com ORDER BY.
     * Demonstra como ordenar resultados.
     *
     * @return Lista de produtos ordenados por preco (maior para menor)
     */
    @Query("SELECT p FROM Product p ORDER BY p.price DESC")
    List<Product> findAllOrderedByPriceDesc();

    /**
     * Conta produtos disponiveis em estoque usando JPQL.
     *
     * JPQL com agregacao COUNT.
     * Retorna um Long ao inves de uma List.
     *
     * @return Quantidade de produtos disponiveis
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.stock > 0")
    long countAvailableProducts();

    // ==================== NATIVE QUERIES ====================

    /**
     * Busca produtos com estoque baixo usando SQL nativo.
     *
     * NATIVE QUERY:
     * - SQL puro do banco de dados (MySQL neste caso)
     * - Usa nomes de TABELAS (products) e COLUNAS (stock)
     * - nativeQuery = true OBRIGATORIO
     * - Parametros podem ser :param ou ?1, ?2, etc
     *
     * QUANDO USAR:
     * - Queries muito complexas dificeis em JPQL
     * - Recursos especificos do MySQL
     * - Otimizacoes especificas do banco
     *
     * @param threshold Limite de estoque
     * @return Lista de produtos com estoque baixo
     */
    @Query(value = "SELECT * FROM products WHERE stock <= :threshold ORDER BY stock ASC", nativeQuery = true)
    List<Product> findLowStockProductsNative(@Param("threshold") Integer threshold);

    /**
     * Calcula o valor total do inventario usando SQL nativo.
     *
     * Native Query com agregacao MySQL:
     * - SUM(price * stock): Multiplica preco por quantidade em estoque
     * - IFNULL(): Funcao especifica do MySQL para tratar NULL
     *
     * Retorna o valor total de todos os produtos em estoque.
     *
     * @return Valor total do inventario
     */
    @Query(value = "SELECT IFNULL(SUM(price * stock), 0) FROM products", nativeQuery = true)
    BigDecimal calculateTotalInventoryValue();

    /**
     * Busca produtos por faixa de preco com estatisticas usando SQL nativo.
     *
     * Native Query com funcoes de agregacao MySQL:
     * - AVG(): Media de preco
     * - MIN(): Preco minimo
     * - MAX(): Preco maximo
     * - COUNT(): Quantidade de produtos
     *
     * IMPORTANTE: Retorna Object[] porque consulta retorna multiplas colunas.
     * Object[0] = AVG(price), Object[1] = MIN(price), etc.
     *
     * @param minPrice Preco minimo
     * @param maxPrice Preco maximo
     * @return Array com [avg, min, max, count]
     */
    @Query(value = "SELECT AVG(price), MIN(price), MAX(price), COUNT(*) " +
            "FROM products WHERE price BETWEEN ?1 AND ?2", nativeQuery = true)
    Object[] getPriceStatistics(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Busca produtos usando busca Full-Text do MySQL.
     *
     * Native Query com MATCH AGAINST (Full-Text Search):
     * - Recurso ESPECIFICO do MySQL para busca textual avancada
     * - Muito mais eficiente que LIKE '%texto%' para textos grandes
     * - Requer index FULLTEXT na coluna name
     *
     * IMPORTANTE: Nao funciona em JPQL, somente Native Query!
     *
     * Para habilitar, execute no banco:
     * ALTER TABLE products ADD FULLTEXT INDEX ft_name (name);
     *
     * @param searchTerm Termo de busca
     * @return Lista de produtos encontrados ordenados por relevancia
     */
    @Query(value = "SELECT * FROM products WHERE MATCH(name) AGAINST(?1 IN NATURAL LANGUAGE MODE)", nativeQuery = true)
    List<Product> searchProductsByNameFullText(String searchTerm);

    // ==================== SPECIFICATIONS ====================
    // Os metodos de Specification sao herdados de JpaSpecificationExecutor:
    // - findAll(Specification<Product> spec)
    // - findOne(Specification<Product> spec)
    // - count(Specification<Product> spec)
    // - exists(Specification<Product> spec)
    //
    // Uso:
    // productRepository.findAll(ProductSpecifications.nameContains("laptop"));
    // productRepository.findAll(ProductSpecifications.priceGreaterThan(BigDecimal.valueOf(100)));
    //
    // Composicao (AND, OR):
    // productRepository.findAll(
    // Specification.where(ProductSpecifications.nameContains("laptop"))
    // .and(ProductSpecifications.priceGreaterThan(BigDecimal.valueOf(100)))
    // );
    //
    // Veja a classe ProductSpecifications para exemplos de implementacao.
}