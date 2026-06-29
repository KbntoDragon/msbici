package com.ventas.ms2.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ventas.ms2.DTO.TipoPagoDTO;
import com.ventas.ms2.model.TipoPago;
import com.ventas.ms2.service.TipoPagoService;

@RestController
@RequestMapping("/api/v1/tipoPago")
public class TipoPagoController {
    @Autowired
    private TipoPagoService tipoPagoService;

    @GetMapping
    public ResponseEntity<List<TipoPagoDTO>> listarTipoPago(){
        List<TipoPagoDTO> tipoPagos = tipoPagoService.obtenerTipoPago();
        if (tipoPagos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(tipoPagos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoPagoDTO> buscarServicioPorId(@PathVariable Integer id){
        try {
            TipoPagoDTO tipoPago = tipoPagoService.obtenerTipoPagoDTOPorId(id);
            return new ResponseEntity<>(tipoPago, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<TipoPago> guardarTipoPago(@RequestBody TipoPago tipoPago){
        try {
            TipoPago tipoPago2 = tipoPagoService.guardarTipoPago(tipoPago);
            return new ResponseEntity<>(tipoPago2, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
