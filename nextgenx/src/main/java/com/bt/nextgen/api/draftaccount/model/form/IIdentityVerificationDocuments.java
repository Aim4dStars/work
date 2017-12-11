package com.bt.nextgen.api.draftaccount.model.form;

import java.util.List;

/**
 * Created by m040398 on 14/03/2016.
 */
public interface IIdentityVerificationDocuments {
    
    List<IIdentityDocument> getIdentityDocuments();

    /**
     * Created by F030695 on 31/03/2016.
     * <p/>
     * TODO: this is temporary until the structure of IdentityVerificationDocuments is refactored @ US16228 (https://rally1.rallydev.com/#/53862485470/detail/userstory/53464740392)
     */
    public enum IdentityTypeEnum {
        DRIVERS_LICENSE("driverlicence"),
        NATIONAL_ID("nationalid"),
        AGE_CARD("agecard"),
        PASSPORT("passport"),
        TAXATION_NOTICE("atonotice"),
        BIRTH_CERTIFICATE("birthcertificate"),
        CITIZENSHIP_CERTIFICATE("citizenshipdocument"),
        FINANCIAL_BENEFITS_NOTICE("financialbenefits"),
        HEALTH_CARD("healthcard"),
        PENSION_CARD("pensioncard"),
        UTILITIES_NOTICE("utilitiesnotice");

        private final String value;

        IdentityTypeEnum(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

}
