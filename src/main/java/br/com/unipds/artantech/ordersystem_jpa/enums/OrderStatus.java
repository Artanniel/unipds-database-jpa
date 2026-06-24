package br.com.unipds.artantech.ordersystem_jpa.enums;

/**
 * Enum que representa os possiveis estados de um pedido no sistema.
 *
 * Este enum e mapeado para o tipo ENUM do MySQL na tabela orders.
 * O JPA usa @Enumerated(EnumType.STRING) para armazenar o nome da constante
 * ao inves do ordinal (posicao numerica), o que e mais seguro e legivel.
 *
 * Fluxo de estados tipico:
 * CREATED -> PROCESSING (quando o pagamento e confirmado e o pedido entra em
 * separacao)
 * PROCESSING -> SHIPPED (quando o pedido e despachado)
 * SHIPPED -> DELIVERED (quando o pedido e entregue)
 * CREATED -> CANCELLED (cancelamento antes do pagamento)
 *
 * @see br.com.unipds.artantech.ordersystem_jpa.entity.Order
 */
public enum OrderStatus {

    /**
     * Pedido pendente de acao.
     * Criado pelo projeto JDBC.
     */
    PENDING,

    /**
     * Pedido criado mas ainda nao pago.
     * Estado inicial de todo pedido no sistema JPA.
     */
    CREATED,

    /**
     * Pedido pago e confirmado.
     */
    PAID,

    /**
     * Pedido em processamento/separacao no estoque.
     */
    PROCESSING,

    /**
     * Pedido despachado para entrega.
     */
    SHIPPED,

    /**
     * Pedido entregue ao destinatario.
     */
    DELIVERED,

    /**
     * Pedido cancelado pelo usuario ou sistema.
     * Pode ocorrer em qualquer estado anterior.
     */
    CANCELLED
}