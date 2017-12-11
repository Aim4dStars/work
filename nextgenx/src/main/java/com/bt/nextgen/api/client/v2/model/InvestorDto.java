package com.bt.nextgen.api.client.v2.model;

import com.btfin.panorama.core.security.avaloq.userinformation.PersonRelationship;
import com.bt.nextgen.service.integration.domain.InvestorRole;
import com.bt.nextgen.service.integration.domain.PensionExemptionReason;

import java.util.List;

public class InvestorDto extends ClientDto {
    private boolean tfnProvided;
    // comes from AssociatedPerson mapping, for Trustee TFNs. Similar to tfnProvided, mapping is sorted out in the UI.
    private boolean tfnEntered;
    private String exemptionReason;
    private PensionExemptionReason pensionExemptionReason;
    private String safiDeviceId;
    private String idvs;
    private List<InvestorRole> personRoles;
    private String modificationSeq;
    private String gcmId;
    private String anzsicId;
    private String industry;
    private String openDate;
    private String investorType;
    private String tfnExemptId;
    private String saTfnExemptId;
    private PersonRelationship primaryRole;
    private boolean nominatedFlag;
    private String cisId;

    public List<InvestorRole> getPersonRoles() {
        return personRoles;
    }

    public void setPersonRoles(List<InvestorRole> personRoles) {
        this.personRoles = personRoles;
    }

    /**
     * This is the original vanilla mapping.
     * BTFG$UI_DOC_CUSTR_LIST_BP has_tfn.
     */
     public boolean isTfnProvided() {
        return tfnProvided;
    }

    /**
     * This is the original vanilla mapping.
     */
    public void setTfnProvided(boolean tfnProvided) {
        this.tfnProvided = tfnProvided;
    }
    
    /**
     * comes from AssociatedPerson mapping, for Trustee TFNs. Similar to tfnProvided, mapping is sorted out in the UI.
     * BTFG$UI_DOC_CUSTR_LIST person_has_tfn.
     */
    public boolean isTfnEntered() {
        return tfnEntered;
    }

    /**
     * comes from AssociatedPerson mapping, for Trustee TFNs. Similar to tfnProvided, mapping is sorted out in the UI.
     */
    public void setTfnEntered(boolean tfnEntered) {
        this.tfnEntered = tfnEntered;
    }

    public String getExemptionReason() {
        return exemptionReason;
    }

    public void setExemptionReason(String exemptionReason) {
        this.exemptionReason = exemptionReason;
    }

    public String getSafiDeviceId() {
        return safiDeviceId;
    }

    public void setSafiDeviceId(String safiDeviceId) {
        this.safiDeviceId = safiDeviceId;
    }

    public String getIdvs() {
        return idvs;
    }

    public void setIdvs(String idvs) {
        this.idvs = idvs;
    }

    public String getModificationSeq() {
        return modificationSeq;
    }

    public void setModificationSeq(String modificationSeq) {
        this.modificationSeq = modificationSeq;
    }

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }

    public String getAnzsicId() {
        return anzsicId;
    }

    public void setAnzsicId(String anzsicId) {
        this.anzsicId = anzsicId;
    }

    public String getOpenDate() {
        return openDate;
    }

    public void setOpenDate(String openDate) {
        this.openDate = openDate;
    }

    public String getInvestorType() {
        return investorType;
    }

    public void setInvestorType(String investorType) {
        this.investorType = investorType;
    }

    public String getTfnExemptId() {
        return tfnExemptId;
    }

    public void setTfnExemptId(String tfnExemptId) {
        this.tfnExemptId = tfnExemptId;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }


    public PersonRelationship getPrimaryRole() {
        return primaryRole;
    }

    public void setPrimaryRole(PersonRelationship primaryRole) {
        this.primaryRole = primaryRole;
    }

    public boolean isNominatedFlag() {
        return nominatedFlag;
    }

    public void setIsNominated(boolean isNominated) {
        this.nominatedFlag = isNominated;
    }

    public String getCisId() {
        return cisId;
    }

    public void setCisId(String cisId) {
        this.cisId = cisId;
    }


    /**
     * Retrieve the Tax Exempt Id for Pension Account
     * @return saTfnExemptId
     */
    public String getSaTfnExemptId() {
        return saTfnExemptId;
    }
    /**
     * Set the Tax Exempt Id for Pension Account
     * @param saTfnExemptId
     */
    public void setSaTfnExemptId(String saTfnExemptId) {
        this.saTfnExemptId = saTfnExemptId;
    }

    public PensionExemptionReason getPensionExemptionReason() {
        return pensionExemptionReason;
    }

    public void setPensionExemptionReason(PensionExemptionReason pensionExemptionReason) {
        this.pensionExemptionReason = pensionExemptionReason;
    }



}
