package com.inventario.ms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventario.ms.model.Servicio;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Integer>{  
}