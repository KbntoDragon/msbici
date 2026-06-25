package com.inventario.ms.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventario.ms.DTO.ProductoDTO;
import com.inventario.ms.repository.ProductoRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public BoletaDTO obtenerBoleta(Integer id){
        return webClient.get()
                .uri("/boletas/{id}", id)
                .retrieve()
                .bodyToMono(BoletaDTO.class)
                .block();
    }

    private ProductoDTO convertirADTO(com.inventario.ms.model.Producto producto) {
        ProductoDTO productoDTO = new ProductoDTO();

        productoDTO.setId(producto.getId());
        productoDTO.setNombreProducto(producto.getNombreProducto());
        productoDTO.setPrecio(producto.getPrecio());
        productoDTO.setStock(producto.getStock());
        productoDTO.setCodigoBarras(producto.getCodigoBarras());
        if (producto.getBoletas() != null){
            productoDTO.setBoleta_id(producto.getBoletaId());
        }
        return productoDTO;
    }

    public List<ProductoDTO> obtenerTodosLosProductos() {
        log.info("Obteniendo todos los productos");
        return productoRepository.findAll().stream()
                .map(this::convertirADTO)
                .toList();
    }


}
