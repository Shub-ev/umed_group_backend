package com.ug.ug_inventory_management.controllers;

import com.ug.ug_inventory_management.common.dtos.UnitNameDTO;
import com.ug.ug_inventory_management.services.UnitNameServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/unit-name")
public class UnitNameController {

    private final UnitNameServices unitNameServices;
    private final Logger log = LoggerFactory.getLogger(UnitNameController.class);

    public UnitNameController(UnitNameServices unitNameServices) {
        this.unitNameServices = unitNameServices;
    }

    @PostMapping("/add-unit")
    public ResponseEntity<UnitNameDTO> addUnit(@RequestBody UnitNameDTO unitNameDTO) {
        log.info("Creating unit with name: {}", unitNameDTO.getUnitName());
        log.info("UnitNameDTO: {}", unitNameDTO);
        UnitNameDTO unitNameRes = unitNameServices.addUnitName(unitNameDTO);
        log.info("Created unit: {}", unitNameDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(unitNameRes);
    }
}
