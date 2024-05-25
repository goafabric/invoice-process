package org.goafabric.invoice.process.adapter.authorization.dto;

public enum PermissionType {
    PATIENT("Patient"),
    ORGANIZATION("Organization"),
    CATALOGS("Catalogs"),
    APPOINTMENTS("Appointments"),
    FILES("Files"),
    MONITORING("Monitoring"),
    USERS("Users"),
    INVOICE("Invoice"),

    READ("Read"),
    READ_WRITE("Read & Write"),
    READ_WRITE_DELETE("Write & Delete"),
    ;

    private String value;

    PermissionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
