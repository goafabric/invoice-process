package org.goafabric.invoice.persistence;

import org.goafabric.invoice.persistence.entity.EpisodeDetails;

public class ADTCreator {
    private ADTCreator() {
    }

    public static String createCondition(String code, String display) {
        return "DG1|1|" + "I10|" + code + "^" + display;
    }

    public static String createChargeItem(String code, String display) {
        return "FT1|1|" + code + "^" + display;
    }

    public static String createPatient(String familyName, String givenName, String city, String street) {
        return "PID|1|" + familyName + "^" + givenName + "|" + city + "^" + street;
    }

    public static String fromEpisodeDetails(EpisodeDetails episodeDetails) {
        switch (episodeDetails.type()) {
            case "condition" -> {
                return createCondition(episodeDetails.code(), episodeDetails.display());
            }
            case "chargeitem" -> {
                return createChargeItem(episodeDetails.code(), episodeDetails.display());
            }
            case "patient" -> {
                return createPatient(episodeDetails.patientFamily(), episodeDetails.patientGiven(), episodeDetails.patientCity(), episodeDetails.patientStreet());
            }
            default -> {
                return null;
            }
        }
    }
}

