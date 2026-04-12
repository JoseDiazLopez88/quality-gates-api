package com.qualitygates.api.service;

import com.qualitygates.api.exception.ResourceNotFoundException;
import com.qualitygates.api.model.Product;
import com.qualitygates.api.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Implementación del servicio de productos.
 * Contiene la lógica de negocio para las operaciones CRUD.
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param productRepository repositorio de productos
     */
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Producto no encontrado con ID: " + id));
    }

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long id, Product productDetails) {
        Product product = getProductById(id);
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setQuantity(productDetails.getQuantity());
        product.setCategory(productDetails.getCategory());
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    @Override
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
}
