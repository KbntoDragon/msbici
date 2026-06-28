package com.bicicleta.ms3.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.bicicleta.ms3.DTO.BicicletaDTO;
import com.bicicleta.ms3.model.Bicicleta;
import com.bicicleta.ms3.service.BicicletaService;

@ExtendWith(MockitoExtension.class)
class BicicletaControllerTest {

    @Mock
    private BicicletaService bicicletaService;

    @InjectMocks
    private BicicletaController bicicletaController;

    private BicicletaDTO dto() {
        BicicletaDTO d = new BicicletaDTO();
        d.setId(1);
        d.setMaterial("Aluminio");
        return d;
    }

    @Test
    void listar_conDatos_200() {
        when(bicicletaService.obtenerTodas()).thenReturn(List.of(dto()));
        assertThat(bicicletaController.listar().getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void listar_vacio_204() {
        when(bicicletaService.obtenerTodas()).thenReturn(List.of());
        assertThat(bicicletaController.listar().getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void buscarPorId_200() {
        when(bicicletaService.buscarPorId(1)).thenReturn(dto());
        assertThat(bicicletaController.buscarPorId(1).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void guardar_201() {
        Bicicleta b = new Bicicleta();
        when(bicicletaService.guardar(b)).thenReturn(b);
        assertThat(bicicletaController.guardarBicicleta(b).getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void actualizar_201() {
        Bicicleta b = new Bicicleta();
        when(bicicletaService.actualizarBicicleta(1, b)).thenReturn(b);
        assertThat(bicicletaController.actualizarBicicleta(b, 1).getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void eliminar_200() {
        when(bicicletaService.eliminar(1)).thenReturn("Bicicleta eliminada con exito");
        assertThat(bicicletaController.eliminarBicicleta(1).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void buscarPorCliente_vacio_204() {
        when(bicicletaService.buscarPorCliente(5)).thenReturn(List.of());
        assertThat(bicicletaController.buscarPorCliente(5).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void buscarPorModelo_200() {
        when(bicicletaService.buscarPorModelo(2)).thenReturn(List.of(dto()));
        assertThat(bicicletaController.buscarPorModelo(2).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void buscarPorMaterial_200() {
        when(bicicletaService.buscarPorMaterial("Aluminio")).thenReturn(List.of(dto()));
        assertThat(bicicletaController.buscarPorMaterial("Aluminio").getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void buscarPorMarca_200() {
        when(bicicletaService.buscarPorMarca(3)).thenReturn(List.of(dto()));
        assertThat(bicicletaController.buscarPorMarca(3).getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
