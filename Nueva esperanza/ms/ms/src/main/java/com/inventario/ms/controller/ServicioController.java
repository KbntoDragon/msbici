package com.inventario.ms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inventario.ms.DTO.ServicioDTO;
import com.inventario.ms.model.Servicio;
import com.inventario.ms.services.ServicioService;

@RestController
@RequestMapping("/api/v1/servicios")
public class ServicioController {
    @Autowired
    private ServicioService servicioService;

    @GetMapping
    public ResponseEntity<List<ServicioDTO>> listarServicios(){
        List<ServicioDTO> servicios = servicioService.obtenerServicios();
        if (servicios.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(servicios, HttpStatus.OK);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ServicioDTO>> buscarPorNombre(@RequestParam String nombre){
        List<ServicioDTO> servicios = servicioService.buscarPorNombreDTO(nombre);
        if (servicios.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(servicios, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicioDTO> buscarProductoPorId(@PathVariable Integer id){
        try {
            ServicioDTO servicio = servicioService.obtenerServicioDTOPorId(id);
            return new ResponseEntity<>(servicio, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Servicio> guardarServicio(@RequestBody Servicio servicio){
        try {
            Servicio servicio2 = servicioService.guardarServicio(servicio);
            return new ResponseEntity<>(servicio2, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarServicio(@PathVariable Integer id){
        String resultado = servicioService.eliminarServicio(id);
        if (resultado.contains("exito")) {
            return new ResponseEntity<>(resultado, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(resultado, HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Servicio> editarServicio(@PathVariable Integer id, @RequestBody Servicio servicio){
        try {
            Servicio editado = servicioService.guardarServicio(servicio);
            return new ResponseEntity<>(editado,HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }    

}
