package br.com.unipds.artantech.ordersystem_jpa.enums;

/**
 * Enum que representa os possiveis estados de um pedido no sistema.
 *
 * Este enum e mapeado para o tipo ENUM do MySQL na tabela orders.
 * O JPA usa @Enumerated(EnumType.STRING) para armazenar o nome da constante
 * ao inves do ordinal (posicao numerica), o que e mais seguro e legivel.
 *
 * Fluxo de estados tipico:
 * CREATED -> PAID (quando o pagamento e confirmado)
 * CREATED -> CANCELLED (quando o pedido e cancelado antes do pagamento)
 * PAID -> CANCELLED (estorno/cancelamento apos pagamento)
 *
 * @see br.com.unipds.ordersystemjpa.entity.Order
 */
public enum OrderStatus {

    /**
     * Pedido pendente.
     * Criado pelo projeto JDBC.
     */
    PENDING,

    /**
     * Pedido criado mas ainda nao pago.
     * Estado inicial de todo pedido.
     */
    CREATED,

    /**
     * Pedido pago e confirmado.
     * Transicao tipica: CREATED -> PAID
     */
    PAID,

    /**
     * Pedido cancelado pelo usuario ou sistema.
     * Pode ocorrer em qualquer estado anterior.
     */
    CANCELLED,

    /**
     * Pedido em processamento/separacao.
     * Transicao tipica: PAID -> PROCESSING
     */
    PROCESSING,

    /**
     * Pedido enviado para entrega.
     * Transicao tipica: PROCESSING -> SHIPPED
     */
    SHIPPED,

    /**
     * Pedido entregue ao destinatario.
     * Estado final positivo: SHIPPED -> DELIVERED
     */
    DELIVERED
}