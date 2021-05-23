package com.java.sahil.minio.error;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(Class entity) {
        super(entity.getSimpleName() + " ");
    }

    public EntityNotFoundException(Class entity, Object id) {
        super(entity.getSimpleName() + " with ID: " + id + " ");
    }
}
