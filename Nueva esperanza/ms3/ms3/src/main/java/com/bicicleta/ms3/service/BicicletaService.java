package com.bicicleta.ms3.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bicicleta.ms3.DTO.BicicletaDTO;
import com.bicicleta.ms3.model.Bicicleta;
import com.bicicleta.ms3.model.Marca;
import com.bicicleta.ms3.repository.BicicletaRepository;


@Service
public class BicicletaService {

    @Autowired
    private BicicletaRepository bicicletaRepository;

    public List<BicicletaDTO> obtenerTodas() {
        List<BicicletaDTO> listaDTOs = new ArrayList<>();
        List<Bicicleta> bicicletasReales = bicicletaRepository.findAll();
        for (Bicicleta bici : bicicletasReales) {
            listaDTOs.add(convertirADTO(bici));
        }
        return listaDTOs;
    }

    public BicicletaDTO buscarPorId(Integer id) {
        Bicicleta bici = bicicletaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Bicicleta no encontrada en los archivos"));
        return convertirADTO(bici);
    }

    public Bicicleta guardar(Bicicleta nuevaBicicleta) {
        // Regla de negocio: el material es obligatorio
        if (nuevaBicicleta.getMaterial() == null || nuevaBicicleta.getMaterial().isBlank()) {
            throw new IllegalArgumentException("El material de la bicicleta es obligatorio");
        }
        return bicicletaRepository.save(nuevaBicicleta);
    }

    public Bicicleta actualizarBicicleta(Integer id, Bicicleta bicicleta){
        Bicicleta bici = bicicletaRepository.findById(id).orElseThrow(() -> new RuntimeException("No se puede actualizar, la bicicleta no existe con los registros"));
        if(bicicleta.getMaterial() != null){
            bici.setMaterial(bicicleta.getMaterial());
        }
        return bicicletaRepository.save(bici);
    }

    public String eliminar(Integer id){
        try{
            Bicicleta bicicleta = bicicletaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("No se puede eliminar, la bicicleta no existe con el id:" + id));
            bicicletaRepository.delete(bicicleta);
            return "Bicicleta eliminada con exito";
        } catch (RuntimeException e){
            return e.getMessage();
        }
    }

    // Búsquedas filtradas (usadas por el controller)
    public List<BicicletaDTO> buscarPorCliente(Integer clienteId) {
        return bicicletaRepository.findByClienteId(clienteId).stream()
                .map(this::convertirADTO)
                .toList();
    }

    public List<BicicletaDTO> buscarPorModelo(Integer modeloId) {
        return bicicletaRepository.findByModeloId(modeloId).stream()
                .map(this::convertirADTO)
                .toList();
    }

    public List<BicicletaDTO> buscarPorMaterial(String material) {
        return bicicletaRepository.findByMaterial(material).stream()
                .map(this::convertirADTO)
                .toList();
    }

    public List<BicicletaDTO> buscarPorMarca(Integer marcaId) {
        return bicicletaRepository.findByMarcasId(marcaId).stream()
                .map(this::convertirADTO)
                .toList();
    }

    private BicicletaDTO convertirADTO(Bicicleta bici) {
        BicicletaDTO dto = new BicicletaDTO();
        dto.setId(bici.getId());
        dto.setMaterial(bici.getMaterial());
        if (bici.getModelo() != null) {
            dto.setModeloNombre(bici.getModelo().getNombreModelo());
        }
        if (bici.getMarcas() != null && !bici.getMarcas().isEmpty()) {
            dto.setMarcas(bici.getMarcas().stream()
                    .map(Marca::getNombre)
                    .collect(Collectors.joining(", ")));
        }
        if (bici.getClienteId() != null) {
            dto.setClienteNombre("cliente#" + bici.getClienteId());
        }
        return dto;
    }
}
