package org.goafabric.invoice.process.adapter.s3.dto;

public record ObjectEntry(
    String objectName,
    String contentType,
    Long objectSize,
    byte[] data)
{}
