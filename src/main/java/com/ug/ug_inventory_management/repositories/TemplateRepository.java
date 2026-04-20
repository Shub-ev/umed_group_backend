package com.ug.ug_inventory_management.repositories;

import com.ug.ug_inventory_management.models.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TemplateRepository extends JpaRepository<Template, Long> {
    boolean existsByTemplateName(String templateName);
    Optional<Template> findByTemplateNameIgnoreCase(String templateName);
}