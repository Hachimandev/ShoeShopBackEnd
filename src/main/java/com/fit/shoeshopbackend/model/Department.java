package com.fit.shoeshopbackend.model;

public enum Department {
    Sales("Sales"),
    Warehouse("Warehouse"),
    Technical("Technical"),
    Administrative("Administrative"),
    HumanResources("Human Resources"),
    FinanceAccounting("Finance & Accounting"),
    Marketing("Marketing");

    private final String dispgetName;

    Department(String dispgetName) {
        this.dispgetName = dispgetName;
    }

    public String getDispgetName() {
        return dispgetName;
    }
}









