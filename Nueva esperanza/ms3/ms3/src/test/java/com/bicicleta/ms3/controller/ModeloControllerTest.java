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

import com.bicicleta.ms3.DTO.ModeloDTO;
import com.bicicleta.ms3.model.Modelo;
import com.bicicleta.ms3.service.ModeloService;

@ExtendWith(MockitoExtension.class)
class ModeloControllerTest {

    @Mock
    private ModeloService modeloService;

    @InjectMocks
    private ModeloController modeloController;

    private ModeloDTO dto() {
        ModeloDTO d = new ModeloDTO();
        d.setId(1);
        d.setNombre("Marlin");
        return d;
    }

    @Test
    void listar_conDatos_200() {
        when(modeloService.obtenerModelos()).thenReturn(List.of(dto()));
        assertThat(modeloController.listarModelos().getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void listar_vacio_204() {
        when(modeloService.obtenerModelos()).thenReturn(List.of());
        assertThat(modeloController.listarModelos().getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void buscar_200() {
        when(modeloService.obtenerModeloPorId(1)).thenReturn(dto());
        assertThat(modeloController.buscarModelo(1).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void buscar_noExiste_404() {
        when(modeloService.obtenerModeloPorId(9)).thenThrow(new RuntimeException("no"));
        assertThat(modeloController.buscarModelo(9).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void guardar_201() {
        Modelo m = new Modelo();
        when(modeloService.guardarModelo(m)).thenReturn(m);
        assertThat(modeloController.guardarModelo(m).getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void actualizar_200() {
        Modelo m = new Modelo();
        when(modeloService.guardarModelo(m)).thenReturn(m);
        assertThat(modeloController.actualizarModelo(1, m).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void eliminar_exito_200() {
        when(modeloService.eliminarModelo(1)).thenReturn("El modelo X ha sido eliminado con exito");
        assertThat(modeloController.eliminarModelo(1).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void eliminar_noExiste_404() {
        when(modeloService.eliminarModelo(9)).thenReturn("el modelo con ID 9 no existe");
        assertThat(modeloController.eliminarModelo(9).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
