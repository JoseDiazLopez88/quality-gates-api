package com.qualitygates.api.repository;

import com.qualitygates.api.model.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad Product.
 * Proporciona operaciones CRUD y consultas personalizadas.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Busca productos por categoría.
     *
     * @param category la categoría a buscar
     * @return lista de productos de la categoría indicada
     */
    List<Product> findByCategory(String category);

    /**
     * Busca productos cuyo nombre contenga el texto dado (case insensitive).
     *
     * @param name texto a buscar en el nombre
     * @return lista de productos que coinciden
     */
    List<Product> findByNameContainingIgnoreCase(String name);
}
