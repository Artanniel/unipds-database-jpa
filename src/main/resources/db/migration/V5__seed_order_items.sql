-- V5: Seed de 20 itens de pedido
-- FK checks desabilitados para garantir seed mesmo com estado variável do banco
SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO `ordersystem_db`.`order_items` (`order_id`, `product_id`, `quantity`, `subtotal`) VALUES
( 1,  1, 1,  4500.00),
( 1,  2, 1,   189.90),
( 2,  2, 1,   189.90),
( 3,  1, 1,  4500.00),
( 3,  3, 1,   349.90),
( 4,  4, 1,  1099.00),
( 4,  5, 1,   549.90),
( 5,  6, 1,   449.00),
( 5,  7, 1,   289.90),
( 6,  4, 1,  1099.00),
( 7,  8, 1,   399.00),
( 7,  3, 1,   349.90),
( 7,  2, 1,   189.90),
( 8,  9, 1,  1800.00),
( 8, 10, 1,  1250.00),
( 9,  1, 1,  4500.00),
(10,  6, 1,   449.00),
(10, 14, 1,    89.90),
(10, 18, 1,    39.90),
(11,  5, 1,   549.90);

SET FOREIGN_KEY_CHECKS = 1;
