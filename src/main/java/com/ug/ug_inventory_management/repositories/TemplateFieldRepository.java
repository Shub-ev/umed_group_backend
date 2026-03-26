package com.ug.ug_inventory_management.repositories;

import com.ug.ug_inventory_management.models.TemplateField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TemplateFieldRepository extends JpaRepository<TemplateField, Long> {
    List<TemplateField> findByTemplateId(Long templateId);
}

