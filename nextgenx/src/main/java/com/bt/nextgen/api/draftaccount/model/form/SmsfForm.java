package com.bt.nextgen.api.draftaccount.model.form;

import java.util.Map;

/**
 * @deprecated Use the v1 version of this class instead.
 */
@Deprecated
class SmsfForm extends OrganisationForm implements ISmsfForm{
    public SmsfForm(Map<String, Object> corporatesmsfdetails) {
        super(corporatesmsfdetails);
    }

    @Override
    protected String getFieldPrefix() {
        return "smsf";
    }

    @Override
    public OrganisationType getOrganisationType() {
        return OrganisationType.SMSF;
    }

    @Override
    public AddressForm getRegisteredAddress() {
        return new AddressForm((Map<String, Object>) this.map.get("smsfaddress"));
    }

    @Override
    public ICrsTaxDetailsForm getCrsTaxDetails() {
        return null;
    }

    @Override
    public String getIDVDocIssuer() {
        return (String) map.get("docissuer");
    }

    public boolean hasIDVDocument() {
    	return false;
    }

    @Override
    public RegulatoryBody getRegulatoryBody() {
        return RegulatoryBody.ATO;
    }
}
