package br.com.unipds.artantech.ordersystem_jpa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade JPA que representa uma avaliacao de produto feita por um usuario.
 *
 * Mapeamento para a tabela 'product_reviews' no banco de dados MySQL.
 *
 * CONCEITOS JPA DEMONSTRADOS - CHAVE COMPOSTA:
 * - @EmbeddedId: Usa uma classe separada (ProductReviewId) como chave primaria
 * - @MapsId: Mapeia o relacionamento @ManyToOne para parte da chave composta
 * - Chave composta = PRIMARY KEY (user_id, product_id)
 * - Garante unicidade: Um usuario avalia cada produto apenas uma vez
 *
 * COMPARACAO COM @Id SIMPLES:
 *
 * ESTE EXEMPLO (@EmbeddedId - Chave Composta):
 * ```
 * 
 * @EmbeddedId
 *             private ProductReviewId id; // Composto: userId + productId
 *
 *             @MapsId("userId") // Liga User ao campo userId da chave
 *             private User user;
 *             ```
 *             - Chave natural do negocio (userId + productId)
 *             - NAO permite duplicatas (usuario + produto)
 *             - Mais complexo, mas expressa regra de negocio
 *
 *             OUTRAS ENTIDADES (@Id - Chave Simples):
 *             ```
 * @Id
 * @GeneratedValue(strategy = GenerationType.IDENTITY)
 *                          private Long id; // Auto-incremento
 *
 * @ManyToOne
 *            private User user; // Relacionamento separado da chave
 *            ```
 *            - Chave artificial (nao tem significado de negocio)
 *            - Permite duplicatas se necessario
 *            - Mais simples e flexivel
 *
 *            POR QUE USAR @EmbeddedId AQUI?
 *            - Um usuario NAO DEVE avaliar o mesmo produto duas vezes
 *            - A combinacao (usuario, produto) identifica unicamente a
 *            avaliacao
 *            - A chave composta IMPOE esta regra no banco de dados
 *
 * @see ProductReviewId
 * @see User
 * @see Product
 * @see br.com.unipds.ordersystemjpa.repository.ProductReviewRepository
 */
@Entity
@Table(name = "product_reviews")
public class ProductReview {

    /**
     * Chave primaria composta.
     *
     * @EmbeddedId indica que a chave e uma classe @Embeddable separada.
     *             A chave e composta por userId + productId.
     *
     *             IMPORTANTE: Nao use @GeneratedValue com @EmbeddedId!
     *             Os valores da chave devem ser fornecidos manualmente.
     */
    @EmbeddedId
    private ProductReviewId id;

    /**
     * Usuario que fez a avaliacao.
     *
     * @MapsId("userId"): Liga este relacionamento ao campo userId da chave
     * composta.
     * - "userId" e o nome do campo em ProductReviewId
     * - Isso sincroniza User.id com ProductReviewId.userId
     * - A FK user_id faz parte da PK
     *
     * DIFERENCA para @Id simples:
     * - Com @Id simples: user_id seria apenas FK (nao PK)
     * - Com @EmbeddedId + @MapsId: user_id e FK E parte da PK
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Produto avaliado.
     *
     * @MapsId("productId"): Liga este relacionamento ao campo productId da chave.
     * Similar ao user, product_id e FK E parte da PK.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("productId")
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * Nota da avaliacao (1 a 5 estrelas).
     */
    @Column(nullable = false)
    private Integer rating;

    /**
     * Comentario opcional do usuario sobre o produto.
     */
    @Column(length = 500)
    private String comment;

    /**
     * Data e hora em que a avaliacao foi criada.
     * Gerenciado pelo banco via DEFAULT CURRENT_TIMESTAMP.
     */
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Construtor padrao necessario para o JPA.
     */
    public ProductReview() {
    }

    /**
     * Construtor de conveniencia.
     *
     * IMPORTANTE: Ao criar uma avaliacao com @EmbeddedId, voce tem duas opcoes:
     *
     * Opcao 1 - Criar o ID manualmente:
     * ```
     * ProductReviewId id = new ProductReviewId(user.getId(), product.getId());
     * ProductReview review = new ProductReview(id, user, product, 5, "Otimo!");
     * ```
     *
     * Opcao 2 - Criar sem ID, setar user/product, JPA preenche o ID:
     * ```
     * ProductReview review = new ProductReview();
     * review.setUser(user);
     * review.setProduct(product);
     * review.setRating(5);
     * // JPA extrai user.id e product.id para preencher o @EmbeddedId
     * ```
     *
     * @param id      Chave composta
     * @param user    Usuario que avaliou
     * @param product Produto avaliado
     * @param rating  Nota (1-5)
     * @param comment Comentario opcional
     */
    public ProductReview(ProductReviewId id, User user, Product product, Integer rating, String comment) {
        this.id = id;
        this.user = user;
        this.product = product;
        this.rating = rating;
        this.comment = comment;
    }

    // ==================== Getters and Setters ====================

    public ProductReviewId getId() {
        return id;
    }

    public void setId(ProductReviewId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Compara avaliacoes baseado na chave composta (id).
     *
     * IMPORTANTE: Com @EmbeddedId, equals() deve comparar o objeto id completo.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ProductReview that = (ProductReview) o;
        return Objects.equals(id, that.id);
    }

    /**
     * Hash code baseado na chave composta.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ProductReview{" +
                "id=" + id +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}