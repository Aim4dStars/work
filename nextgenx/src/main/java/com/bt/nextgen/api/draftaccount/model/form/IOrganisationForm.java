package com.bt.nextgen.api.draftaccount.model.form;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Created by m040398 on 14/03/2016.
 */
public interface IOrganisationForm extends ICorrelated {

    public OrganisationType getOrganisationType();

    public IAddressForm getRegisteredAddress();

    public String getABN();

    public String getACN();

    public String getName();

    public Boolean getRegisteredForGST();

    /**
     * ANZSIC codes are used as the industry code for version 1.0 of the onboarding service.
     * @return the standard ANZSIC code chosen by the adviser for this organisation.
     * @see #getIndustryUcmCode()
     */
    public String getAnzsicCode();

    /**
     * Westpac UCM uses its own set of codes for denoting industries, so use this code instead when sending
     * version 3.0 on-boarding requests.
     * @return
     */
    public String getIndustryUcmCode();

    public String getCisKey();

    public ITaxDetailsForm getTaxDetails();

    public ICrsTaxDetailsForm getCrsTaxDetails();

    /**
     * @return XMLGregorianCalendar or null if regdate is null or blank.
     */
    public XMLGregorianCalendar getDateOfRegistration();

    /**
     * @deprecated  to be removed
     * @return
     */
    @Deprecated
    public String getRegistrationState();

    public boolean hasSourceOfWealth();

    public String getSourceOfWealth();

    public String getAdditionalSourceOfWealth();

    public boolean hasIDVDocument();

    public RegulatoryBody getRegulatoryBody();

    public String getIDVDocIssuer();

    /**
     * Regulatory bodies.
     */
    public enum RegulatoryBody {
        APRA, ASIC, ATO;
    }

    /**
     * Organisation type enumeration.
     */
    public enum OrganisationType {
        COMPANY, SMSF, TRUST;
    }

    /**
     * A specific roles within an organisation that can be played by an individual.
     */
    public enum OrganisationRole {

        DIRECTOR("Director"), SECRETARY("Secretary"), SIGNATORY("Signatory"), TRUSTEE("Trustee");

        private final String jsonValue;

        private OrganisationRole(String jsonValue) {
            this.jsonValue = jsonValue;
        }

        public static OrganisationRole fromJson(String jsonValue) {
            for (OrganisationRole role : values()) {
                if (role.jsonValue.equalsIgnoreCase(jsonValue)) {
                    return role;
                }
            }
            return null;
        }

        public String getJsonValue() {
            return jsonValue;
        }
    }


}
