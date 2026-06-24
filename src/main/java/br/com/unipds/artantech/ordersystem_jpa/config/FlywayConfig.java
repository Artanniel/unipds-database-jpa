package br.com.unipds.artantech.ordersystem_jpa.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do Flyway para garantir que registros de migrations com falha
 * sejam limpos automaticamente antes de cada execução do migrate().
 *
 * O repair() remove entradas FAILED da tabela flyway_schema_history e
 * recalcula checksums, permitindo que as migrations sejam re-executadas
 * sem intervenção manual no banco de dados.
 */
@Configuration
public class FlywayConfig {

    @Bean
    public FlywayMigrationStrategy repairBeforeMigrate() {
        return (Flyway flyway) -> {
            flyway.repair();
            flyway.migrate();
        };
    }
}
