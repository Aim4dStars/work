package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.model.form.IAddressForm;
import com.bt.nextgen.api.draftaccount.model.form.ICrsTaxDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.ISmsfForm;
import com.bt.nextgen.api.draftaccount.model.form.ITaxDetailsForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.AnswerTypeEnum;
import com.bt.nextgen.api.draftaccount.schemas.v1.smsf.SmsfDetails;
import com.bt.nextgen.api.draftaccount.util.XMLGregorianCalendarUtil;
import org.apache.commons.lang3.StringUtils;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Implementation of the {@code ISmsfForm} interface.
 */
class SmsfForm implements ISmsfForm {

    private final SmsfDetails smsfDetails;
    private final Integer index;

    SmsfForm(Integer index, SmsfDetails smsfDetails){
        this.index = index;
        this.smsfDetails = smsfDetails;
    }

    @Override
    public OrganisationType getOrganisationType() {
        return OrganisationType.SMSF;
    }

    @Override
    public IAddressForm getRegisteredAddress() {
        if(this.smsfDetails.getSmsfaddressv2() != null) {
            return new AddressForm(this.smsfDetails.getSmsfaddressv2());
        }
        return new AddressForm(this.smsfDetails.getSmsfaddress());
    }

    @Override
    public String getABN() {
        return smsfDetails.getSmsfabn();
    }

    /**
     * @deprecated
     * @return
     */
    @Override
    @Deprecated
    public String getACN() {
        return null;
    }

    @Override
    public String getName() {
        return smsfDetails.getSmsfname();
    }

    @Override
    public Boolean getRegisteredForGST() {
        return smsfDetails.getRegisteredforgst()!=null && smsfDetails.getRegisteredforgst().equals(AnswerTypeEnum.YES);
    }

    @Override
    public String getAnzsicCode() {
        if(null!=smsfDetails.getIndustry()){
            return smsfDetails.getIndustry().getAnzsiccode();
        }
        return null;
    }

    @Override
    public String getIndustryUcmCode() {
        if(null!=smsfDetails.getIndustry()){
            return smsfDetails.getIndustry().getUcmcode();
        }
        return null;
    }

    /**
     * @deprecated
     *
     * @return
     */
    @Override
    @Deprecated
    public String getCisKey() {
        return null;
    }

    @Override
    public ITaxDetailsForm getTaxDetails() {
        return new TaxDetailsForm(smsfDetails.getTfn(),smsfDetails.getExemptionreason(),null,getTaxoption());
    }

    @Override
    public ICrsTaxDetailsForm getCrsTaxDetails() {
        return (null!=smsfDetails && null!=smsfDetails.getTaxdetails()) ? new CrsTaxDetailsForm(smsfDetails.getTaxdetails()): null;
    }

    private String getTaxoption(){
        return null!= smsfDetails.getTaxoption() ? smsfDetails.getTaxoption().toString() : null;
    }

    @Override
    public XMLGregorianCalendar getDateOfRegistration() {
        XMLGregorianCalendar xmlGregorianCalendar = null;
        String registeredOn = smsfDetails.getRegdate();
        if (StringUtils.isNotBlank(registeredOn)) {
            xmlGregorianCalendar = XMLGregorianCalendarUtil.getXMLGregorianCalendar(registeredOn, "dd/MM/yyyy");
        }
        return xmlGregorianCalendar;
    }

    @Override
    public String getRegistrationState() {
        return smsfDetails.getRegistrationstate();
    }

    @Override
    public boolean hasSourceOfWealth() {
        return null != smsfDetails.getWealthsource();
    }

    @Override
    public String getSourceOfWealth() {
        return smsfDetails.getWealthsource();
    }

    @Override
    public String getAdditionalSourceOfWealth() {
        return smsfDetails.getAdditionalsourceofwealth();
    }

    @Override
    public boolean hasIDVDocument() {
        return false;
    }

    @Override
    public RegulatoryBody getRegulatoryBody() {
        return RegulatoryBody.ATO;
    }

    @Override
    public String getIDVDocIssuer() {
        return smsfDetails.getDocissuer();
    }

    @Override
    public Integer getCorrelationSequenceNumber() {
        return index;
    }
}
