package com.bicicleta.ms3.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bicicleta.ms3.DTO.ModeloDTO;
import com.bicicleta.ms3.model.Marca;
import com.bicicleta.ms3.model.Modelo;
import com.bicicleta.ms3.repository.ModeloRepository;

@ExtendWith(MockitoExtension.class)
class ModeloServiceTest {

    @Mock
    private ModeloRepository modeloRepository;

    @InjectMocks
    private ModeloService modeloService;

    private Modelo nuevoModelo(Integer id, String nombre) {
        Modelo m = new Modelo();
        m.setId(id);
        m.setNombreModelo(nombre);
        m.setTipoSuspension("Doble");
        m.setTallaCuadro("M");
        return m;
    }

    @Test
    void obtenerModelos_devuelveDTOs() {
        when(modeloRepository.findAll()).thenReturn(List.of(nuevoModelo(1, "Marlin")));
        List<ModeloDTO> resultado = modeloService.obtenerModelos();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Marlin");
    }

    @Test
    void convertirADTO_conMarca_llenaMarcaNombre() {
        Modelo m = nuevoModelo(1, "Marlin");
        Marca ma = new Marca();
        ma.setNombre("Trek");
        m.setMarca(ma);
        when(modeloRepository.findAll()).thenReturn(List.of(m));
        assertThat(modeloService.obtenerModelos().get(0).getMarcaNombre()).isEqualTo("Trek");
    }

    @Test
    void obtenerModeloPorId_noExiste_lanza() {
        when(modeloRepository.findById(6)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> modeloService.obtenerModeloPorId(6))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void guardarModelo_persiste() {
        Modelo m = nuevoModelo(null, "Roscoe");
        when(modeloRepository.save(m)).thenReturn(nuevoModelo(1, "Roscoe"));
        assertThat(modeloService.guardarModelo(m).getId()).isEqualTo(1);
    }

    @Test
    void actualizarModelo_existente_actualiza() {
        when(modeloRepository.findById(1)).thenReturn(Optional.of(nuevoModelo(1, "Viejo")));
        when(modeloRepository.save(any(Modelo.class))).thenAnswer(inv -> inv.getArgument(0));
        assertThat(modeloService.actualizarModelo(1, nuevoModelo(null, "Nuevo")).getNombreModelo()).isEqualTo("Nuevo");
    }

    @Test
    void eliminarModelo_existe_borra() {
        when(modeloRepository.findById(1)).thenReturn(Optional.of(nuevoModelo(1, "Marlin")));
        assertThat(modeloService.eliminarModelo(1)).contains("exito");
        verify(modeloRepository).delete(any(Modelo.class));
    }

    @Test
    void eliminarModelo_noExiste_devuelveMensajeError() {
        when(modeloRepository.findById(99)).thenReturn(Optional.empty());
        assertThat(modeloService.eliminarModelo(99)).contains("no existe");
    }
}
