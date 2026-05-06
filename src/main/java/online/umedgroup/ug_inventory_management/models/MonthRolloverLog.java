package online.umedgroup.ug_inventory_management.models;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "month_rollover_log")
public class MonthRolloverLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rollover_key", nullable = false, unique = true)
    private String rolloverKey;

    @Column(name = "executed_at", nullable = false)
    private LocalDateTime executedAt;

    public MonthRolloverLog() {}

    public MonthRolloverLog(String rolloverKey, LocalDateTime executedAt) {
        this.rolloverKey = rolloverKey;
        this.executedAt = executedAt;
    }

    public Long getId() { return id; }

    public String getRolloverKey() { return rolloverKey; }
    public void setRolloverKey(String rolloverKey) { this.rolloverKey = rolloverKey; }

    public LocalDateTime getExecutedAt() { return executedAt; }
    public void setExecutedAt(LocalDateTime executedAt) { this.executedAt = executedAt; }
}