package com.banco.agricultura.dao;

import java.util.List;
import java.util.Optional;

public interface GenericDAO<T> {
    void save(T entity);
    void update(T entity);
    void delete(T entity);
    Optional<T> findById(Integer id);
    List<T> findAll();
}