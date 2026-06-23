package br.com.unipds.artantech.ordersystem_jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Classe de chave composta (composite key) para ProductReview.
 *
 * CONCEITOS JPA DEMONSTRADOS:
 * - @Embeddable: Marca esta classe como incorporavel em outras entidades
 * - Chave composta: Multiplas colunas formam a chave primaria
 * - Serializable: Obrigatorio para chaves compostas em JPA
 * - equals() e hashCode(): Obrigatorios e devem usar TODOS os campos da chave
 *
 * QUANDO USAR CHAVE COMPOSTA:
 * - Identificacao natural do negocio (nao artificial)
 * - Combinacao de campos identifica unicamente o registro
 * - Exemplo: Um usuario avalia cada produto apenas uma vez
 *
 * ESTRUTURA DA CHAVE:
 * - userId: Quem avaliou
 * - productId: O que foi avaliado
 * - Combinacao (userId, productId) e unica
 *
 * COMPARACAO:
 * - @EmbeddedId: Usa esta classe como chave (ESTE EXEMPLO)
 * - @Id simples: Usa Long id auto-incremento (User, Order, Product, OrderItem)
 *
 * @see ProductReview
 * @see jakarta.persistence.Embeddable
 * @see jakarta.persistence.EmbeddedId
 */
@Embeddable
public class ProductReviewId implements Serializable {

    /**
     * ID do usuario que fez a avaliacao.
     * Faz parte da chave primaria composta.
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * ID do produto avaliado.
     * Faz parte da chave primaria composta.
     */
    @Column(name = "product_id")
    private Long productId;

    /**
     * Construtor padrao necessario para o JPA.
     */
    public ProductReviewId() {
    }

    /**
     * Construtor de conveniencia.
     *
     * @param userId    ID do usuario
     * @param productId ID do produto
     */
    public ProductReviewId(Long userId, Long productId) {
        this.userId = userId;
        this.productId = productId;
    }

    // ==================== Getters and Setters ====================

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    /**
     * IMPORTANTE: equals() DEVE usar TODOS os campos da chave composta.
     *
     * Duas avaliacoes sao iguais se tem o mesmo userId E productId.
     * Isso garante que um usuario nao pode avaliar o mesmo produto duas vezes.
     *
     * @param o Objeto a comparar
     * @return true se ambos os campos sao iguais
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ProductReviewId that = (ProductReviewId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(productId, that.productId);
    }

    /**
     * IMPORTANTE: hashCode() DEVE usar os MESMOS campos que equals().
     *
     * @return hash code baseado em userId e productId
     */
    @Override
    public int hashCode() {
        return Objects.hash(userId, productId);
    }

    @Override
    public String toString() {
        return "ProductReviewId{" +
                "userId=" + userId +
                ", productId=" + productId +
                '}';
    }
}