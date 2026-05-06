package online.umedgroup.ug_inventory_management.repositories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import online.umedgroup.ug_inventory_management.models.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface TemplateRepository extends JpaRepository<Template, Long> {

    boolean existsByTemplateName(String templateName);

    Optional<Template> findByTemplateNameIgnoreCase(String templateName);

    Page<Template> findByTemplateNameContainingIgnoreCase(String templateName, Pageable pageable);

    Optional<Template> findById(Long id);

    @Query("""
        SELECT t FROM Template t
        LEFT JOIN t.employees e
        WHERE t.isRestricted = false
        OR e.eId = :employeeId
       """)
    Page<Template> findEmployeeAccessibleTemplates(
            @Param("employeeId") Long employeeId,
            Pageable pageable
    );

    @Query("""
        SELECT t FROM Template t
        LEFT JOIN t.employees e
        WHERE (t.isRestricted = false OR e.eId = :employeeId)
        AND LOWER(t.templateName) LIKE LOWER(CONCAT('%', :name, '%'))
    """)
    Page<Template> findEmployeeAccessibleTemplatesByName(
            @Param("employeeId") Long employeeId,
            @Param("name") String name,
            Pageable pageable
    );

    @Query("SELECT t FROM Template t LEFT JOIN FETCH t.employees WHERE t.id = :id")
    Optional<Template> findByIdWithEmployees(@Param("id") Long id);
}