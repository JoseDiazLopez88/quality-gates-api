package com.qualitygates.api.controller;

import com.qualitygates.api.model.Product;
import com.qualitygates.api.service.ProductService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para la gestión de productos.
 * Expone los endpoints CRUD de la API.
 */
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    /**
     * Constructor con inyección de dependenciass.
     *
     * @param productService servicio de productos
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Obtiene todos los productos.
     *
     * @return lista de productos
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * Obtiene un producto por su ID.
     *
     * @param id identificador del producto
     * @return el producto encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * Crea un nuevo producto.
     *
     * @param product datos del producto
     * @return el producto creado
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(
            @Valid @RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    /**
     * Actualiza un producto existente.
     *
     * @param id      identificador del producto
     * @param product datos actualizados
     * @return el producto actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(id, product);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Elimina un producto.
     *
     * @param id identificador del producto
     * @return mensaje de confirmación
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(
            @PathVariable Long id) {
        productService.deleteProduct(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Producto eliminado exitosamente");
        response.put("id", id.toString());
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene productos por categoría.
     *
     * @param category la categoría a consultar
     * @return lista de productos
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(
            @PathVariable String category) {
        List<Product> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    /**
     * Busca productos por nombre.
     *
     * @param name texto a buscar
     * @return lista de productos que coinciden
     */
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(
            @RequestParam String name) {
        List<Product> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
    }
}
