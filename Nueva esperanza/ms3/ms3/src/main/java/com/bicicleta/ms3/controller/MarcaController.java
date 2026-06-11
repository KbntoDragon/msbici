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

import com.bicicleta.ms3.DTO.MarcaDTO;
import com.bicicleta.ms3.model.Marca;
import com.bicicleta.ms3.service.MarcaService;


@RestController
@RequestMapping("/api/v1/marcas")
public class MarcaController {

    @Autowired
    private MarcaService marcaService;

    @GetMapping
    public ResponseEntity<List<MarcaDTO>> todasLasMarcas() {
        List<MarcaDTO> marcas = marcaService.obtenerMarcas();
        if (marcas.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(marcas, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarcaDTO> buscarPorId(@PathVariable Integer id){
        MarcaDTO marcas = marcaService.buscarPorId(id);
        return new ResponseEntity<>(marcas, HttpStatus.OK); 
    }

    @GetMapping ("nombre/{nombre}")
    public ResponseEntity <Marca> buscarPorNombre(@PathVariable String nombre) {
        Marca marca = marcaService.buscarPorNombre(nombre);
        if (marca == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(marca, HttpStatus.OK);
    }  

    @PostMapping
    public ResponseEntity<Marca> agregarMarca(@RequestBody Marca marca ) {
        try {
            Marca guardado = marcaService.guardarMarca(marca);
            return new ResponseEntity<>(guardado, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping ("/{id}")
    public ResponseEntity<Marca> editarMarca(@PathVariable Integer id, @RequestBody Marca marca) {
        try {
            Marca editado = marcaService.guardarMarca(marca);
            return new ResponseEntity<>(editado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping ("/{id}")
    public ResponseEntity<Marca> actualizarMarca(@PathVariable Integer id, @RequestBody Marca marca) {
        try {
            Marca actualizado = marcaService.actualizarMarca(id, marca);
            return new ResponseEntity<>(actualizado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<String> eliminarMarca(@PathVariable Integer id) {
        String resultado = marcaService.eliminar(id);
        
        if (resultado.contains("Eliminado exitosamente!")) {
            return new ResponseEntity<>(resultado, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(resultado, HttpStatus.NOT_FOUND);
        }
    }
}
