package br.com.unipds.artantech.ordersystem_jpa.repository.specification;

import br.com.unipds.artantech.ordersystem_jpa.entity.ProductReview;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications (Criteria API) para a entidade ProductReview.
 *
 * IMPORTANTE - CHAVE COMPOSTA:
 * ProductReview usa @EmbeddedId (ProductReviewId), entao devemos navegar
 * pelos campos da chave composta: id.userId e id.productId.
 *
 * @see ProductReview
 * @see br.com.unipds.ordersystemjpa.entity.ProductReviewId
 * @see br.com.unipds.ordersystemjpa.repository.ProductReviewRepository
 */
public class ProductReviewSpecifications {

    /**
     * Specification: Avaliacoes de um usuario especifico.
     *
     * Equivalente JPQL: WHERE pr.id.userId = :userId
     *
     * CHAVE COMPOSTA:
     * - root.get("id"): Acessa ProductReviewId (chave composta)
     * - .get("userId"): Acessa o campo userId dentro da chave
     */
    public static Specification<ProductReview> byUser(Long userId) {
        return (root, query, criteriaBuilder) -> userId == null ? null
                : criteriaBuilder.equal(root.get("id").get("userId"), userId);
    }

    /**
     * Specification: Avaliacoes de um produto especifico.
     *
     * Equivalente JPQL: WHERE pr.id.productId = :productId
     *
     * CHAVE COMPOSTA:
     * - root.get("id"): Acessa ProductReviewId (chave composta)
     * - .get("productId"): Acessa o campo productId dentro da chave
     */
    public static Specification<ProductReview> byProduct(Long productId) {
        return (root, query, criteriaBuilder) -> productId == null ? null
                : criteriaBuilder.equal(root.get("id").get("productId"), productId);
    }

    /**
     * Specification: Avaliacoes com nota maior ou igual ao valor especificado.
     *
     * Equivalente JPQL: WHERE pr.rating >= :rating
     */
    public static Specification<ProductReview> ratingGreaterThanEqual(Integer rating) {
        return (root, query, criteriaBuilder) -> rating == null ? null
                : criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), rating);
    }

    /**
     * Specification: Avaliacoes com comentario nao vazio.
     *
     * Equivalente JPQL: WHERE pr.comment IS NOT NULL AND pr.comment != ''
     */
    public static Specification<ProductReview> hasComment() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.isNotNull(root.get("comment")),
                criteriaBuilder.notEqual(root.get("comment"), ""));
    }

    /**
     * Specification: Avaliacoes com comentario contendo texto especificado.
     *
     * Equivalente JPQL: WHERE LOWER(pr.comment) LIKE LOWER('%:text%')
     */
    public static Specification<ProductReview> commentContains(String text) {
        return (root, query, criteriaBuilder) -> text == null ? null
                : criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("comment")),
                        "%" + text.toLowerCase() + "%");
    }
}