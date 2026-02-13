package com.ug.ug_inventory_management.repositories;

import com.ug.ug_inventory_management.models.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByName(String name);
}