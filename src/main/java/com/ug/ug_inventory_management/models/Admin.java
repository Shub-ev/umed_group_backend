package com.ug.ug_inventory_management.models;

import jakarta.persistence.*;

@Entity
@Table(name="admin")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String password;

    public Admin() {
    }

    public Admin(String name,String password) {
        this.name=name;
        this.password=password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}