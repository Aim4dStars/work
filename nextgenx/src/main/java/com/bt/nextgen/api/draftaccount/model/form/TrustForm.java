package com.bt.nextgen.api.draftaccount.model.form;

import com.bt.nextgen.api.draftaccount.util.XMLGregorianCalendarUtil;

import java.util.Map;

import static org.springframework.util.StringUtils.hasText;

/**
 * @deprecated Use the v1 version of this class instead.
 */
@Deprecated
class TrustForm extends OrganisationForm implements ITrustForm{

    private final TrustType trustType;

    public TrustForm(Map<String, Object> trustDetails) {
        super(trustDetails);
        this.trustType = TrustType.fromString((String) trustDetails.get("trusttype"));
    }

    public String getBusinessName() {
        return (String) map.get("businessname");
    }

    public SettlorofTrustType getSettlorOfTrust() {
        return SettlorofTrustType.valueOf(((String)map.get("settloroftrust")).toUpperCase());
    }

    public boolean hasSettlorOfTrust() {
        return hasText((String)map.get("settloroftrust"));
    }

    public String getOrganisationName(){
        return (String)map.get("organisationName");
    }

    public String getTitle() {
        return (String) map.get("title");
    }

    public String getFirstName() {
        return (String) map.get("firstname");
    }

    public String getMiddleName() {
        return (String) map.get("middlename");
    }

    public String getLastName() {
        return (String) map.get("lastname");
    }

    @Override
    public String getABN() {
        return (String) this.map.get("abn");
    }

    @Override
    public ICrsTaxDetailsForm getCrsTaxDetails() {
        return null;
    }

    @Override
    public String getIDVDocIssuer() {
        throw new UnsupportedOperationException("No Doc issuer for the trust form");
    }

    @Override
    protected String getFieldPrefix() {
        return "trust";
    }

    @Override
    public OrganisationType getOrganisationType() {
        return OrganisationType.TRUST;
    }

    public boolean hasIDVDocument() {
        switch (trustType) {
            case FAMILY:
            case OTHER:
                return true;
            default:
                return false;
        }
    }

	@Override
	public AddressForm getRegisteredAddress()
	{
		return new AddressForm((Map<String, Object>) this.map.get("address"));
	}

	public String getDescription() {
		return (String)map.get("trustdescription");
	}

    public String getDescriptionOther() {
        return (String)map.get("trustdescriptionother");
    }

	public ITrustIdentityVerificationForm getIdentityDocument() {
		return new TrustIdentityVerificationForm((Map <String, Object>)map.get("trustverification"));
	}

    public String getRegulatorName() {
        return (String) map.get("nameofregulator");
    }

    public String getRegulatorLicenseNumber() {
        return (String) map.get("licensingnumber");
    }

    public String getArsn() {
        return (String) map.get("arsn");
    }

    public TrustType getTrustType() {
        return trustType;
    }

    public String getNameOfLegislation() {
        return (String) map.get("nameoflegislation");
    }

    public String getIdvURL() {
        switch (trustType) {
            case REGULATED:
                return "www.apra.gov.au";

            case REGISTERED_MIS:
                return "ASIC";

            case GOVT_SUPER:
                return (String) map.get("nameofwebsite");

            default:
                return null;
        }
    }


    public String getIDVLegislationName() {
        return (String) map.get("trustnameoflegislation");
    }

    public javax.xml.datatype.XMLGregorianCalendar getIDVSearchDate() {
        String documentDate = (String) map.get("searchdate");
        return XMLGregorianCalendarUtil.getXMLGregorianCalendar(documentDate, "dd/MM/yyyy");
    }

    @Override
    public Boolean getPersonalInvestmentEntity() {
        return (Boolean)map.get("personalinvestmententity");
    }

    @Override
    public RegulatoryBody getRegulatoryBody() {
        switch (trustType) {
            case REGULATED:
            case GOVT_SUPER:
                return RegulatoryBody.APRA;
            case REGISTERED_MIS:
                return RegulatoryBody.ASIC;
            default:
                return null;
        }
    }
}
