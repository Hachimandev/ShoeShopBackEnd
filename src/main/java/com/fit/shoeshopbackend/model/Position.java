package com.fit.shoeshopbackend.model;

public enum Position {
    Staff("Staff"),
    Specialist("Specialist"),
    Director("Director"),
    CEO("CEO"),
    DeputyManager("Deputy Manager"),
    DepartmentManager("Department Manager");

    private final String dispgetName;

    Position(String dispgetName) {
        this.dispgetName = dispgetName;
    }

    public String getDispgetName() {
        return dispgetName;
    }
}









