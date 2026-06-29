package com.bicicleta.ms3.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bicicleta.ms3.DTO.BicicletaDTO;
import com.bicicleta.ms3.model.Bicicleta;
import com.bicicleta.ms3.service.BicicletaService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/bicicletas")
@Tag(name = "Bicicletas", description = "Gestión de bicicletas, búsqueda por cliente, modelo, marca y material")
public class BicicletaController {
    
    @Autowired
    private BicicletaService  bicicletaService;

    @GetMapping
    public ResponseEntity<List<BicicletaDTO>> listar(){
        List<BicicletaDTO> bicicletas = bicicletaService.obtenerTodas();
        if(bicicletas.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(bicicletas, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BicicletaDTO> buscarPorId(@PathVariable Integer id){
        return new ResponseEntity<>(bicicletaService.buscarPorId(id), HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<Bicicleta> guardarBicicleta(@RequestBody Bicicleta bicicleta){
        Bicicleta nuevaBicicleta = bicicletaService.guardar(bicicleta);
        return new ResponseEntity<>(nuevaBicicleta, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bicicleta> actualizarBicicleta(@RequestBody Bicicleta bicicleta, @PathVariable Integer id){
        Bicicleta bicicletaActualizada = bicicletaService.actualizarBicicleta(id, bicicleta);
        return new ResponseEntity<>(bicicletaActualizada, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarBicicleta(@PathVariable Integer id){
        String mensajeAlerta = bicicletaService.eliminar(id);
        return new ResponseEntity<>(mensajeAlerta, HttpStatus.OK);
    }

    //ID de Cliente
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<BicicletaDTO>> buscarPorCliente(@PathVariable Integer clienteId) {
        List<BicicletaDTO> bicicletas = bicicletaService.buscarPorCliente(clienteId);
        if (bicicletas.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(bicicletas, HttpStatus.OK);
    }

    //ID de Modelo
    @GetMapping("/modelo/{modeloId}")
    public ResponseEntity<List<BicicletaDTO>> buscarPorModelo(@PathVariable Integer modeloId) {
        List<BicicletaDTO> bicicletas = bicicletaService.buscarPorModelo(modeloId);
        if (bicicletas.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(bicicletas, HttpStatus.OK);
    }

    //Buscar por Material
    @GetMapping("/material/{material}")
    public ResponseEntity<List<BicicletaDTO>> buscarPorMaterial(@PathVariable String material) {
        List<BicicletaDTO> bicicletas = bicicletaService.buscarPorMaterial(material);
        if (bicicletas.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(bicicletas, HttpStatus.OK);
    }

    //ID de Marca
    @GetMapping("/marca/{marcaId}")
    public ResponseEntity<List<BicicletaDTO>> buscarPorMarca(@PathVariable Integer marcaId) {
        List<BicicletaDTO> bicicletas = bicicletaService.buscarPorMarca(marcaId);
        if (bicicletas.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(bicicletas, HttpStatus.OK);
    }
}
