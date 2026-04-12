package com.qualitygates.api.exception;

/**
 * Excepción lanzada cuando un recurso no se encuentra en la base de datos.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructor con mensaje personalizado.
     *
     * @param message descripción del error
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
