package com.ventas.ms2.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ventas.ms2.DTO.BoletaDTO;
import com.ventas.ms2.model.Boleta;
import com.ventas.ms2.service.BoletaService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/boletas")
@Tag(name = "Boletas", description = "Gestión de boletas de venta y su comunicación con el inventario")
public class BoletaController {

    @Autowired
    private BoletaService boletaService;

    @GetMapping
    public ResponseEntity<List<BoletaDTO>> obtenerBoletas(){ 
        List<BoletaDTO> boletas = boletaService.obtenerBoletas();
        if(boletas.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        // 2. Tienes que pasar la variable 'boletas' dentro del paréntesis
        return new ResponseEntity<>(boletas, HttpStatus.OK); 
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoletaDTO> buscarPorId(@PathVariable Integer id){
        BoletaDTO boletas = boletaService.buscarPorId(id);
        return new ResponseEntity<>(boletas, HttpStatus.OK); 
    }

    @PostMapping
    public ResponseEntity<BoletaDTO> guardarBoleta(@RequestBody Boleta boleta){
        // 1. Guardamos la boleta en la base de datos
        Boleta nuevaBoleta = boletaService.guardarBoleta(boleta);
        
        // 2. La convertimos a DTO antes de enviarla a Postman
        BoletaDTO boletaDTO = boletaService.convertirADTO(nuevaBoleta);
        
        // 3. Retornamos el DTO
        return new ResponseEntity<>(boletaDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarBoleta(@PathVariable Integer id){
        String mensajeAlerta = boletaService.eliminar(id);
        return new ResponseEntity<>(mensajeAlerta, HttpStatus.OK);
    }

    @PostMapping("/{boletaId}/productos/{productoId}")
    public Boleta agregarProducto(
            @PathVariable Integer boletaId,
            @PathVariable Integer productoId){

        return boletaService.agregarProducto(boletaId, productoId);
    }

}