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

import com.bicicleta.ms3.DTO.ColorDTO;
import com.bicicleta.ms3.model.Bicicleta;
import com.bicicleta.ms3.model.Color;
import com.bicicleta.ms3.repository.ColorRepository;

@ExtendWith(MockitoExtension.class)
class ColorServiceTest {

    @Mock
    private ColorRepository colorRepository;

    @InjectMocks
    private ColorService colorService;

    private Color nuevoColor(Integer id, String nombre) {
        Color c = new Color();
        c.setId(id);
        c.setNombre(nombre);
        return c;
    }

    @Test
    void obtenerColores_devuelveDTOs() {
        when(colorRepository.findAll()).thenReturn(List.of(nuevoColor(1, "Rojo")));
        List<ColorDTO> resultado = colorService.obtenerColores();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Rojo");
    }

    @Test
    void convertirADTO_conBicicletas_llenaLista() {
        Color c = nuevoColor(1, "Rojo");
        Bicicleta b = new Bicicleta();
        b.setId(20);
        c.setBicicletas(List.of(b));
        when(colorRepository.findAll()).thenReturn(List.of(c));
        assertThat(colorService.obtenerColores().get(0).getBicicletas()).containsExactly(20);
    }

    @Test
    void buscarPorId_noExiste_lanza() {
        when(colorRepository.findById(7)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> colorService.buscarPorId(7))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void guardarColor_persiste() {
        Color c = nuevoColor(null, "Azul");
        when(colorRepository.save(c)).thenReturn(nuevoColor(1, "Azul"));
        assertThat(colorService.guardarColor(c).getId()).isEqualTo(1);
    }

    @Test
    void actualizarColor_existente_actualiza() {
        when(colorRepository.findById(1)).thenReturn(Optional.of(nuevoColor(1, "Viejo")));
        when(colorRepository.save(any(Color.class))).thenAnswer(inv -> inv.getArgument(0));
        assertThat(colorService.actualizarColor(1, nuevoColor(null, "Nuevo")).getNombre()).isEqualTo("Nuevo");
    }

    @Test
    void eliminar_existe_borra() {
        when(colorRepository.findById(1)).thenReturn(Optional.of(nuevoColor(1, "Verde")));
        assertThat(colorService.eliminar(1)).contains("exito");
        verify(colorRepository).delete(any(Color.class));
    }

    @Test
    void eliminar_noExiste_devuelveMensaje() {
        when(colorRepository.findById(9)).thenReturn(Optional.empty());
        assertThat(colorService.eliminar(9)).contains("no existe");
    }
}
