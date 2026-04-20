package com.ug.ug_inventory_management.services;

import com.ug.ug_inventory_management.common.dtos.UnitNameDTO;
import com.ug.ug_inventory_management.common.exceptions.IllegalArgumentException;
import com.ug.ug_inventory_management.models.UnitName;
import com.ug.ug_inventory_management.repositories.UnitNameRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnitNameServices {

    private final UnitNameRepository unitNameRepository;

    public UnitNameServices(UnitNameRepository unitNameRepository) {
        this.unitNameRepository = unitNameRepository;
    }

    public UnitNameDTO addUnitName(UnitNameDTO unitNameDTO) {

        if(unitNameDTO.getUnitName().trim() == "") {
            throw new IllegalArgumentException("Unit Name must not be blank");
        }

        UnitName unitName = unitNameRepository.save(new UnitName(unitNameDTO.getUnitName().trim()));
        return new UnitNameDTO(unitName.getUnitName());
    }

    public UnitNameDTO convertToDTO(UnitName unitName) {
        return new UnitNameDTO(unitName.getUnitName());
    }

    public List<UnitNameDTO> getUnits() {
        List<UnitName> unitNames = unitNameRepository.findAll();

        return unitNames.stream()
                .map(this::convertToDTO)
                .toList();
    }
}
