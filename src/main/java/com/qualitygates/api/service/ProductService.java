package com.qualitygates.api.service;

import com.qualitygates.api.model.Product;
import java.util.List;

/**
 * Interfaz del servicio de productos.
 * Define las operaciones de negocio disponibles.
 */
public interface ProductService {

    /**
     * Obtiene todos los productos.
     *
     * @return lista de todos los productos
     */
    List<Product> getAllProducts();

    /**
     * Obtiene un producto por su ID.
     *
     * @param id identificador del producto
     * @return el producto encontrado
     */
    Product getProductById(Long id);

    /**
     * Crea un nuevo producto.
     *
     * @param product datos del producto a crear
     * @return el producto creado con su ID asignado
     */
    Product createProduct(Product product);

    /**
     * Actualiza un producto existente.
     *
     * @param id identificador del producto a actualizar
     * @param product datos actualizados del producto
     * @return el producto actualizado
     */
    Product updateProduct(Long id, Product product);

    /**
     * Elimina un producto por su ID.
     *
     * @param id identificador del producto a eliminar
     */
    void deleteProduct(Long id);

    /**
     * Obtiene productos por categoría.
     *
     * @param category la categoría a consultar
     * @return lista de productos de esa categoría
     */
    List<Product> getProductsByCategory(String category);

    /**
     * Busca productos por nombre.
     *
     * @param name texto a buscar en el nombre
     * @return lista de productos que coinciden
     */
    List<Product> searchProductsByName(String name);
}
