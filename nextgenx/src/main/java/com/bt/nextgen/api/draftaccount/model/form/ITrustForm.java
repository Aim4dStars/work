package com.bt.nextgen.api.draftaccount.model.form;

/**
 * Created by m040398 on 14/03/2016.
 */
public interface ITrustForm extends IOrganisationForm {
    public SettlorofTrustType getSettlorOfTrust();
    public String getBusinessName();
    public boolean hasSettlorOfTrust();
    public String getOrganisationName();
    public String getTitle();
    public String getFirstName();
    public String getMiddleName();
    public String getLastName();
    public String getDescription();
    public String getDescriptionOther();
    public ITrustIdentityVerificationForm getIdentityDocument();
    public String getRegulatorName();
    public String getRegulatorLicenseNumber();
    public String getArsn();
    public TrustForm.TrustType getTrustType();
    public String getNameOfLegislation();
    public String getIdvURL();
    public String getIDVLegislationName();
    public javax.xml.datatype.XMLGregorianCalendar getIDVSearchDate();
    public Boolean getPersonalInvestmentEntity();

    public enum SettlorofTrustType{
        ORGANISATION("organisation"),
        INDIVIDUAL("individual");
        private final String type;
        SettlorofTrustType(String type){
            this.type =type;
        }

        public static SettlorofTrustType fromstring(String type){
            for(SettlorofTrustType s : SettlorofTrustType.values()){
                if(s.type.equalsIgnoreCase(type)) {
                    return s;
                }
            }
            throw new IllegalArgumentException("Invalid SettlorofTrustType: " + type);
        }
    }

    public enum TrustType {
        FAMILY("family"),
        OTHER("other"),
        REGISTERED_MIS("invscheme"),
        GOVT_SUPER("govsuper"),
        REGULATED("regulated");

        private final String repr;

        TrustType(String repr) {
            this.repr = repr;
        }

        public static TrustType fromString(String text) {
            for (TrustType t : TrustType.values()) {
                if (t.repr.equalsIgnoreCase(text)) {
                    return t;
                }
            }

            throw new IllegalArgumentException("Invalid trust type: " + text);
        }

        public String value() {
            return repr;
        }
    }

    public enum StandardTrustDescription {
        FAMILY("family","btfg$discrny_trust"),
        OTHER("other","btfg$oth"),
        TESTAMENTARY_TRUST("testamentary","btfg$tstmtry_trust"),
        UNIT_TRUST("unittrust","btfg$unit_trust"),
        UNREGISTERED_MANAGED_INVESTMENT_SCHEME("unregmanagedinv","btfg$unreg_mngd_invst_sch");

        private final String description;
        private final String code;

        StandardTrustDescription(String description, String code) {
            this.description = description;
            this.code = code;
        }

        public static StandardTrustDescription fromString(String text) {
            for (StandardTrustDescription t : StandardTrustDescription.values()) {
                if (t.description.equalsIgnoreCase(text)) {
                    return t;
                }
            }

            throw new IllegalArgumentException("Invalid trust description type: "  +text);
        }

        public String value() {
            return code;
        }
    }
}
