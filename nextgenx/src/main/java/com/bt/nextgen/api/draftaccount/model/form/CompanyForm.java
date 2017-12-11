package com.bt.nextgen.api.draftaccount.model.form;

import java.util.Map;

/**
 * @deprecated Should be using the v1 version of this class instead.
 */
@Deprecated
class CompanyForm extends OrganisationForm implements ICompanyForm {
    public CompanyForm(Map<String, Object> organisation) {
        super(organisation);
    }

    @Override
    public String getIDVDocIssuer() {
        return (String) map.get("docissuer");
    }

    @Override
    protected String getFieldPrefix() {
        return "company";
    }

    @Override
    public OrganisationType getOrganisationType() {
        return OrganisationType.COMPANY;
    }

	//CHECKSTYLE:OFF
    public String getAsicName() {
        return (String) this.map.get("asicregisteredname");
    }
	//CHECKSTYLE:ON

    public String getOccupierName() { return  this.map.containsKey("occupiername") ? this.map.get("occupiername").toString(): "";}

    @Override
    public Boolean getPersonalInvestmentEntity() {
        return (Boolean)this.map.get("personalinvestmententity");
    }

    public IAddressForm getPlaceOfBusinessAddress() {
        return new AddressForm((Map<String, Object>) this.map.get("placeofbusiness"));
    }

    public IAddressForm getRegisteredAddress() {
        return new AddressForm((Map<String, Object>) this.map.get("companyoffice"));
    }

    @Override
    public ICrsTaxDetailsForm getCrsTaxDetails() {
        return null;
    }

    @Override
	public boolean hasIDVDocument() {
		return false;
	}

    @Override
    public RegulatoryBody getRegulatoryBody() {
        return RegulatoryBody.ASIC;
    }
}
