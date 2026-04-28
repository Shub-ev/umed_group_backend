package online.umedgroup.ug_inventory_management.repositories;

import online.umedgroup.ug_inventory_management.models.TemplateField;
import online.umedgroup.ug_inventory_management.models.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface TemplateFieldRepository extends JpaRepository<TemplateField, Long> {
    List<TemplateField> findByTemplate_IdOrderByDisplayOrderAsc(Long templateId);

    boolean existsByTemplate_IdAndFieldNameIgnoreCase(Long templateId, String fieldName);

    @Query("SELECT MAX(f.displayOrder) FROM TemplateField f WHERE f.template.id = :templateId")
    Integer findMaxDisplayOrder(@Param("templateId") Long templateId);

    Optional<TemplateField> findByTemplate_IdAndFieldNameIgnoreCase(Long templateId, String fieldName);

    @Modifying
    @Transactional
    @Query("DELETE FROM TemplateField f WHERE f.template.id = :templateId")
    void deleteByTemplateId(@Param("templateId") Long templateId);
}