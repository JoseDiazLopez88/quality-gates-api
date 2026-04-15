package com.qualitygates.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.qualitygates.api.exception.ResourceNotFoundException;
import com.qualitygates.api.model.Product;
import com.qualitygates.api.repository.ProductRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests unitarios para ProductServiceImpl.
 * Mockito
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

        @Mock
        private ProductRepository productRepository;

        @InjectMocks
        private ProductServiceImpl productService;

        private Product product1;
        private Product product2;

        @BeforeEach
        void setUp() {
                product1 = new Product("Laptop HP", "Laptop gaming 16GB RAM",
                                1299.99, 10, "Electrónica");
                product1.setId(1L);

                product2 = new Product("Mouse Logitech", "Mouse inalámbrico",
                                49.99, 50, "Electrónica");
                product2.setId(2L);
        }

        @Test
        @DisplayName("Debe obtener todos los productos")
        void getAllProducts_ReturnsListOfProducts() {
                when(productRepository.findAll())
                                .thenReturn(Arrays.asList(product1, product2));

                List<Product> result = productService.getAllProducts();

                assertNotNull(result);
                assertEquals(2, result.size());
                verify(productRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Debe obtener un producto por ID")
        void getProductById_ExistingId_ReturnsProduct() {
                when(productRepository.findById(1L))
                                .thenReturn(Optional.of(product1));

                Product result = productService.getProductById(1L);

                assertNotNull(result);
                assertEquals("Laptop HP", result.getName());
                assertEquals(1299.99, result.getPrice());
                verify(productRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Debe lanzar excepción si el producto no existe")
        void getProductById_NonExistingId_ThrowsException() {
                when(productRepository.findById(99L))
                                .thenReturn(Optional.empty());

                assertThrows(ResourceNotFoundException.class,
                                () -> productService.getProductById(99L));

                verify(productRepository, times(1)).findById(99L);
        }

        @Test
        @DisplayName("Debe crear un producto exitosamente")
        void createProduct_ValidProduct_ReturnsCreatedProduct() {
                when(productRepository.save(any(Product.class)))
                                .thenReturn(product1);

                Product result = productService.createProduct(product1);

                assertNotNull(result);
                assertEquals("Laptop HP", result.getName());
                verify(productRepository, times(1)).save(any(Product.class));
        }

        @Test
        @DisplayName("Debe actualizar un producto existente")
        void updateProduct_ExistingId_ReturnsUpdatedProduct() {
                Product updatedDetails = new Product("Laptop HP Pro",
                                "Laptop gaming 32GB RAM", 1599.99, 5, "Electrónica");

                when(productRepository.findById(1L))
                                .thenReturn(Optional.of(product1));
                when(productRepository.save(any(Product.class)))
                                .thenReturn(updatedDetails);

                Product result = productService.updateProduct(1L, updatedDetails);

                assertNotNull(result);
                assertEquals("Laptop HP Pro", result.getName());
                assertEquals(1599.99, result.getPrice());
                verify(productRepository, times(1)).findById(1L);
                verify(productRepository, times(1)).save(any(Product.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción al actualizar producto inexistente")
        void updateProduct_NonExistingId_ThrowsException() {
                Product updatedDetails = new Product("Test", "Test", 10.0, 1, "Test");

                when(productRepository.findById(99L))
                                .thenReturn(Optional.empty());

                assertThrows(ResourceNotFoundException.class,
                                () -> productService.updateProduct(99L, updatedDetails));
        }

        @Test
        @DisplayName("Debe eliminar un producto existente")
        void deleteProduct_ExistingId_DeletesSuccessfully() {
                when(productRepository.findById(1L))
                                .thenReturn(Optional.of(product1));

                productService.deleteProduct(1L);

                verify(productRepository, times(1)).findById(1L);
                verify(productRepository, times(1)).delete(product1);
        }

        @Test
        @DisplayName("Debe lanzar excepción al eliminar producto inexistente")
        void deleteProduct_NonExistingId_ThrowsException() {
                when(productRepository.findById(99L))
                                .thenReturn(Optional.empty());

                assertThrows(ResourceNotFoundException.class,
                                () -> productService.deleteProduct(99L));
        }

        @Test
        @DisplayName("Debe obtener productos por categoría")
        void getProductsByCategory_ReturnsFilteredProducts() {
                when(productRepository.findByCategory("Electrónica"))
                                .thenReturn(Arrays.asList(product1, product2));

                List<Product> result = productService.getProductsByCategory("Electrónica");

                assertNotNull(result);
                assertEquals(2, result.size());
                verify(productRepository, times(1))
                                .findByCategory("Electrónica");
        }

        @Test
        @DisplayName("Debe buscar productos por nombre")
        void searchProductsByName_ReturnsMatchingProducts() {
                when(productRepository.findByNameContainingIgnoreCase("Laptop"))
                                .thenReturn(List.of(product1));

                List<Product> result = productService.searchProductsByName("Laptop");

                assertNotNull(result);
                assertEquals(1, result.size());
                assertEquals("Laptop HP", result.get(0).getName());
        }
}
