package br.com.unipds.artantech.ordersystem_jpa.repository;

import br.com.unipds.artantech.ordersystem_jpa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository Spring Data JPA para a entidade User.
 *
 * CONCEITOS SPRING DATA JPA DEMONSTRADOS:
 * - JpaRepository<User, Long>: Interface generica que fornece CRUD completo
 * - JpaSpecificationExecutor<User>: Permite queries dinamicas com Criteria API
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
 * - Exemplo: findByEmail(String email)
 *
 * 2. JPQL - Java Persistence Query Language (Portavel):
 * - Vantagem: Orientado a objetos, independente do banco
 * - Desvantagem: Sintaxe propria para aprender
 * - Exemplo: @Query("SELECT u FROM User u WHERE u.email = :email")
 *
 * 3. NATIVE QUERY (Especifico do banco):
 * - Vantagem: SQL puro, recursos especificos do MySQL
 * - Desvantagem: Nao portavel entre bancos
 * - Exemplo: @Query(value = "SELECT * FROM users WHERE email = ?", nativeQuery
 * = true)
 *
 * 4. SPECIFICATION (Queries dinamicas):
 * - Vantagem: Reusavel, composicao de criterios, type-safe
 * - Desvantagem: Mais verboso, curva de aprendizado
 * - Exemplo: findAll(UserSpecifications.hasEmail(email))
 *
 * @see User
 * @see br.com.unipds.ordersystemjpa.repository.specification.UserSpecifications
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    // ==================== QUERY METHODS ====================

    /**
     * Busca um usuario pelo email.
     *
     * Query Method: Spring Data JPA gera automaticamente:
     * SELECT * FROM users WHERE email = ?
     *
     * @param email Email do usuario
     * @return Optional contendo o usuario se encontrado, ou Optional.empty()
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica se existe um usuario com o email especificado.
     *
     * Query Method: Spring Data JPA gera:
     * SELECT COUNT(*) > 0 FROM users WHERE email = ?
     *
     * Util para validacoes antes de criar usuarios.
     *
     * @param email Email a verificar
     * @return true se existe, false caso contrario
     */
    boolean existsByEmail(String email);

    /**
     * Busca usuarios cujo nome contem o texto especificado (case-insensitive).
     *
     * Query Method com palavras-chave: Containing + IgnoreCase
     *
     * @param name Texto a buscar no nome
     * @return Lista de usuarios encontrados
     */
    List<User> findByNameContainingIgnoreCase(String name);

    // ==================== JPQL QUERIES ====================

    /**
     * Busca usuario por email usando JPQL.
     *
     * JPQL (Java Persistence Query Language):
     * - Usa nomes de ENTIDADES (User) ao inves de TABELAS (users)
     * - Usa nomes de PROPRIEDADES (email) ao inves de COLUNAS
     * - Independente do banco de dados (portavel)
     * - Named parameter :email (mais legivel que ?)
     *
     * EQUIVALENTE Query Method: findByEmail(String email)
     *
     * @param email Email do usuario
     * @return Optional contendo o usuario
     */
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmailUsingJPQL(@Param("email") String email);

    /**
     * Busca usuarios cadastrados apos uma data especifica usando JPQL.
     *
     * JPQL com comparacao de data.
     * Demonstra navegacao de propriedade: u.createdAt
     *
     * @param date Data de referencia
     * @return Lista de usuarios cadastrados apos a data
     */
    @Query("SELECT u FROM User u WHERE u.createdAt > :date")
    List<User> findUsersCreatedAfter(@Param("date") LocalDateTime date);

    /**
     * Busca usuarios com pedidos usando JPQL com JOIN.
     *
     * JPQL com JOIN FETCH:
     * - Carrega User E seus Orders em uma unica query
     * - Evita problema N+1 (multiplas queries para relacionamentos)
     * - FETCH torna o join EAGER para esta query especifica
     *
     * @return Lista de usuarios que tem pelo menos um pedido
     */
    @Query("SELECT DISTINCT u FROM User u JOIN FETCH u.orders")
    List<User> findUsersWithOrders();

    // ==================== NATIVE QUERIES ====================

    /**
     * Busca usuario por email usando SQL nativo (Native Query).
     *
     * NATIVE QUERY:
     * - SQL puro do banco de dados (MySQL neste caso)
     * - Usa nomes de TABELAS (users) e COLUNAS (email)
     * - Permite recursos especificos do MySQL
     * - nativeQuery = true OBRIGATORIO
     * - Parametros podem ser :param ou ?1, ?2, etc
     *
     * QUANDO USAR:
     * - Queries muito complexas dificeis em JPQL
     * - Recursos especificos do banco (ex: MySQL REGEXP, window functions)
     * - Otimizacoes especificas do banco
     *
     * DESVANTAGEM:
     * - Nao portavel (se mudar de MySQL para PostgreSQL, precisa ajustar)
     *
     * @param email Email do usuario
     * @return Optional contendo o usuario
     */
    @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
    Optional<User> findByEmailUsingNative(@Param("email") String email);

    /**
     * Conta usuarios cadastrados em um determinado mes usando SQL nativo.
     *
     * Native Query com funcoes especificas do MySQL:
     * - MONTH() e YEAR() sao funcoes do MySQL
     * - Esta query NAO funcionaria em outros bancos sem adaptacao
     *
     * Exemplo de recurso especifico do banco que justifica Native Query.
     *
     * @param month Mes (1-12)
     * @param year  Ano
     * @return Quantidade de usuarios cadastrados no mes/ano
     */
    @Query(value = "SELECT COUNT(*) FROM users WHERE MONTH(created_at) = ?1 AND YEAR(created_at) = ?2", nativeQuery = true)
    long countUsersByMonthAndYear(int month, int year);

    /**
     * Busca usuarios usando busca Full-Text do MySQL.
     *
     * Native Query com MATCH AGAINST (Full-Text Search):
     * - Recurso ESPECIFICO do MySQL para busca textual avancada
     * - Muito mais eficiente que LIKE '%texto%' para textos grandes
     * - Requer index FULLTEXT na coluna name
     *
     * IMPORTANTE: Nao funciona em JPQL, somente Native Query!
     *
     * Para habilitar, execute no banco:
     * ALTER TABLE users ADD FULLTEXT INDEX ft_name (name);
     *
     * @param searchTerm Termo de busca
     * @return Lista de usuarios encontrados ordenados por relevancia
     */
    @Query(value = "SELECT * FROM users WHERE MATCH(name) AGAINST(?1 IN NATURAL LANGUAGE MODE)", nativeQuery = true)
    List<User> searchUsersByNameFullText(String searchTerm);

    // ==================== SPECIFICATIONS ====================
    // Os metodos de Specification sao herdados de JpaSpecificationExecutor:
    // - findAll(Specification<User> spec)
    // - findOne(Specification<User> spec)
    // - count(Specification<User> spec)
    // - exists(Specification<User> spec)
    //
    // Uso:
    // userRepository.findAll(UserSpecifications.hasEmail("test@example.com"));
    // userRepository.findAll(UserSpecifications.createdAfter(LocalDateTime.now().minusDays(7)));
    //
    // Composicao (AND, OR):
    // userRepository.findAll(
    // Specification.where(UserSpecifications.hasEmail("test@example.com"))
    // .and(UserSpecifications.createdAfter(date))
    // );
    //
    // Veja a classe UserSpecifications para exemplos de implementacao.
}