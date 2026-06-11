package com.inventario.ms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventario.ms.model.Repuesto;

@Repository
public interface RepuestoRepository extends JpaRepository<Repuesto, Integer>{  
}