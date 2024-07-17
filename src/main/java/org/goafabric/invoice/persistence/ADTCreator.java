package org.goafabric.invoice.persistence;

import org.goafabric.invoice.persistence.entity.ADTEntry;
import org.goafabric.invoice.process.adapter.patient.dto.MedicalRecord;
import org.goafabric.invoice.process.adapter.patient.dto.Patient;

public class ADTCreator {
    private ADTCreator() {
    }

    public static ADTEntry createPatient(Patient patient) {
        return new ADTEntry("patient", patient.id(),
                "PID|1|" + patient.familyName() + "^" + patient.givenName() + "|"
                        + patient.address().getFirst().street() + "^" + patient.address().getFirst().city());
    }

    public static ADTEntry createCondition(MedicalRecord condition) {
        return new ADTEntry("condition", condition.id(),
                "DG1|1|" + "I10|" + condition.code() + "^" + condition.display() + "||20230707|AD");
    }

    public static ADTEntry createChargeItem(MedicalRecord chargeItem) {
        return new ADTEntry("chargeitem", chargeItem.id(),
                "FT1|1|" + "I10|" + chargeItem.code() + "^" + chargeItem.display());
    }
}

