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
import org.springframework.web.bind.annotation.RestController;

import com.inventario.ms.DTO.RepuestoDTO;
import com.inventario.ms.model.Repuesto;
import com.inventario.ms.services.RepuestoService;

@RestController
@RequestMapping("/api/v1/repuesto")
public class RepuestoController {
    @Autowired
    private RepuestoService repuestoService;

    @GetMapping
    public ResponseEntity<List<RepuestoDTO>> listarProductos(){
        List<RepuestoDTO> repuestos = repuestoService.obtenerRepuestos();
        if (repuestos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(repuestos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepuestoDTO> buscarProductoPorId(@PathVariable Integer id) {
        try {
            RepuestoDTO repuestos = repuestoService.obtenerRepuestoDTOPorId(id);
            return new ResponseEntity<>(repuestos, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/codigo/{codigoBarra}")
    public ResponseEntity<List<RepuestoDTO>> buscarPorCodigoBarra(@PathVariable String codigoBarra) {
        try{
            List<RepuestoDTO> repuestos = repuestoService.buscarPorCodigoBarraDTO(codigoBarra);
            return new ResponseEntity<>(repuestos,HttpStatus.OK);
        } catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Repuesto> guardarRepuesto(@RequestBody Repuesto repuesto){
        try {
            Repuesto repuesto2 = repuestoService.guardarRepuesto(repuesto);
            return new ResponseEntity<>(repuesto2,HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarProducto(@PathVariable Integer id) {
        String resultado = repuestoService.eliminarRepuesto(id);
        if (resultado.contains("exito")) {
            return new ResponseEntity<>(resultado,HttpStatus.OK);
        }else{
            return new ResponseEntity<>(resultado, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<RepuestoDTO>> buscarPorNombre(@RequestParam String nombre){
        List<RepuestoDTO> repuestos = repuestoService.buscarPorNombreDTO(nombre);
        if(repuestos.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(repuestos, HttpStatus.OK);
    }

    @GetMapping("/sin-stock")
    public ResponseEntity<List<RepuestoDTO>> sinStock() {
        List<RepuestoDTO> repuestos = repuestoService.obtenerRepuestosSinStockDTO();
        if (repuestos == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(repuestos, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Repuesto> editarRepuestos(@PathVariable Integer id, @RequestBody Repuesto repuesto){
        try {
            Repuesto editado = repuestoService.guardarRepuesto(repuesto);
            return new ResponseEntity<>(editado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
