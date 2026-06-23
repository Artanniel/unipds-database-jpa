package br.com.unipds.artantech.ordersystem_jpa.repository.specification;

import br.com.unipds.artantech.ordersystem_jpa.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

/**
 * Specifications (Criteria API) para a entidade Product.
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
 * @Query("SELECT p FROM Product p WHERE p.price > :price")
 * ```
 * - Problema: Erro de digitacao so e detectado em runtime
 *
 * Specification (Type-safe):
 * ```
 * root.get("price").greaterThan(price)
 * ```
 * - Vantagem: Erro de digitacao e detectado em compile time
 *
 * COMPOSICAO DE SPECIFICATIONS:
 * ```
 * Specification<Product> spec = Specification
 * .where(nameContains("laptop"))
 * .and(priceGreaterThan(BigDecimal.valueOf(1000)))
 * .and(hasStock());
 *
 * List<Product> products = productRepository.findAll(spec);
 * ```
 *
 * ESTRUTURA BASICA:
 * ```
 * (root, query, criteriaBuilder) -> {
 * // root: Representa a entidade Product (FROM Product)
 * // query: Permite configurar SELECT, ORDER BY, etc
 * // criteriaBuilder: Constroi predicados (WHERE)
 * return criteriaBuilder.greaterThan(root.get("price"), price);
 * }
 * ```
 *
 * @see Product
 * @see br.com.unipds.ordersystemjpa.repository.ProductRepository
 * @see org.springframework.data.jpa.domain.Specification
 */
public class ProductSpecifications {

    /**
     * Specification: Produto cujo nome contem o texto especificado
     * (case-insensitive).
     *
     * Equivalente JPQL: WHERE LOWER(p.name) LIKE LOWER('%:name%')
     *
     * ESTRUTURA:
     * - root.get("name"): Acessa a propriedade name de Product
     * - criteriaBuilder.lower(): Converte para minuscula
     * - criteriaBuilder.like(): Cria predicado LIKE
     * - "%" + name + "%": Busca em qualquer posicao
     *
     * USO:
     * ```
     * productRepository.findAll(ProductSpecifications.nameContains("laptop"));
     * ```
     *
     * @param name Texto a buscar no nome
     * @return Specification que filtra por nome
     */
    public static Specification<Product> nameContains(String name) {
        return (root, query, criteriaBuilder) -> name == null ? null
                : criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%");
    }

    /**
     * Specification: Produtos com preco maior que o valor especificado.
     *
     * Equivalente JPQL: WHERE p.price > :price
     *
     * ESTRUTURA:
     * - root.get("price"): Acessa propriedade price
     * - criteriaBuilder.greaterThan(): Cria predicado > (maior que)
     *
     * USO:
     * ```
     * BigDecimal minPrice = BigDecimal.valueOf(1000);
     * productRepository.findAll(ProductSpecifications.priceGreaterThan(minPrice));
     * ```
     *
     * @param price Preco minimo
     * @return Specification que filtra por preco
     */
    public static Specification<Product> priceGreaterThan(BigDecimal price) {
        return (root, query, criteriaBuilder) -> price == null ? null
                : criteriaBuilder.greaterThan(root.get("price"), price);
    }

    /**
     * Specification: Produtos com preco menor que o valor especificado.
     *
     * Equivalente JPQL: WHERE p.price < :price
     *
     * @param price Preco maximo
     * @return Specification que filtra por preco
     */
    public static Specification<Product> priceLessThan(BigDecimal price) {
        return (root, query, criteriaBuilder) -> price == null ? null
                : criteriaBuilder.lessThan(root.get("price"), price);
    }

    /**
     * Specification: Produtos em uma faixa de preco.
     *
     * Equivalente JPQL: WHERE p.price BETWEEN :minPrice AND :maxPrice
     *
     * COMPOSICAO:
     * Este metodo COMBINA dois Specifications usando criteriaBuilder.between().
     *
     * USO:
     * ```
     * BigDecimal min = BigDecimal.valueOf(500);
     * BigDecimal max = BigDecimal.valueOf(2000);
     * productRepository.findAll(ProductSpecifications.priceBetween(min, max));
     * ```
     *
     * @param minPrice Preco minimo
     * @param maxPrice Preco maximo
     * @return Specification que filtra por faixa de preco
     */
    public static Specification<Product> priceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null && maxPrice == null)
                return null;
            if (minPrice == null)
                return criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
            if (maxPrice == null)
                return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
            return criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
        };
    }

    /**
     * Specification: Produtos com estoque disponivel (stock > 0).
     *
     * Equivalente JPQL: WHERE p.stock > 0
     *
     * ESTRUTURA:
     * - root.get("stock"): Acessa propriedade stock
     * - criteriaBuilder.greaterThan(): Cria predicado > (maior que)
     *
     * USO:
     * ```
     * productRepository.findAll(ProductSpecifications.hasStock());
     * ```
     *
     * @return Specification que filtra produtos disponiveis
     */
    public static Specification<Product> hasStock() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("stock"), 0);
    }

    /**
     * Specification: Produtos com estoque abaixo ou igual ao limite.
     *
     * Equivalente JPQL: WHERE p.stock <= :threshold
     *
     * USO:
     * ```
     * productRepository.findAll(ProductSpecifications.lowStock(10));
     * ```
     *
     * @param threshold Limite de estoque
     * @return Specification que filtra produtos com estoque baixo
     */
    public static Specification<Product> lowStock(Integer threshold) {
        return (root, query, criteriaBuilder) -> threshold == null ? null
                : criteriaBuilder.lessThanOrEqualTo(root.get("stock"), threshold);
    }

    /**
     * Specification: Produtos sem estoque (stock = 0).
     *
     * Equivalente JPQL: WHERE p.stock = 0
     *
     * @return Specification que filtra produtos sem estoque
     */
    public static Specification<Product> outOfStock() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("stock"), 0);
    }

    // ==================== EXEMPLO DE COMPOSICAO ====================

    /**
     * EXEMPLO EDUCACIONAL: Composicao de multiplos criterios.
     *
     * Este metodo NAO precisa existir aqui, e apenas um exemplo de como
     * COMPOR Specifications no codigo que usa o repository.
     *
     * Busca produtos:
     * - Cujo nome contem o texto especificado
     * - Com preco entre min e max
     * - Com estoque disponivel
     *
     * USO (no Service ou Controller):
     * ```
     * Specification<Product> spec = Specification
     * .where(ProductSpecifications.nameContains("laptop"))
     * .and(ProductSpecifications.priceBetween(min, max))
     * .and(ProductSpecifications.hasStock());
     *
     * List<Product> products = productRepository.findAll(spec);
     * ```
     *
     * OUTRO EXEMPLO - Filtros opcionais:
     * ```
     * Specification<Product> spec = Specification.where(null);
     *
     * if (name != null) {
     * spec = spec.and(ProductSpecifications.nameContains(name));
     * }
     *
     * if (minPrice != null || maxPrice != null) {
     * spec = spec.and(ProductSpecifications.priceBetween(minPrice, maxPrice));
     * }
     *
     * if (onlyAvailable) {
     * spec = spec.and(ProductSpecifications.hasStock());
     * }
     *
     * List<Product> products = productRepository.findAll(spec);
     * ```
     *
     * VANTAGENS DA COMPOSICAO:
     * - Criterios sao opcionais (if name != null)
     * - Reutilizacao de logica (nameContains, priceBetween)
     * - Type-safe (erros em compile time)
     * - Testavel (pode testar cada Specification separadamente)
     */
}