package online.umedgroup.ug_inventory_management.enums;
import com.fasterxml.jackson.annotation.JsonCreator;
public enum ActionType {
    INWARD,
    OUTWARD,
    CREATE,
    DELETE,
    UPDATE;
    @JsonCreator
    public static ActionType fromString(String value) {
        return ActionType.valueOf(value.toUpperCase());
    }
}