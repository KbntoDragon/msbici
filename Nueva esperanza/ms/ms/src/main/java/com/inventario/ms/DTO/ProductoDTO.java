package com.inventario.ms.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoDTO {
    private Integer id;
    private String nombreProducto;
    private Double precio;
    private Integer stock;
    private String codigoBarras;
    private List<Integer> boletas;

}