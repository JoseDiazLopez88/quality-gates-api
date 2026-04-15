package com.qualitygates.api.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.qualitygates.api.model.Product;
import com.qualitygates.api.repository.ProductRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

/**
 * Tests de integración end-to-end.
 * Levanta el contexto completo de Spring Boot con H2.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ProductIntegrationTest {

        @LocalServerPort
        private int port;

        @Autowired
        private TestRestTemplate restTemplate;

        @Autowired
        private ProductRepository productRepository;

        private String baseUrl;

        @BeforeEach
        void setUp() {
                baseUrl = "http://localhost:" + port + "/api/v1/products";
                productRepository.deleteAll();
        }

        // @Test
        // @DisplayName("Integración: Flujo completo CRUD de productos")
        // void fullCrudFlow() {
        // // CREATE - Crear producto
        // Product newProduct = new Product("Teclado Mecánico",
        // "Teclado RGB Cherry MX", 89.99, 25, "Periféricos");

        // ResponseEntity<Product> createResponse = restTemplate
        // .postForEntity(baseUrl, newProduct, Product.class);

        // assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        // assertNotNull(createResponse.getBody());
        // assertNotNull(createResponse.getBody().getId());

        // Long productId = createResponse.getBody().getId();

        // // READ - Obtener producto por ID
        // ResponseEntity<Product> getResponse = restTemplate
        // .getForEntity(baseUrl + "/" + productId, Product.class);

        // assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        // assertEquals("Teclado Mecánico", getResponse.getBody().getName());

        // // UPDATE - Actualizar producto
        // Product updatedProduct = new Product("Teclado Mecánico Pro",
        // "Teclado RGB Cherry MX Brown", 119.99, 15, "Periféricos");

        // restTemplate.put(baseUrl + "/" + productId, updatedProduct);

        // ResponseEntity<Product> updatedResponse = restTemplate
        // .getForEntity(baseUrl + "/" + productId, Product.class);

        // assertEquals("Teclado Mecánico Pro",
        // updatedResponse.getBody().getName());
        // assertEquals(119.99, updatedResponse.getBody().getPrice());

        // // DELETE - Eliminar producto
        // restTemplate.delete(baseUrl + "/" + productId);

        // ResponseEntity<Product> deletedResponse = restTemplate
        // .getForEntity(baseUrl + "/" + productId, Product.class);

        // assertEquals(HttpStatus.NOT_FOUND, deletedResponse.getStatusCode());
        // }

        // @Test
        // @DisplayName("Integración: Listar todos los productos")
        // void getAllProducts_ReturnsAllProducts() {
        // // Insertar datos de prueba
        // productRepository.save(new Product("Producto 1",
        // "Desc 1", 10.0, 5, "Cat1"));
        // productRepository.save(new Product("Producto 2",
        // "Desc 2", 20.0, 10, "Cat2"));

        // ResponseEntity<List<Product>> response = restTemplate.exchange(
        // baseUrl,
        // HttpMethod.GET,
        // null,
        // new ParameterizedTypeReference<List<Product>>() { });

        // assertEquals(HttpStatus.OK, response.getStatusCode());
        // assertNotNull(response.getBody());
        // assertEquals(2, response.getBody().size());
        // }

        // @Test
        // @DisplayName("Integración: Filtrar por categoría")
        // void getProductsByCategory_ReturnsFiltered() {
        // productRepository.save(new Product("Laptop",
        // "Laptop HP", 999.99, 5, "Electrónica"));
        // productRepository.save(new Product("Silla",
        // "Silla gamer", 299.99, 3, "Muebles"));
        // productRepository.save(new Product("Monitor",
        // "Monitor 27 pulgadas", 399.99, 8, "Electrónica"));

        // ResponseEntity<List<Product>> response = restTemplate.exchange(
        // baseUrl + "/category/Electrónica",
        // HttpMethod.GET,
        // null,
        // new ParameterizedTypeReference<List<Product>>() { });

        // assertEquals(HttpStatus.OK, response.getStatusCode());
        // assertEquals(2, response.getBody().size());
        // }

        // @Test
        // @DisplayName("Integración: Buscar productos por nombre")
        // void searchProducts_ReturnsMatching() {
        // productRepository.save(new Product("Laptop HP",
        // "Gaming", 999.99, 5, "Electrónica"));
        // productRepository.save(new Product("Laptop Dell",
        // "Oficina", 799.99, 3, "Electrónica"));
        // productRepository.save(new Product("Mouse",
        // "Inalámbrico", 29.99, 20, "Periféricos"));

        // ResponseEntity<List<Product>> response = restTemplate.exchange(
        // baseUrl + "/search?name=Laptop",
        // HttpMethod.GET,
        // null,
        // new ParameterizedTypeReference<List<Product>>() { });

        // assertEquals(HttpStatus.OK, response.getStatusCode());
        // assertEquals(2, response.getBody().size());
        // }

        // @Test
        // @DisplayName("Integración: Producto no encontrado retorna 404")
        // void getProductById_NotFound_Returns404() {
        // ResponseEntity<String> response = restTemplate
        // .getForEntity(baseUrl + "/999", String.class);

        // assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        // }
}
