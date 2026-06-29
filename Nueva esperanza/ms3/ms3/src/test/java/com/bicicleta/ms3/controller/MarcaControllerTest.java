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

import com.bicicleta.ms3.DTO.MarcaDTO;
import com.bicicleta.ms3.model.Marca;
import com.bicicleta.ms3.service.MarcaService;

@ExtendWith(MockitoExtension.class)
class MarcaControllerTest {

    @Mock
    private MarcaService marcaService;

    @InjectMocks
    private MarcaController marcaController;

    private MarcaDTO dto() {
        MarcaDTO d = new MarcaDTO();
        d.setId(1);
        d.setNombre("Trek");
        return d;
    }

    @Test
    void todas_conDatos_200() {
        when(marcaService.obtenerMarcas()).thenReturn(List.of(dto()));
        assertThat(marcaController.todasLasMarcas().getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void todas_vacio_204() {
        when(marcaService.obtenerMarcas()).thenReturn(List.of());
        assertThat(marcaController.todasLasMarcas().getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void buscarPorId_200() {
        when(marcaService.buscarPorId(1)).thenReturn(dto());
        assertThat(marcaController.buscarPorId(1).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void buscarPorNombre_existe_200() {
        Marca m = new Marca();
        m.setNombre("Trek");
        when(marcaService.buscarPorNombre("Trek")).thenReturn(m);
        assertThat(marcaController.buscarPorNombre("Trek").getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void buscarPorNombre_noExiste_204() {
        when(marcaService.buscarPorNombre("X")).thenReturn(null);
        assertThat(marcaController.buscarPorNombre("X").getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void agregar_201() {
        Marca m = new Marca();
        when(marcaService.guardarMarca(m)).thenReturn(m);
        assertThat(marcaController.agregarMarca(m).getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void actualizar_200() {
        Marca m = new Marca();
        when(marcaService.actualizarMarca(1, m)).thenReturn(m);
        assertThat(marcaController.actualizarMarca(1, m).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void eliminar_exito_200() {
        when(marcaService.eliminar(1)).thenReturn("Marca eliminada exitosamente");
        assertThat(marcaController.eliminarMarca(1).getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
