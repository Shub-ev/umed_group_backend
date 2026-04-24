package online.umedgroup.ug_inventory_management.controllers;

import online.umedgroup.ug_inventory_management.common.dtos.UnitNameDTO;
import online.umedgroup.ug_inventory_management.services.UnitNameServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
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

    @DeleteMapping("/delete-unit")
    public ResponseEntity<UnitNameDTO> deleteUnit(@RequestBody UnitNameDTO unitNameDTO) {
        log.info("Delete unit with name: {}", unitNameDTO.getUnitName());
        unitNameServices.deleteUnitName(unitNameDTO);
        return ResponseEntity.ok(unitNameDTO);
    }

    @GetMapping("/get-units")
    public ResponseEntity<List<UnitNameDTO>> getUnits() {
        log.info("Fetching all units");
        List<UnitNameDTO> unitNames = unitNameServices.getUnits();
        log.info("Unit Names fetched successfully!");
        return ResponseEntity.ok(unitNames);
    }
}
