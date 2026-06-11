package com.bicicleta.ms3.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bicicleta.ms3.DTO.BicicletaDTO;
import com.bicicleta.ms3.model.Bicicleta;
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
    
    private BicicletaDTO convertirADTO(Bicicleta bici) {
        BicicletaDTO dto = new BicicletaDTO();
        dto.setId(bici.getId());
        dto.setMaterial(bici.getMaterial());
        return dto;
    }
}
