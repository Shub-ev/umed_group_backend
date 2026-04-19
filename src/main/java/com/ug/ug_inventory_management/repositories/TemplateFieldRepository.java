package com.ug.ug_inventory_management.repositories;

import com.ug.ug_inventory_management.models.TemplateField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TemplateFieldRepository extends JpaRepository<TemplateField, Long> {
    List<TemplateField> findByTemplate_IdOrderByDisplayOrderAsc(Long templateId);

    boolean existsByTemplate_IdAndFieldNameIgnoreCase(Long templateId, String fieldName);

    @Query("SELECT MAX(f.displayOrder) FROM TemplateField f WHERE f.template.id = :templateId")
    Integer findMaxDisplayOrder(@Param("templateId") Long templateId);
}