package br.com.unipds.artantech.ordersystem_jpa;

import br.com.unipds.artantech.ordersystem_jpa.entity.Order;
import br.com.unipds.artantech.ordersystem_jpa.entity.OrderItem;
import br.com.unipds.artantech.ordersystem_jpa.entity.Product;
import br.com.unipds.artantech.ordersystem_jpa.entity.User;
import br.com.unipds.artantech.ordersystem_jpa.enums.OrderStatus;
import br.com.unipds.artantech.ordersystem_jpa.repository.OrderRepository;
import br.com.unipds.artantech.ordersystem_jpa.repository.ProductRepository;
import br.com.unipds.artantech.ordersystem_jpa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@Transactional
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public DatabaseSeeder(UserRepository userRepository, ProductRepository productRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n===================================================");
        System.out.println("Iniciando a inserção de dados via JPA...\n");
        criarUsuarios();
        System.out.println("---------------------------------------------------");
        criarProdutos();
        System.out.println("---------------------------------------------------");
        criarPedidos();
        System.out.println("---------------------------------------------------");
        testarQueriesJpqlENativas();
        System.out.println("\nProcesso de inserção finalizado!");
        System.out.println("===================================================\n");
    }

    private void criarUsuarios() {
        if (userRepository.count() > 0) {
            System.out.println("Usuários já cadastrados anteriormente. Pulando criação.");
            return;
        }

        try {
            User u1 = new User("Maria Silva", "maria.silva@email.com");
            userRepository.save(u1);

            System.out.println("Usuários cadastrados no sistema:");
            for (User u : userRepository.findAll()) {
                System.out.println(u);
            }
        } catch (Exception e) {
            System.err.println("Erro ao criar usuários: " + e.getMessage());
        }

        try {
            User u2 = new User("Artanniel Fortes", "artanniel.fortes@email.com");
            userRepository.save(u2);

            System.out.println("Usuários cadastrados no sistema:");
            for (User u : userRepository.findAll()) {
                System.out.println(u);
            }
        } catch (Exception e) {
            System.err.println("Erro ao criar usuários: " + e.getMessage());
        }
    }

    private void criarProdutos() {
        if (productRepository.count() > 0) {
            System.out.println("Produtos já cadastrados anteriormente. Pulando criação.");
            return;
        }

        try {
            Product p1 = new Product("Notebook Dell", new BigDecimal("4500.00"), 10);
            productRepository.save(p1);

            Product p2 = new Product("Mouse Sem Fio", new BigDecimal("120.00"), 50);
            productRepository.save(p2);

            System.out.println("Produtos cadastrados no sistema:");
            for (Product p : productRepository.findAll()) {
                System.out.println(p);
            }
        } catch (Exception e) {
            System.err.println("Erro ao criar produtos: " + e.getMessage());
        }
    }

    private void criarPedidos() {
        if (orderRepository.count() > 0) {
            System.out.println("Pedidos já cadastrados anteriormente. Pulando criação.");
            
            System.out.println("Pedidos atuais no sistema:");
            for (Order o : orderRepository.findAll()) {
                System.out.println(o);
                System.out.println("   Itens do Pedido:");
                for (OrderItem oi : o.getItems()) {
                    System.out.println("   - " + oi);
                }
            }
            return;
        }

        try {
            List<User> users = userRepository.findAll();
            List<Product> products = productRepository.findAll();

            if (users.isEmpty() || products.isEmpty()) {
                System.out.println("Não há usuários ou produtos suficientes para criar um pedido.");
                return;
            }

            // Pega o primeiro usuário e os dois primeiros produtos
            User user = users.get(0);
            Product product1 = products.get(0);
            Product product2 = products.size() > 1 ? products.get(1) : product1;

            BigDecimal total = product1.getPrice().multiply(new BigDecimal("1"))
                    .add(product2.getPrice().multiply(new BigDecimal("2")));

            Order order = new Order(total, user);
            order.setStatus(OrderStatus.CREATED);

            // Criar itens do pedido
            OrderItem item1 = new OrderItem(1, product1);
            item1.setSubtotal(product1.getPrice().multiply(new BigDecimal("1")));
            order.addItem(item1); // Esse método sincroniza o order bidirecional

            if (products.size() > 1) {
                OrderItem item2 = new OrderItem(2, product2);
                item2.setSubtotal(product2.getPrice().multiply(new BigDecimal("2")));
                order.addItem(item2);
            }

            orderRepository.save(order);

            System.out.println("Pedidos cadastrados no sistema:");
            // Para pedidos precisaremos ter o fetchType do items lidado no Transactional se não fosse EAGER, mas o findAll costuma buscar de forma proxy. 
            // O OrderRepository tem um EntityGraph customizado? 
            for (Order o : orderRepository.findAll()) {
                System.out.println(o);
                System.out.println("   Itens do Pedido:");
                for (OrderItem oi : o.getItems()) {
                    System.out.println("   - " + oi);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao criar pedidos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void testarQueriesJpqlENativas() {
        System.out.println("--- EXECUTANDO QUERIES JPQL ---");

        System.out.println("\n[JPQL] Buscando produtos disponíveis (estoque > 0):");
        List<Product> availableProducts = productRepository.findAvailableProducts();
        for (Product p : availableProducts) {
            System.out.println("   -> " + p.getName() + " (Estoque: " + p.getStock() + ")");
        }

        System.out.println("\n[JPQL] Buscando usuário específico por email:");
        userRepository.findByEmailUsingJPQL("maria.silva@email.com").ifPresent(u ->
                System.out.println("   -> Usuário encontrado: " + u.getName() + " (" + u.getEmail() + ")")
        );

        System.out.println("\n--- EXECUTANDO NATIVE QUERIES ---");

        System.out.println("\n[NATIVE] Valor total em inventário (Soma de preço * estoque):");
        BigDecimal totalInventory = productRepository.calculateTotalInventoryValue();
        System.out.println("   -> R$ " + totalInventory);

        System.out.println("\n[NATIVE] Contagem de pedidos agrupados por status:");
        List<Object[]> statusCount = orderRepository.countOrdersByStatus();
        for (Object[] row : statusCount) {
            String status = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            System.out.println("   -> Status: " + status + " | Quantidade: " + count);
        }
    }
}
