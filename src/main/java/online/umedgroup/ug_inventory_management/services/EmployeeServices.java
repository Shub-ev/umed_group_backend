package online.umedgroup.ug_inventory_management.services;

import online.umedgroup.ug_inventory_management.common.dtos.Employee.*;
import online.umedgroup.ug_inventory_management.common.dtos.Employee.*;
import online.umedgroup.ug_inventory_management.common.exceptions.EmployeeNotFoundException;
import online.umedgroup.ug_inventory_management.common.exceptions.IllegalArgumentException;
import online.umedgroup.ug_inventory_management.common.exceptions.WrongPasswordException;
import online.umedgroup.ug_inventory_management.models.Employee;
import online.umedgroup.ug_inventory_management.repositories.EmployeeRepository;
import online.umedgroup.ug_inventory_management.repositories.UnitNameRepository;
import online.umedgroup.ug_inventory_management.security.JwtService;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmployeeServices {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UnitNameRepository unitNameRepository;
    private static final Logger log = LoggerFactory.getLogger(EmployeeServices.class);

    public EmployeeServices(
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            UnitNameRepository unitNameRepository
    ) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.unitNameRepository = unitNameRepository;
    }

    // ================= CREATE EMPLOYEE =================
    public EmployeeResponseDTO createEmployee(@NonNull CreateEmployeeDTO dto) {

        if (dto.getEId() == null) {
            throw new IllegalArgumentException("Employee Id cannot be blank");
        }

        String unitName = dto.getUnitName();
        if (unitName == null || unitName.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee unit name cannot be blank");
        }
        unitName = unitName.trim();

        if (!unitNameRepository.existsByUnitName(unitName)) {
            throw new IllegalArgumentException("Unit name does not exist");
        }

        String password = dto.getPassword();
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee password cannot be blank");
        }
        password = password.trim();

        if (password.length() < 5 || password.length() > 14) {
            throw new IllegalArgumentException("Password must be 5 to 14 characters");
        }

        if (employeeRepository.findByeId(dto.getEId()).isPresent()) {
            throw new IllegalArgumentException("Employee already exists with id: " + dto.getEId());
        }

        Employee employee = new Employee(
                dto.getEId(),
                unitName,
                passwordEncoder.encode(password),
                LocalDate.now()
        );

        Employee saved = employeeRepository.save(employee);

        return new EmployeeResponseDTO(
                saved.getId(),
                saved.getEId(),
                saved.getUnitName(),
                saved.getAllocation()
        );
    }

    // ================= LOGIN =================
    public EmployeeLoginResponseDTO loginEmployee(@NonNull LoginEmployeeDTO dto) {

        if (dto.getEId() == null) {
            throw new IllegalArgumentException("Employee Id cannot be blank");
        }

        String password = dto.getPassword();
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee password cannot be blank");
        }

        Employee found = employeeRepository.findByeId(dto.getEId())
                .orElseThrow(() ->
                        new EmployeeNotFoundException("Employee not found with eid: " + dto.getEId())
                );

        if (!passwordEncoder.matches(password.trim(), found.getPassword())) {
            throw new WrongPasswordException("Invalid credentials");
        }

        String token = jwtService.generateToken(
                dto.getEId().toString(),
                "ROLE_EMPLOYEE"
        );

        return new EmployeeLoginResponseDTO(
                found.getId(),
                found.getEId(),
                found.getUnitName(),
                found.getAllocation(),
                token
        );
    }

    // ================= UPDATE UNIT NAME =================
    public EmployeeResponseDTO updateEmployeeUnitName(@NonNull EmployeeUnitNameUpdateDTO dto) {

        if (dto.geteId() == null) {
            throw new IllegalArgumentException("Employee Id cannot be null");
        }

        String password = dto.getPassword();
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        String newUnit = dto.getNewUnitName();
        if (newUnit == null || newUnit.trim().isEmpty()) {
            throw new IllegalArgumentException("New unit name cannot be empty");
        }
        newUnit = newUnit.trim();

        if (!unitNameRepository.existsByUnitName(newUnit)) {
            throw new IllegalArgumentException("Unit name does not exist");
        }

        Employee found = employeeRepository.findByeId(dto.geteId())
                .orElseThrow(() ->
                        new EmployeeNotFoundException("Employee not found with eid: " + dto.geteId())
                );

        if (!passwordEncoder.matches(password.trim(), found.getPassword())) {
            throw new WrongPasswordException("Invalid password");
        }

        found.setUnitName(newUnit);

        Employee saved = employeeRepository.save(found);

        return new EmployeeResponseDTO(
                saved.getId(),
                saved.getEId(),
                saved.getUnitName(),
                saved.getAllocation()
        );
    }

    // ================= UPDATE PASSWORD =================
    public EmployeeResponseDTO updateEmployeePassword(@NonNull EmployeePasswordUpdateDTO dto) {

        if (dto.getEId() == null) {
            throw new IllegalArgumentException("Employee Id cannot be blank");
        }

        String oldPassword = dto.getPasswordPre();
        String newPassword = dto.getPasswordNew();

        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Old password cannot be blank");
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("New password cannot be blank");
        }

        if (newPassword.equals(oldPassword)) {
            throw new IllegalArgumentException("New password cannot be same as old");
        }

        if (newPassword.length() < 5 || newPassword.length() > 14) {
            throw new IllegalArgumentException("Password must be 5 to 14 characters");
        }

        Employee found = employeeRepository.findByeId(dto.getEId())
                .orElseThrow(() ->
                        new EmployeeNotFoundException("Employee not found with eid: " + dto.getEId())
                );

        if (!passwordEncoder.matches(oldPassword.trim(), found.getPassword())) {
            throw new WrongPasswordException("Invalid old password");
        }

        found.setPassword(passwordEncoder.encode(newPassword));

        Employee saved = employeeRepository.save(found);

        return new EmployeeResponseDTO(
                saved.getId(),
                saved.getEId(),
                saved.getUnitName(),
                saved.getAllocation()
        );
    }

    // ================= DELETE SELF =================
    public EmployeeResponseDTO deleteSelfEmployee(@NonNull EmployeePasswordVerifyDTO dto) {

        if (dto.getEId() == null) {
            throw new IllegalArgumentException("Employee Id cannot be blank");
        }

        String password = dto.getPassword();
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be blank");
        }

        Employee found = employeeRepository.findByeId(dto.getEId())
                .orElseThrow(() ->
                        new EmployeeNotFoundException("Employee not found with eid: " + dto.getEId())
                );

        if (!passwordEncoder.matches(password.trim(), found.getPassword())) {
            throw new WrongPasswordException("Invalid password");
        }

        employeeRepository.delete(found);

        return new EmployeeResponseDTO(
                found.getId(),
                found.getEId(),
                found.getUnitName(),
                found.getAllocation()
        );
    }

    // ================= ADMIN DELETE (UNCHANGED) =================
    public EmployeeResponseDTO deleteEmployeeByAdmin(@NonNull LoginEmployeeDTO dto) {

        if (dto.getEId() == null) {
            throw new IllegalArgumentException("Employee Id cannot be blank");
        }

        Employee found = employeeRepository.findByeId(dto.getEId())
                .orElseThrow(() ->
                        new EmployeeNotFoundException("Employee not found with eid: " + dto.getEId())
                );

        employeeRepository.delete(found);

        return new EmployeeResponseDTO(
                found.getId(),
                found.getEId(),
                found.getUnitName(),
                found.getAllocation()
        );
    }

    // ================= UTIL =================
    public Long employeeCount() {
        return employeeRepository.count();
    }

    public EmployeeResponseDTO convertDTO(Employee employee) {
        return new EmployeeResponseDTO(
                employee.getId(),
                employee.getEId(),
                employee.getUnitName(),
                employee.getAllocation()
        );
    }

    public List<EmployeeResponseDTO> getEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(this::convertDTO)
                .toList();
    }
}