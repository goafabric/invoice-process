package org.goafabric.invoice.process.adapter.authorization.dto;

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
