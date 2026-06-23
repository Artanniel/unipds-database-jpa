package br.com.unipds.artantech.ordersystem_jpa.repository.specification;

import br.com.unipds.artantech.ordersystem_jpa.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

/**
 * Specifications (Criteria API) para a entidade User.
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
 * COMPARACAO:
 *
 * JPQL (String):
 * ```
 * @Query("SELECT u FROM User u WHERE u.email = :email")
 * ```
 * - Problema: Erro de digitacao so e detectado em runtime
 *
 * Specification (Type-safe):
 * ```
 * root.get("email").equals(email)
 * ```
 * - Vantagem: Erro de digitacao e detectado em compile time
 *
 * COMPOSICAO DE SPECIFICATIONS:
 * ```
 * Specification<User> spec = Specification
 * .where(hasEmail("test@example.com"))
 * .and(createdAfter(date))
 * .or(nameContains("John"));
 *
 * List<User> users = userRepository.findAll(spec);
 * ```
 *
 * ESTRUTURA BASICA:
 * ```
 * (root, query, criteriaBuilder) -> {
 * // root: Representa a entidade User (FROM User)
 * // query: Permite configurar SELECT, ORDER BY, etc
 * // criteriaBuilder: Constroi predicados (WHERE)
 * return criteriaBuilder.equal(root.get("email"), email);
 * }
 * ```
 *
 * @see User
 * @see br.com.unipds.ordersystemjpa.repository.UserRepository
 * @see org.springframework.data.jpa.domain.Specification
 */
public class UserSpecifications {

    /**
     * Specification: Usuario com email especifico.
     *
     * Equivalente JPQL: WHERE u.email = :email
     *
     * ESTRUTURA:
     * - root.get("email"): Acessa a propriedade email de User
     * - criteriaBuilder.equal(): Cria predicado de igualdade (=)
     *
     * USO:
     * ```
     * userRepository.findAll(UserSpecifications.hasEmail("test@example.com"));
     * ```
     *
     * @param email Email do usuario
     * @return Specification que filtra por email
     */
    public static Specification<User> hasEmail(String email) {
        return (root, query, criteriaBuilder) -> email == null ? null : criteriaBuilder.equal(root.get("email"), email);
    }

    /**
     * Specification: Usuario cujo nome contem o texto especificado
     * (case-insensitive).
     *
     * Equivalente JPQL: WHERE LOWER(u.name) LIKE LOWER('%:name%')
     *
     * ESTRUTURA:
     * - root.get("name"): Acessa propriedade name
     * - criteriaBuilder.lower(): Converte para minuscula
     * - criteriaBuilder.like(): Cria predicado LIKE
     * - "%" + name + "%": Busca em qualquer posicao
     *
     * USO:
     * ```
     * userRepository.findAll(UserSpecifications.nameContains("John"));
     * ```
     *
     * @param name Texto a buscar no nome
     * @return Specification que filtra por nome
     */
    public static Specification<User> nameContains(String name) {
        return (root, query, criteriaBuilder) -> name == null ? null
                : criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%");
    }

    /**
     * Specification: Usuarios criados apos uma data especifica.
     *
     * Equivalente JPQL: WHERE u.createdAt > :date
     *
     * ESTRUTURA:
     * - root.get("createdAt"): Acessa propriedade createdAt
     * - criteriaBuilder.greaterThan(): Cria predicado > (maior que)
     *
     * USO:
     * ```
     * LocalDateTime lastWeek = LocalDateTime.now().minusDays(7);
     * userRepository.findAll(UserSpecifications.createdAfter(lastWeek));
     * ```
     *
     * @param date Data de referencia
     * @return Specification que filtra por data de criacao
     */
    public static Specification<User> createdAfter(LocalDateTime date) {
        return (root, query, criteriaBuilder) -> date == null ? null
                : criteriaBuilder.greaterThan(root.get("createdAt"), date);
    }

    /**
     * Specification: Usuarios criados antes de uma data especifica.
     *
     * Equivalente JPQL: WHERE u.createdAt < :date
     *
     * @param date Data de referencia
     * @return Specification que filtra por data de criacao
     */
    public static Specification<User> createdBefore(LocalDateTime date) {
        return (root, query, criteriaBuilder) -> date == null ? null
                : criteriaBuilder.lessThan(root.get("createdAt"), date);
    }

    /**
     * Specification: Usuarios criados em um intervalo de datas.
     *
     * Equivalente JPQL: WHERE u.createdAt BETWEEN :startDate AND :endDate
     *
     * COMPOSICAO:
     * Este metodo COMBINA dois Specifications usando AND:
     * - createdAfter(startDate) AND createdBefore(endDate)
     *
     * USO:
     * ```
     * LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0);
     * LocalDateTime end = LocalDateTime.of(2025, 12, 31, 23, 59);
     * userRepository.findAll(UserSpecifications.createdBetween(start, end));
     * ```
     *
     * @param startDate Data inicial
     * @param endDate   Data final
     * @return Specification que filtra por intervalo de datas
     */
    public static Specification<User> createdBetween(LocalDateTime startDate, LocalDateTime endDate) {
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
     * Specification: Usuarios que tem pelo menos um pedido.
     *
     * Equivalente JPQL: WHERE SIZE(u.orders) > 0
     *
     * ESTRUTURA - JOIN:
     * - root.join("orders"): Cria JOIN com a colecao orders
     * - query.distinct(true): Remove duplicatas (usuario aparece N vezes se tem N
     * pedidos)
     * - isNotNull(): Verifica se o join nao e vazio
     *
     * IMPORTANTE: Este e um exemplo de Specification com JOIN,
     * mais complexo que as anteriores.
     *
     * USO:
     * ```
     * userRepository.findAll(UserSpecifications.hasOrders());
     * ```
     *
     * @return Specification que filtra usuarios com pedidos
     */
    public static Specification<User> hasOrders() {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            return criteriaBuilder.isNotNull(root.join("orders"));
        };
    }

    // ==================== EXEMPLO DE COMPOSICAO ====================

    /**
     * EXEMPLO EDUCACIONAL: Composicao de multiplos criterios.
     *
     * Este metodo NAO precisa existir aqui, e apenas um exemplo de como
     * COMPOR Specifications no codigo que usa o repository.
     *
     * Busca usuarios:
     * - Cujo nome contem o texto especificado
     * - E foram criados apos uma data
     * - E tem email especifico (opcional)
     *
     * USO (no Service ou Controller):
     * ```
     * Specification<User> spec = Specification
     * .where(UserSpecifications.nameContains("John"))
     * .and(UserSpecifications.createdAfter(lastWeek));
     *
     * if (email != null) {
     * spec = spec.and(UserSpecifications.hasEmail(email));
     * }
     *
     * List<User> users = userRepository.findAll(spec);
     * ```
     *
     * VANTAGENS DA COMPOSICAO:
     * - Criterios sao opcionais (if email != null)
     * - Reutilizacao de logica (nameContains, createdAfter)
     * - Type-safe (erros em compile time)
     * - Testavel (pode testar cada Specification separadamente)
     */
}