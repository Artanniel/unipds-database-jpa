package br.com.unipds.artantech.ordersystem_jpa.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidade JPA que representa um produto no catalogo do sistema.
 *
 * Mapeamento para a tabela 'products' no banco de dados MySQL.
 *
 * CONCEITOS JPA DEMONSTRADOS:
 * - BigDecimal: Tipo adequado para valores monetarios (evita erros de
 * arredondamento de float/double)
 * - precision e scale: Controlam a precisao de numeros decimais (10,2 = 8
 * digitos antes e 2 depois da virgula)
 * - columnDefinition: Define o tipo SQL exato da coluna, incluindo defaults do
 * banco
 * - @OneToMany sem cascade: Relacionamento inverso que nao propaga operacoes
 * - FetchType.LAZY: Carregamento preguicoso para evitar queries desnecessarias
 *
 * @see OrderItem
 * @see br.com.unipds.ordersystemjpa.repository.ProductRepository
 */
@Entity
@Table(name = "products")
public class Product {

    /**
     * Identificador unico do produto (chave primaria).
     * Gerado automaticamente pelo banco de dados.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nome descritivo do produto.
     * Obrigatorio com tamanho maximo de 120 caracteres.
     */
    @Column(nullable = false, length = 120)
    private String name;

    /**
     * Preco unitario do produto.
     *
     * IMPORTANTE: Sempre use BigDecimal para valores monetarios!
     * - precision=10: Total de digitos (ex: 99999999.99)
     * - scale=2: Numero de casas decimais (centavos)
     * - Evita erros de arredondamento que ocorrem com float/double
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Quantidade disponivel em estoque.
     *
     * columnDefinition especifica o tipo SQL exato incluindo o DEFAULT.
     * O banco de dados aplica DEFAULT 0 se nenhum valor for fornecido.
     */
    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer stock;

    /**
     * Data e hora de cadastro do produto.
     *
     * Gerenciado automaticamente pelo banco via DEFAULT CURRENT_TIMESTAMP.
     * insertable=false e updatable=false impedem o JPA de incluir este campo
     * nos comandos INSERT e UPDATE.
     */
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Lista de itens de pedidos que incluem este produto.
     *
     * Relacionamento bidirecional One-to-Many (1:N):
     * - Um produto pode estar em muitos itens de pedidos
     * - mappedBy="product" indica que OrderItem.product e o lado owner
     * - SEM cascade: Operacoes em Product NAO sao propagadas para OrderItems
     * - SEM orphanRemoval: Remover produto da lista nao deleta OrderItems
     * - FetchType padrao: LAZY (carrega itens somente quando acessados)
     *
     * Nota: Esta lista geralmente e usada apenas para consultas/relatorios,
     * nao para criar novos itens (isso e feito a partir de Order).
     */
    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems = new ArrayList<>();

    /**
     * Construtor padrao necessario para o JPA.
     */
    public Product() {
    }

    /**
     * Construtor de conveniencia para criar um produto com dados basicos.
     *
     * @param name  Nome do produto
     * @param price Preco unitario (use BigDecimal para valores monetarios)
     * @param stock Quantidade em estoque
     */
    public Product(String name, BigDecimal price, Integer stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    // ==================== Getters and Setters ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    /**
     * Compara dois produtos com base no ID.
     * Implementacao padrao para entidades JPA.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    /**
     * Gera o hash code baseado no ID.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Representacao em String do produto.
     * Nao inclui orderItems para evitar loops infinitos.
     */
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", createdAt=" + createdAt +
                '}';
    }
}