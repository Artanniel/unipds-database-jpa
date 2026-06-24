--liquibase formatted sql

--changeset ordersystem:17
--comment: Inserir 20 pedidos com status variados e totais realistas

-- Inserir 20 pedidos com status variados e totais realistas
SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO `ordersystem_db`.`orders` (`user_id`, `total`, `status`) VALUES
 ( 1, 4689.90, 'DELIVERED'),
 ( 2,  189.90, 'SHIPPED'),
 ( 3, 4849.90, 'PROCESSING'),
 ( 4, 1448.90, 'PENDING'),
 ( 5,  539.80, 'DELIVERED'),
 ( 6, 1099.00, 'CANCELLED'),
 ( 7,  899.80, 'DELIVERED'),
 ( 8, 2099.90, 'SHIPPED'),
 ( 9, 4500.00, 'PROCESSING'),
 (10,  689.80, 'PENDING'),
 (11, 1549.80, 'DELIVERED'),
 (12,  449.90, 'SHIPPED'),
 (13, 3050.00, 'PROCESSING'),
 (14,  289.90, 'DELIVERED'),
 (15, 1649.90, 'PENDING'),
 (16,  829.80, 'CANCELLED'),
 (17,  599.90, 'DELIVERED'),
 (18, 2350.00, 'SHIPPED'),
 (19,  169.80, 'DELIVERED'),
 (20, 5749.90, 'PROCESSING');

SET FOREIGN_KEY_CHECKS = 1;