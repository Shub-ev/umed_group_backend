package com.ug.ug_inventory_management.utils;

public class Field {

    private String name;
    private String type;

    public String getName() { return name; }

    public String getType() { return type; }

    @Override
     public String toString(){
        return "FieldRequest : " + name + ", " + type;
    }
}