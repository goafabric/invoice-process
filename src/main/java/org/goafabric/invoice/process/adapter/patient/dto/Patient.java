package org.goafabric.invoice.process.adapter.patient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public record Patient(
    String id,
    Long version,
    String givenName,
    String familyName,

    String gender,
    LocalDate birthDate,
    
    List<Address> address,
    List<ContactPoint> contactPoint
) {}
