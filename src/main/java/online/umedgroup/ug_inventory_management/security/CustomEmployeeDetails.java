package online.umedgroup.ug_inventory_management.security;

import online.umedgroup.ug_inventory_management.models.Employee;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomEmployeeDetails implements UserDetails {
    private final Employee employee;

    public CustomEmployeeDetails(Employee employee) {
        this.employee = employee;
    }

    @Override
    public String getUsername() {
        return employee.getEId().toString();    // using eId as username
    }

    @Override
    public String getPassword() {
        return employee.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));
    }

    // You can keep these simple for now
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    public Employee getEmployee() {
        return this.employee;
    }
}
