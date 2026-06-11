package com.inventario.ms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventario.ms.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer>{  
}