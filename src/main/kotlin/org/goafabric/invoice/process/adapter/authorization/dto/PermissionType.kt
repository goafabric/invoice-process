package org.goafabric.invoice.process.adapter.authorization.dto

enum class PermissionType(val value: String) {
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
}
