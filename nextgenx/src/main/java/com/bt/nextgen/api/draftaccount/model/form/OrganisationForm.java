package com.bt.nextgen.api.draftaccount.model.form;

import com.bt.nextgen.api.draftaccount.util.XMLGregorianCalendarUtil;
import org.apache.commons.lang3.StringUtils;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Map;

/**
 * @deprecated Use the v1 version of this class instead.
 */
@Deprecated
abstract class OrganisationForm extends Correlated implements IOrganisationForm{

    final Map<String, Object> map;

    protected abstract String getFieldPrefix();
    public abstract OrganisationType getOrganisationType();
    public abstract IAddressForm getRegisteredAddress();

    OrganisationForm(Map<String, Object> organisation) {
        super(organisation);
        this.map = organisation;
    }

    @Override
    public String getABN() {
        return (String) this.map.get(getFieldPrefix() + "abn");
    }

    @Override
    public String getACN() {
        return (String) this.map.get("acn");
    }

    @Override
    public String getName() {
        return (String) this.map.get(getFieldPrefix() + "name");
    }

    @Override
    public Boolean getRegisteredForGST() {
        String registered = (String) this.map.get("registeredforgst");
        return "yes".equals(registered);
    }

    /**
     * ANZSIC codes are used as the industry code for version 1.0 of the onboarding service.
     * @return the standard ANZSIC code chosen by the adviser for this organisation.
     * @see #getIndustryUcmCode()
     */
    @Override
    public String getAnzsicCode() {
        return (String) getIndustry().get("anzsiccode");
    }

    /**
     * Westpac UCM uses its own set of codes for denoting industries, so use this code instead when sending
     * version 3.0 on-boarding requests.
     * @return
     */
    @Override
    public String getIndustryUcmCode() {
        return (String) getIndustry().get("ucmcode");
    }

    @Override
    public String getCisKey() {
        return (String) map.get("ciskey");
    }

    @Override
    public TaxDetailsForm getTaxDetails() {
        return new TaxDetailsForm(this.map);
    }

    /**
     * @return XMLGregorianCalendar or null if regdate is null or blank.
     */
    @Override
	public XMLGregorianCalendar getDateOfRegistration() {
		XMLGregorianCalendar xmlGregorianCalendar = null;
        String registeredOn = (String) map.get("regdate");
        if (StringUtils.isNotBlank(registeredOn)) {
        	xmlGregorianCalendar = XMLGregorianCalendarUtil.getXMLGregorianCalendar(registeredOn, "dd/MM/yyyy");
        }
        return xmlGregorianCalendar;
	}

    @Override
    public String getRegistrationState() {
        return (String) map.get("registrationstate");
    }

    @Override
    public boolean hasSourceOfWealth() {
        return getSourceOfWealth() != null;
    }

    @Override
    public String getSourceOfWealth() {
        return (String) map.get("wealthsource");
    }

    @Override
    public String getAdditionalSourceOfWealth() {
        return (String) map.get("additionalsourceofwealth");
    }

    private Map<String, Object> getIndustry() {
        return getSubMap("industry");
    }

    @Override
    public abstract boolean hasIDVDocument();

    @Override
    public abstract RegulatoryBody getRegulatoryBody();

    @Override
    public abstract String getIDVDocIssuer();
}
