package com.qualitygates.api.config;

import com.qualitygates.api.model.Product;
import com.qualitygates.api.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner loadData(ProductRepository repository) {
        return args -> {
            // Solo inserta si la base de datos está completamente vacía
            if (repository.count() == 0) {
                repository
                        .save(new Product("Laptop Gamer", "Laptop ASUS ROG con RTX 4060", 1500.00, 10, "Electrónica"));
                repository.save(new Product("Teclado Mecánico", "Teclado RGB Switches Red", 85.50, 25, "Periféricos"));
                repository.save(new Product("Monitor 27'", "Monitor Ultrawide 144Hz", 320.00, 15, "Monitores"));
                repository.save(new Product("Audífonos Bluetooth", "Audífonos con cancelación de ruido activa", 120.00,
                        30, "Audio"));

                System.out.println("✅ Datos de prueba insertados exitosamente en la base de datos.");
            }
        };
    }
}
