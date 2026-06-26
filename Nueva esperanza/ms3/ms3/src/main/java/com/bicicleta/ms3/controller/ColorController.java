package com.bicicleta.ms3.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bicicleta.ms3.DTO.ColorDTO;
import com.bicicleta.ms3.model.Color;
import com.bicicleta.ms3.service.ColorService;

@RestController
@RequestMapping("/api/v1/colores")
public class ColorController {
    @Autowired
    private ColorService colorService;

    @GetMapping
    public ResponseEntity<List<ColorDTO>> todosLosColores() {
        List<ColorDTO> colores = colorService.obtenerColores();
        if (colores.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(colores, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ColorDTO> buscarPorId(@PathVariable Integer id){
        ColorDTO colores = colorService.buscarPorId(id);
        return new ResponseEntity<>(colores, HttpStatus.OK); 
    }

    @PostMapping
    public ResponseEntity<ColorDTO> guardarBoleta(@RequestBody Color color){
        // 1. Guardamos la boleta en la base de datos
        Color nuevoColor = colorService.guardarColor(color);

        // 2. La convertimos a DTO antes de enviarla a Postman
        ColorDTO colorDTO = colorService.convertirADTO(nuevoColor);

        // 3. Retornamos el DTO
        return new ResponseEntity<>(colorDTO, HttpStatus.CREATED);
    }

    @PatchMapping ("/{id}")
    public ResponseEntity<Color> editarColor(@PathVariable Integer id, @RequestBody Color color) {
        try {
            Color editado = colorService.guardarColor(color);
            return new ResponseEntity<>(editado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping ("/{id}")
    public ResponseEntity<Color> actualizarColor(@PathVariable Integer id, @RequestBody Color color) {
        try {
            Color actualizado = colorService.actualizarColor(id, color);
            return new ResponseEntity<>(actualizado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<String> eliminarColor(@PathVariable Integer id) {
        String resultado = colorService.eliminar(id);
        
        if (resultado.contains("exito")) {
            return new ResponseEntity<>(resultado, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(resultado, HttpStatus.NOT_FOUND);
        }
    }
}
