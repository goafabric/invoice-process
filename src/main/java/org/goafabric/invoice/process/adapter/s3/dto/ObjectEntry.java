package org.goafabric.invoice.process.adapter.s3.dto;

@SuppressWarnings("java:S6218")
public record ObjectEntry(
    String objectName,
    String contentType,
    Long objectSize,
    byte[] data)
{}
