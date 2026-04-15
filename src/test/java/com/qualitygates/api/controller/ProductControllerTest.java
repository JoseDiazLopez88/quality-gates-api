package com.qualitygates.api.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qualitygates.api.exception.ResourceNotFoundException;
import com.qualitygates.api.model.Product;
import com.qualitygates.api.service.ProductService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Tests del controlador REST con MockMvc.
 * Valida endpoints, status codes y respuestas JSON.
 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private ProductService productService;

        @Autowired
        private ObjectMapper objectMapper;

        private Product product1;
        private Product product2;

        private static final String API_URL = "/api/v1/products";

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
        @DisplayName("GET /api/v1/products - Debe retornar lista de productos")
        void getAllProducts_ReturnsOkWithList() throws Exception {
                when(productService.getAllProducts())
                                .thenReturn(Arrays.asList(product1, product2));

                mockMvc.perform(get(API_URL))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[0].name", is("Laptop HP")))
                                .andExpect(jsonPath("$[1].name", is("Mouse Logitech")));
        }

        @Test
        @DisplayName("GET /api/v1/products/{id} - Debe retornar producto")
        void getProductById_ExistingId_ReturnsOk() throws Exception {
                when(productService.getProductById(1L)).thenReturn(product1);

                mockMvc.perform(get(API_URL + "/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name", is("Laptop HP")))
                                .andExpect(jsonPath("$.price", is(1299.99)));
        }

        @Test
        @DisplayName("GET /api/v1/products/{id} - Debe retornar 404")
        void getProductById_NonExistingId_ReturnsNotFound() throws Exception {
                when(productService.getProductById(99L))
                                .thenThrow(new ResourceNotFoundException(
                                                "Producto no encontrado con ID: 99"));

                mockMvc.perform(get(API_URL + "/99"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.error",
                                                is("Recurso no encontrado")));
        }

        @Test
        @DisplayName("POST /api/v1/products - Debe crear producto")
        void createProduct_ValidData_ReturnsCreated() throws Exception {
                when(productService.createProduct(any(Product.class)))
                                .thenReturn(product1);

                mockMvc.perform(post(API_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(product1)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.name", is("Laptop HP")))
                                .andExpect(jsonPath("$.id", is(1)));
        }

        @Test
        @DisplayName("POST /api/v1/products - Debe retornar 400 con datos inválidos")
        void createProduct_InvalidData_ReturnsBadRequest() throws Exception {
                Product invalidProduct = new Product();
                invalidProduct.setPrice(-10.0);

                mockMvc.perform(post(API_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidProduct)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("PUT /api/v1/products/{id} - Debe actualizar producto")
        void updateProduct_ValidData_ReturnsOk() throws Exception {
                Product updatedProduct = new Product("Laptop HP Pro",
                                "32GB RAM", 1599.99, 5, "Electrónica");
                updatedProduct.setId(1L);

                when(productService.updateProduct(eq(1L), any(Product.class)))
                                .thenReturn(updatedProduct);

                mockMvc.perform(put(API_URL + "/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                updatedProduct)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name", is("Laptop HP Pro")))
                                .andExpect(jsonPath("$.price", is(1599.99)));
        }

        @Test
        @DisplayName("DELETE /api/v1/products/{id} - Debe eliminar producto")
        void deleteProduct_ExistingId_ReturnsOk() throws Exception {
                doNothing().when(productService).deleteProduct(1L);

                mockMvc.perform(delete(API_URL + "/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message",
                                                is("Producto eliminado exitosamente")));
        }

        @Test
        @DisplayName("DELETE /api/v1/products/{id} - Debe retornar 404")
        void deleteProduct_NonExistingId_ReturnsNotFound() throws Exception {
                doThrow(new ResourceNotFoundException(
                                "Producto no encontrado con ID: 99"))
                                .when(productService).deleteProduct(99L);

                mockMvc.perform(delete(API_URL + "/99"))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("GET /api/v1/products/category/{cat} - Filtra por categoría")
        void getProductsByCategory_ReturnsFilteredList() throws Exception {
                when(productService.getProductsByCategory("Electrónica"))
                                .thenReturn(Arrays.asList(product1, product2));

                mockMvc.perform(get(API_URL + "/category/Electrónica"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)));
        }

        @Test
        @DisplayName("GET /api/v1/products/search?name=Laptop - Busca por nombre")
        void searchProducts_ReturnsMatchingProducts() throws Exception {
                when(productService.searchProductsByName("Laptop"))
                                .thenReturn(List.of(product1));

                mockMvc.perform(get(API_URL + "/search")
                                .param("name", "Laptop"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].name", is("Laptop HP")));
        }
}
