--liquibase formatted sql

--changeset ordersystem:15
--comment: Inserir 20 usuarios
INSERT INTO `ordersystem_db`.`users` (`id`, `name`, `email`) VALUES
 ( 1, 'Ana Carolina Souza',    'ana.souza@email.com'),
 ( 2, 'Bruno Oliveira',        'bruno.oliveira@email.com'),
 ( 3, 'Carla Mendes',          'carla.mendes@email.com'),
 ( 4, 'Diego Ferreira',        'diego.ferreira@email.com'),
 ( 5, 'Eduarda Lima',          'eduarda.lima@email.com'),
 ( 6, 'Felipe Alves',          'felipe.alves@email.com'),
 ( 7, 'Gabriela Costa',        'gabriela.costa@email.com'),
 ( 8, 'Henrique Rocha',        'henrique.rocha@email.com'),
 ( 9, 'Isabela Martins',       'isabela.martins@email.com'),
 (10, 'João Pedro Santos',     'joao.santos@email.com'),
 (11, 'Karina Neves',          'karina.neves@email.com'),
 (12, 'Lucas Barbosa',         'lucas.barbosa@email.com'),
 (13, 'Mariana Gomes',         'mariana.gomes@email.com'),
 (14, 'Nicolas Carvalho',      'nicolas.carvalho@email.com'),
 (15, 'Olivia Ribeiro',        'olivia.ribeiro@email.com'),
 (16, 'Pedro Henrique Silva',  'pedro.silva@email.com'),
 (17, 'Queila Azevedo',        'queila.azevedo@email.com'),
 (18, 'Rafael Torres',         'rafael.torres@email.com'),
 (19, 'Sabrina Freitas',       'sabrina.freitas@email.com'),
 (20, 'Thiago Nascimento',     'thiago.nascimento@email.com')
    AS new_data
ON DUPLICATE KEY UPDATE
                     `name`  = new_data.`name`,
                     `email` = new_data.`email`;