package com.bicicleta.ms3.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bicicleta.ms3.DTO.BicicletaDTO;
import com.bicicleta.ms3.model.Bicicleta;
import com.bicicleta.ms3.model.Marca;
import com.bicicleta.ms3.model.Modelo;
import com.bicicleta.ms3.repository.BicicletaRepository;

@ExtendWith(MockitoExtension.class)
class BicicletaServiceTest {

    @Mock
    private BicicletaRepository bicicletaRepository;

    @InjectMocks
    private BicicletaService bicicletaService;

    private Bicicleta nuevaBicicleta(Integer id, String material) {
        Bicicleta b = new Bicicleta();
        b.setId(id);
        b.setMaterial(material);
        return b;
    }

    @Test
    void obtenerTodas_devuelveDTOs() {
        when(bicicletaRepository.findAll()).thenReturn(List.of(nuevaBicicleta(1, "Aluminio")));
        List<BicicletaDTO> resultado = bicicletaService.obtenerTodas();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getMaterial()).isEqualTo("Aluminio");
    }

    @Test
    void convertirADTO_conRelaciones_llenaCampos() {
        Modelo m = new Modelo();
        m.setNombreModelo("Marlin");
        Marca ma = new Marca();
        ma.setNombre("Trek");
        Bicicleta b = nuevaBicicleta(1, "Aluminio");
        b.setModelo(m);
        b.setMarcas(List.of(ma));
        b.setClienteId(7);
        when(bicicletaRepository.findAll()).thenReturn(List.of(b));
        BicicletaDTO dto = bicicletaService.obtenerTodas().get(0);
        assertThat(dto.getModeloNombre()).isEqualTo("Marlin");
        assertThat(dto.getMarcas()).contains("Trek");
        assertThat(dto.getClienteNombre()).contains("7");
    }

    @Test
    void buscarPorId_noExiste_lanza() {
        when(bicicletaRepository.findById(9)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bicicletaService.buscarPorId(9))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void guardar_materialVacio_lanza() {
        Bicicleta b = nuevaBicicleta(null, "  ");
        assertThatThrownBy(() -> bicicletaService.guardar(b))
                .isInstanceOf(IllegalArgumentException.class);
        verify(bicicletaRepository, never()).save(any());
    }

    @Test
    void guardar_valida_persiste() {
        Bicicleta b = nuevaBicicleta(null, "Carbono");
        when(bicicletaRepository.save(b)).thenReturn(nuevaBicicleta(1, "Carbono"));
        assertThat(bicicletaService.guardar(b).getId()).isEqualTo(1);
    }

    @Test
    void actualizarBicicleta_cambiaMaterial() {
        when(bicicletaRepository.findById(1)).thenReturn(Optional.of(nuevaBicicleta(1, "Viejo")));
        when(bicicletaRepository.save(any(Bicicleta.class))).thenAnswer(inv -> inv.getArgument(0));
        Bicicleta resultado = bicicletaService.actualizarBicicleta(1, nuevaBicicleta(null, "Carbono"));
        assertThat(resultado.getMaterial()).isEqualTo("Carbono");
    }

    @Test
    void eliminar_existe_borra() {
        when(bicicletaRepository.findById(1)).thenReturn(Optional.of(nuevaBicicleta(1, "Acero")));
        assertThat(bicicletaService.eliminar(1)).contains("exito");
        verify(bicicletaRepository).delete(any());
    }

    @Test
    void eliminar_noExiste_devuelveMensaje() {
        when(bicicletaRepository.findById(9)).thenReturn(Optional.empty());
        assertThat(bicicletaService.eliminar(9)).contains("no existe");
    }

    @Test
    void busquedas_deleganEnRepositorio() {
        when(bicicletaRepository.findByClienteId(7)).thenReturn(List.of(nuevaBicicleta(1, "A")));
        when(bicicletaRepository.findByModeloId(2)).thenReturn(List.of(nuevaBicicleta(1, "A")));
        when(bicicletaRepository.findByMaterial("A")).thenReturn(List.of(nuevaBicicleta(1, "A")));
        when(bicicletaRepository.findByMarcasId(3)).thenReturn(List.of(nuevaBicicleta(1, "A")));
        assertThat(bicicletaService.buscarPorCliente(7)).hasSize(1);
        assertThat(bicicletaService.buscarPorModelo(2)).hasSize(1);
        assertThat(bicicletaService.buscarPorMaterial("A")).hasSize(1);
        assertThat(bicicletaService.buscarPorMarca(3)).hasSize(1);
    }
}
