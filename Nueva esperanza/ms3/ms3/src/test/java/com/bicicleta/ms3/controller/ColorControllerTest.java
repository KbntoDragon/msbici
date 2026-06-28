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

import com.bicicleta.ms3.DTO.ColorDTO;
import com.bicicleta.ms3.model.Color;
import com.bicicleta.ms3.service.ColorService;

@ExtendWith(MockitoExtension.class)
class ColorControllerTest {

    @Mock
    private ColorService colorService;

    @InjectMocks
    private ColorController colorController;

    private ColorDTO dto() {
        ColorDTO d = new ColorDTO();
        d.setId(1);
        d.setNombre("Rojo");
        return d;
    }

    @Test
    void todos_conDatos_200() {
        when(colorService.obtenerColores()).thenReturn(List.of(dto()));
        assertThat(colorController.todosLosColores().getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void todos_vacio_204() {
        when(colorService.obtenerColores()).thenReturn(List.of());
        assertThat(colorController.todosLosColores().getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void buscarPorId_200() {
        when(colorService.buscarPorId(1)).thenReturn(dto());
        assertThat(colorController.buscarPorId(1).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void guardar_201() {
        Color c = new Color();
        when(colorService.guardarColor(c)).thenReturn(c);
        when(colorService.convertirADTO(c)).thenReturn(dto());
        assertThat(colorController.guardarBoleta(c).getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void editar_200() {
        Color c = new Color();
        when(colorService.guardarColor(c)).thenReturn(c);
        assertThat(colorController.editarColor(1, c).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void actualizar_200() {
        Color c = new Color();
        when(colorService.actualizarColor(1, c)).thenReturn(c);
        assertThat(colorController.actualizarColor(1, c).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void eliminar_exito_200() {
        when(colorService.eliminar(1)).thenReturn("Color eliminado exitosamente");
        assertThat(colorController.eliminarColor(1).getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
