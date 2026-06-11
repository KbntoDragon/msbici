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


@RestController
@RequestMapping("/api/v1/modelos")
public class ModeloController {
@Autowired
    private ModeloService modeloService;

    // LISTAR TODOS
    @GetMapping
    public ResponseEntity<List<ModeloDTO>> listarModelos() {
        List<ModeloDTO> modelos = modeloService.obtenerModelos();
        if (modelos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(modelos, HttpStatus.OK);
    }

    // BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<ModeloDTO> buscarModelo(@PathVariable Integer id) {
        try {
            ModeloDTO modelo = modeloService.obtenerModeloPorId(id);
            return new ResponseEntity<>(modelo, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GUARDAR
    @PostMapping
    public ResponseEntity<Modelo> guardarModelo(@RequestBody Modelo modelo) {
        try {
            Modelo save = modeloService.guardarModelo(modelo);
            return new ResponseEntity<>(save, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // ACTUALIZAR
    @PutMapping("/{id}")
    public ResponseEntity<Modelo> actualizarModelo(@PathVariable Integer id,@RequestBody Modelo modelo){
        try {
            Modelo editado = modeloService.guardarModelo(modelo);
            return new ResponseEntity<>(editado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // ELIMINAR
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarModelo(@PathVariable Integer id) {
        String resultado = modeloService.eliminarModelo(id);

        if (resultado.contains("exito")) {
            return new ResponseEntity<>(resultado, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(resultado, HttpStatus.NOT_FOUND);
        }
    }
}
