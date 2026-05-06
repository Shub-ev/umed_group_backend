package online.umedgroup.ug_inventory_management.repositories;
import online.umedgroup.ug_inventory_management.models.MonthRolloverLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthRolloverLogRepository extends JpaRepository<MonthRolloverLog, Long> {
}