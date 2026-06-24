-- V3: Seed de 20 produtos com IDs explícitos (determinísticos para as migrations seguintes)
INSERT INTO `ordersystem_db`.`products` (`id`, `name`, `price`, `stock`) VALUES
( 1, 'Notebook Dell Inspiron 15',    4500.00, 10),
( 2, 'Mouse Sem Fio Logitech MX3',    189.90, 50),
( 3, 'Teclado Mecânico Redragon',     349.90, 30),
( 4, 'Monitor LG 24" Full HD',       1099.00, 15),
( 5, 'Headset HyperX Cloud II',       549.90, 25),
( 6, 'Webcam Logitech C920',          449.00, 20),
( 7, 'SSD Kingston 480GB',            289.90, 40),
( 8, 'Memória RAM Corsair 16GB DDR4', 399.00, 35),
( 9, 'Placa de Vídeo GTX 1650',      1800.00,  8),
(10, 'Processador Intel i5-12400F',  1250.00, 12),
(11, 'Roteador TP-Link AC1200',       219.90, 45),
(12, 'Impressora HP Deskjet 2774',    499.00, 18),
(13, 'HD Externo Seagate 1TB',        349.00, 22),
(14, 'Carregador Universal USB-C',     89.90, 60),
(15, 'Suporte para Notebook',         129.90, 55),
(16, 'Mouse Pad Gamer XL',             59.90, 70),
(17, 'Hub USB 7 Portas',               99.90, 48),
(18, 'Cabo HDMI 2m Gold',              39.90, 80),
(19, 'Adaptador USB-C para HDMI',      79.90, 65),
(20, 'Caixa de Som Bluetooth JBL Go3', 249.90, 38)
AS new_data
ON DUPLICATE KEY UPDATE
  `name`  = new_data.`name`,
  `price` = new_data.`price`,
  `stock` = new_data.`stock`;
