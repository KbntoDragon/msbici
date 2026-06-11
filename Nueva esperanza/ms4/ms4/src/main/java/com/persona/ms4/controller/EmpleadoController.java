package com.persona.ms4.controller;

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

import com.persona.ms4.DTO.EmpleadoDTO;
import com.persona.ms4.modelo.Empleado;
import com.persona.ms4.service.EmpleadoService;

import jakarta.validation.Valid;




@RestController
@RequestMapping("/api/v1/empleados")
public class EmpleadoController {
    @Autowired
    private EmpleadoService empleadoService;

    @GetMapping
    public ResponseEntity<List<EmpleadoDTO>> todos() {
        List<EmpleadoDTO> lista = empleadoService.findAll();
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> porId(@PathVariable Integer id) {
        try {
            EmpleadoDTO dto = empleadoService.buscarPorId(id);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping ("nombres/{nombres}")
    public ResponseEntity<List<Empleado>> buscarPorNombres(@PathVariable String nombres) {
        List<Empleado> empleados = empleadoService.buscarPorNombre(nombres);
        if (empleados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(empleados, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> registrar(@Valid @RequestBody Empleado empleado) {
        try {
            EmpleadoDTO dto = empleadoService.guardarEmpleado(empleado);
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error en la transmision de datos", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping ("/{id}")
    public ResponseEntity<Empleado> actualizarEmpleado(@PathVariable Integer id, @RequestBody Empleado empleado) {
        try {
            Empleado actualizado = empleadoService.actualizarEmpleado(id, empleado);
            return new ResponseEntity<>(actualizado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<String> eliminarEmpleado(@PathVariable Integer id) {
        String resultado = empleadoService.eliminarPorId(id);
        
        if (resultado.contains("Eliminado exitosamente!")) {
            return new ResponseEntity<>(resultado, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(resultado, HttpStatus.NOT_FOUND);
        }
    }
}
