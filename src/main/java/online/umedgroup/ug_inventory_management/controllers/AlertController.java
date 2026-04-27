package online.umedgroup.ug_inventory_management.controllers;

import online.umedgroup.ug_inventory_management.common.dtos.StockAlertDTO;
import online.umedgroup.ug_inventory_management.security.CustomEmployeeDetails;
import online.umedgroup.ug_inventory_management.services.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/inventory")
public class AlertController {

    private final InventoryService inventoryService;

    public AlertController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/alerts/low-stock")
    public ResponseEntity<?> getLowStockAlerts(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        if (isAdmin) {
            List<StockAlertDTO> alerts = inventoryService.getAllLowStockAlerts();
            return ResponseEntity.ok(alerts);
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomEmployeeDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        CustomEmployeeDetails employeeDetails = (CustomEmployeeDetails) principal;
        String unitName = employeeDetails.getEmployee().getUnitName();

        List<StockAlertDTO> alerts = inventoryService.getLowStockAlertsForUnit(unitName);
        return ResponseEntity.ok(alerts);
    }
}