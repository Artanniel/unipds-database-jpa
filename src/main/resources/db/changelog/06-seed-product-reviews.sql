--liquibase formatted sql

--changeset ordersystem:19
--comment: Inserir 18 product_reviews com chave composta (user_id, product_id)

-- Inserir 18 product_reviews (ratings de 1 a 5, com comentarios variados)
-- Produtos mais populares terao mais avaliacoes
SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO `ordersystem_db`.`product_reviews` (`product_id`, `user_id`, `rating`, `comment`, `created_at`) VALUES
( 1,  1, 5, 'Notebook excelente, muito rápido e tela ótima!',            NOW()),
( 1,  2, 4, 'Boa máquina, mas poderia ter mais RAM de fábrica.',         NOW()),
( 2,  3, 5, 'Mouse perfeito, bateria dura semanas.',                     NOW()),
( 2,  4, 3, 'Scroll um pouco duro no começo, mas se acostuma.',         NOW()),
( 3,  5, 5, 'Teclado incrível, teclas responsivas e iluminação linda.',  NOW()),
( 4,  6, 4, 'Monitor com ótima resolução para o preço.',                NOW()),
( 5,  7, 5, 'Headset com cancelamento de ruído surpreendente.',         NOW()),
( 6,  8, 4, 'Webcam com boa qualidade de imagem em 1080p.',             NOW()),
( 7,  9, 5, 'SSD extremamente rápido, boot em 8 segundos.',             NOW()),
( 8, 10, 4, 'Memória estável, sem aquecimento excessivo.',              NOW()),
( 9, 11, 5, 'Placa de vídeo entregou o que prometeu nos games.',        NOW()),
(10, 12, 4, 'Processador potente para o valor, recomendo.',             NOW()),
(11, 13, 3, 'Roteador razoável, sinal bom só até 10 metros.',           NOW()),
(12, 14, 4, 'Impressora fácil de configurar e boa qualidade.',          NOW()),
(13, 15, 5, 'HD externo compacto e muito rápido para backup.',          NOW()),
(14, 16, 5, 'Carregador universal resolveu minha vida na viagem.',      NOW()),
(15, 17, 4, 'Suporte firme e ajuda muito na postura.',                  NOW()),
(16, 18, 5, 'Mouse pad enorme, cabem mouse e teclado com folga.',       NOW()),
(17, 19, 3, 'Hub funcional mas esquenta um pouco com muitos devices.',  NOW()),
(18, 20, 5, 'Cabo HDMI sem perda de qualidade, imagem nítida.',         NOW())
    AS new_data
ON DUPLICATE KEY UPDATE
                     `rating`  = new_data.`rating`,
                     `comment` = new_data.`comment`;

SET FOREIGN_KEY_CHECKS = 1;