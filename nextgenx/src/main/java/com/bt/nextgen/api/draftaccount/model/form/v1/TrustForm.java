package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.model.form.IAddressForm;
import com.bt.nextgen.api.draftaccount.model.form.ICrsTaxDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.ITaxDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.ITrustForm;
import com.bt.nextgen.api.draftaccount.model.form.ITrustIdentityVerificationForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.AnswerTypeEnum;
import com.bt.nextgen.api.draftaccount.schemas.v1.trust.TrustDetails;
import com.bt.nextgen.api.draftaccount.util.XMLGregorianCalendarUtil;
import org.apache.commons.lang3.StringUtils;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Implementation of the {@code ITrustForm} interface.
 */
class TrustForm implements ITrustForm {

    private final TrustDetails trustDetails;
    private final Integer index;

    TrustForm(Integer index, TrustDetails trustDetails){
        this.index = index;
        this.trustDetails = trustDetails;
    }


    @Override
    public SettlorofTrustType getSettlorOfTrust() {

        if(null != trustDetails.getSettloroftrust()){
            return SettlorofTrustType.fromstring(trustDetails.getSettloroftrust().toString());
        }

        return null;
    }

    @Override
    public String getBusinessName() {
        return trustDetails.getBusinessname();
    }

    @Override
    public boolean hasSettlorOfTrust() {
        return null!=trustDetails;
    }

    @Override
    public String getOrganisationName() {
        return trustDetails.getOrganisationName();
    }

    @Override
    public String getTitle() {
        return trustDetails.getTitle();
    }

    @Override
    public String getFirstName() {
        return trustDetails.getFirstname();
    }

    @Override
    public String getMiddleName() {
        return trustDetails.getMiddlename();
    }

    @Override
    public String getLastName() {
        return trustDetails.getLastname();
    }

    @Override
    public String getDescription() {
        return null!=trustDetails.getTrustdescription() ? trustDetails.getTrustdescription().toString() : null;
    }

    @Override
    public String getDescriptionOther() {
        return trustDetails.getTrustdescriptionother();
    }

    @Override
    public ITrustIdentityVerificationForm getIdentityDocument() {
        return new TrustIdentityVerificationForm(trustDetails.getTrustverification());
    }

    /**
     * Regulator name.
     * @return the name of the regulator.
     * @deprecated not used as part of form submission.
     */
    @Override
    @Deprecated
    public String getRegulatorName() {
        return null;
    }

    @Override
    public String getRegulatorLicenseNumber() {
        return trustDetails.getLicensingnumber();
    }

    @Override
    public String getArsn() {
        return trustDetails.getArsn();
    }

    @Override
    public TrustType getTrustType() {
        if(trustDetails.getTrusttype()!=null) {
            return TrustType.fromString(trustDetails.getTrusttype().toString());
        }
        return null;
    }

    @Override
    public String getNameOfLegislation() {
        return trustDetails.getNameoflegislation();
    }

    @Override
    public String getIdvURL() {
        switch(getTrustType()){
            case REGULATED:
                return "www.apra.gov.au";
            case REGISTERED_MIS:
                return "ASIC";
            case GOVT_SUPER:
                return trustDetails.getNameofwebsite();
            default:
                return null;
        }
    }

    /**
     * @deprecated
     * @return
     */
    @Override
    @Deprecated
    public String getIDVLegislationName() {
        return null;
    }

    @Override
    public XMLGregorianCalendar getIDVSearchDate() {
        String documentDate = trustDetails.getSearchdate();
        return XMLGregorianCalendarUtil.getXMLGregorianCalendar(documentDate, "dd/MM/yyyy");
    }

    @Override
    public Boolean getPersonalInvestmentEntity() {
        return trustDetails.getPersonalinvestmententity();
    }

    @Override
    public OrganisationType getOrganisationType() {
        return OrganisationType.TRUST;
    }

    @Override
    public IAddressForm getRegisteredAddress() {
        if(this.trustDetails.getAddressv2() != null){
            return new AddressForm(this.trustDetails.getAddressv2());
        }
        return new AddressForm(this.trustDetails.getAddress());
    }

    @Override
    public String getABN() {
        return trustDetails.getAbn();
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
        return trustDetails.getTrustname();
    }

    @Override
    public Boolean getRegisteredForGST() {
        return AnswerTypeEnum.YES.equals(trustDetails.getRegisteredforgst());
    }

    @Override
    public String getAnzsicCode() {
        if(null!=trustDetails.getIndustry()){
            return trustDetails.getIndustry().getAnzsiccode();
        }
        return null;
    }

    @Override
    public String getIndustryUcmCode() {
        if(null!=trustDetails.getIndustry()){
            return trustDetails.getIndustry().getUcmcode();
        }
        return null;
    }

    /**
     * @deprecated
     * @return
     */
    @Override
    @Deprecated
    public String getCisKey() {
        return null;
    }

    @Override
    public ITaxDetailsForm getTaxDetails() {
        return new TaxDetailsForm(trustDetails.getTfn(),trustDetails.getExemptionreason(),null,getTaxoption());
    }

    @Override
    public ICrsTaxDetailsForm getCrsTaxDetails() {
        return (null!= trustDetails && null!= trustDetails.getTaxdetails()) ? new CrsTaxDetailsForm(trustDetails.getTaxdetails()) : null;
    }

    private String getTaxoption(){
        return null!= trustDetails.getTaxoption() ? trustDetails.getTaxoption().toString() : null;
    }

    @Override
    public XMLGregorianCalendar getDateOfRegistration() {
        XMLGregorianCalendar xmlGregorianCalendar = null;
        String registeredOn = trustDetails.getRegdate();
        if (StringUtils.isNotBlank(registeredOn)) {
            xmlGregorianCalendar = XMLGregorianCalendarUtil.getXMLGregorianCalendar(registeredOn, "dd/MM/yyyy");
        }
        return xmlGregorianCalendar;
    }

    @Override
    public String getRegistrationState() {
        return trustDetails.getRegistrationstate();
    }

    @Override
    public boolean hasSourceOfWealth() {
        return null!=trustDetails.getWealthsource();
    }

    @Override
    public String getSourceOfWealth() {
        return trustDetails.getWealthsource();
    }

    @Override
    public String getAdditionalSourceOfWealth() {
        return trustDetails.getAdditionalsourceofwealth();
    }

    @Override
    public boolean hasIDVDocument() {
        switch (getTrustType()) {
            case FAMILY:
            case OTHER:
                return true;
            default:
                return false;
        }
    }

    @Override
    public RegulatoryBody getRegulatoryBody() {
        switch (getTrustType()) {
            case REGULATED:
            case GOVT_SUPER:
                return RegulatoryBody.APRA;
            case REGISTERED_MIS:
                return RegulatoryBody.ASIC;
            default:
                return null;
        }
    }

    /**
     * @deprecated
     * @return
     */
    @Override
    @Deprecated
    public String getIDVDocIssuer() {
        return null;
    }

    @Override
    public Integer getCorrelationSequenceNumber() {
        return index;
    }
}
