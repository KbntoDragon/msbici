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

import com.bicicleta.ms3.DTO.MarcaDTO;
import com.bicicleta.ms3.model.Bicicleta;
import com.bicicleta.ms3.model.Marca;
import com.bicicleta.ms3.repository.MarcaRepository;

@ExtendWith(MockitoExtension.class)
class MarcaServiceTest {

    @Mock
    private MarcaRepository marcaRepository;

    @InjectMocks
    private MarcaService marcaService;

    private Marca nuevaMarca(Integer id, String nombre) {
        Marca m = new Marca();
        m.setId(id);
        m.setNombre(nombre);
        return m;
    }

    @Test
    void obtenerMarcas_devuelveDTOs() {
        when(marcaRepository.findAll()).thenReturn(List.of(nuevaMarca(1, "Trek")));
        List<MarcaDTO> resultado = marcaService.obtenerMarcas();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Trek");
    }

    @Test
    void convertirADTO_conBicicletas_llenaLista() {
        Marca m = nuevaMarca(1, "Trek");
        Bicicleta b = new Bicicleta();
        b.setId(10);
        m.setBicicletas(List.of(b));
        when(marcaRepository.findAll()).thenReturn(List.of(m));
        MarcaDTO dto = marcaService.obtenerMarcas().get(0);
        assertThat(dto.getBicicletas()).containsExactly(10);
    }

    @Test
    void buscarPorId_noExiste_lanza() {
        when(marcaRepository.findById(4)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> marcaService.buscarPorId(4))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void buscarPorNombre_noExiste_devuelveNull() {
        when(marcaRepository.findByNombre("Giant")).thenReturn(Optional.empty());
        assertThat(marcaService.buscarPorNombre("Giant")).isNull();
    }

    @Test
    void guardarMarca_persiste() {
        Marca m = nuevaMarca(null, "Specialized");
        when(marcaRepository.save(m)).thenReturn(nuevaMarca(1, "Specialized"));
        assertThat(marcaService.guardarMarca(m).getId()).isEqualTo(1);
    }

    @Test
    void actualizarMarca_existente_actualizaNombre() {
        when(marcaRepository.findById(1)).thenReturn(Optional.of(nuevaMarca(1, "Viejo")));
        when(marcaRepository.save(any(Marca.class))).thenAnswer(inv -> inv.getArgument(0));
        assertThat(marcaService.actualizarMarca(1, nuevaMarca(null, "Nuevo")).getNombre()).isEqualTo("Nuevo");
    }

    @Test
    void eliminar_existe_borra() {
        when(marcaRepository.findById(1)).thenReturn(Optional.of(nuevaMarca(1, "Trek")));
        assertThat(marcaService.eliminar(1)).contains("exito");
        verify(marcaRepository).delete(any());
    }

    @Test
    void eliminar_noExiste_devuelveMensaje() {
        when(marcaRepository.findById(9)).thenReturn(Optional.empty());
        assertThat(marcaService.eliminar(9)).contains("no existe");
    }
}
