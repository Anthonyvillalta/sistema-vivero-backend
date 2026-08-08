package com.vivero.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseMigrationRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        try {
            jdbcTemplate.execute("ALTER TABLE inventory_movements MODIFY COLUMN movement_type VARCHAR(50) NOT NULL");
            log.info(">>> [MIGRACION MYSQL] Columna movement_type actualizada exitosamente a VARCHAR(50).");
        } catch (Exception e) {
            log.info(">>> [MIGRACION MYSQL] Info de columna movement_type: {}", e.getMessage());
        }

        runMigration("orders.delivery_time_slot ampliada a TEXT",
                "ALTER TABLE orders MODIFY COLUMN delivery_time_slot TEXT NULL");

        runMigration("deliveries.destination_latitude",
                "ALTER TABLE deliveries ADD COLUMN destination_latitude DECIMAL(10,8) NULL");
        runMigration("deliveries.destination_longitude",
                "ALTER TABLE deliveries ADD COLUMN destination_longitude DECIMAL(11,8) NULL");
        runMigration("deliveries.gps_accuracy",
                "ALTER TABLE deliveries ADD COLUMN gps_accuracy DECIMAL(10,2) NULL");
        runMigration("deliveries.gps_speed",
                "ALTER TABLE deliveries ADD COLUMN gps_speed DECIMAL(10,2) NULL");
        runMigration("company_settings.warehouse_latitude",
                "ALTER TABLE company_settings ADD COLUMN warehouse_latitude DECIMAL(10,8) NULL");
        runMigration("company_settings.warehouse_longitude",
                "ALTER TABLE company_settings ADD COLUMN warehouse_longitude DECIMAL(11,8) NULL");
    }

    private void runMigration(String name, String sql) {
        try {
            jdbcTemplate.execute(sql);
            log.info(">>> [MIGRACION MYSQL] Columna {} creada exitosamente.", name);
        } catch (Exception e) {
            log.info(">>> [MIGRACION MYSQL] Info de columna {}: {}", name, e.getMessage());
        }
    }
}
