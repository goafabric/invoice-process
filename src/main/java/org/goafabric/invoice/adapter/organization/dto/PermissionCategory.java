package org.goafabric.invoice.adapter.organization.dto;

public enum PermissionCategory {
    VIEW("VIEW"),
    CRUD("CRUD"),
    PROCESS("PROCESS");

    private String value;

    PermissionCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
