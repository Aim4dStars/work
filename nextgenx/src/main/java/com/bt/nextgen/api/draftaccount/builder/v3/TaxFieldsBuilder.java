package com.bt.nextgen.api.draftaccount.builder.v3;

import java.util.List;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import ns.btfin_com.party.v3_0.ForeignCountriesForTaxationType;
import ns.btfin_com.party.v3_0.ForeignCountryForTaxationType;
import ns.btfin_com.party.v3_0.ReasonForTaxIdentificationNumberExemptionType;
import ns.btfin_com.product.common.investmentaccount.v2_0.InvolvedPartyDetailsType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.TFNRegistrationExemptionType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.TFNRegistrationType;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.draftaccount.model.form.IAusTaxDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.model.form.ICrsTaxDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IExtendedPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IOverseasTaxDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.ITaxDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.v1.ShareholderMemberDetailsForm;
import com.bt.nextgen.service.integration.domain.ExemptionReason;

import static com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm.AccountType.NEW_CORPORATE_SMSF;
import static com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm.AccountType.NEW_INDIVIDUAL_SMSF;
import static com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm.AccountType.SUPER_ACCUMULATION;
import static com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm.AccountType.SUPER_PENSION;
import static com.bt.nextgen.api.draftaccount.schemas.v1.base.TaxOptionTypeEnum.PENSIONER;
import static com.bt.nextgen.api.draftaccount.schemas.v1.base.TaxOptionTypeEnum.TAX_FILE_NUMBER_OR_EXEMPTION_NOT_PROVIDED;
import static com.bt.nextgen.api.draftaccount.schemas.v1.base.TaxOptionTypeEnum.TAX_FILE_NUMBER_PROVIDED;
import static com.bt.nextgen.api.draftaccount.schemas.v1.base.TinOptionTypeEnum.EXEMPTION_REASON_PROVIDED;
import static com.bt.nextgen.api.draftaccount.schemas.v1.base.TinOptionTypeEnum.TIN_PROVIDED;
import static ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.TFNRegistrationExemptionType.PENSIONER_FOR_SUPER;
import static ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.TFNRegistrationType.EXEMPT;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.upperCase;
@Service
public class TaxFieldsBuilder {

    //This is the reason which is set for existing panorama investors when tfn is provided.
    private static final String NO_EXEMPTION = "No exemption";

    @Deprecated
    public void populateTax(InvolvedPartyDetailsType investorDetails, IExtendedPersonDetailsForm personDetailsForm, IClientApplicationForm.AccountType accountType){
        if(personDetailsForm.hasCrsTaxDetails()){
            populateCrsTaxRelatedFieldsForNewInvestor(investorDetails, personDetailsForm);
        }else{
            populateTaxRelatedFieldsForNewInvestor(investorDetails, personDetailsForm);
            if (!isNewSmsfAccount(accountType) && !isSuperAccount(accountType)) {
                investorDetails.setCountryOfResidenceForTax("AU");
            }
        }
    }

    public void populateCrsTax(InvolvedPartyDetailsType investorDetails, IExtendedPersonDetailsForm personDetailsForm, boolean existingInvestor){
        if(!existingInvestor){
            populateCrsTaxRelatedFieldsForNewInvestor(investorDetails, personDetailsForm);
        } else {
            populateCrsTaxRelatedFieldsForExistingPanInvestor(investorDetails, personDetailsForm);
        }
    }

    public void populateCrsTaxRelatedFieldsForNewInvestor(InvolvedPartyDetailsType investorDetails, ICrsTaxDetailsForm crsTaxDetailsForm) {
        populateCRSTaxRelatedFields(investorDetails,crsTaxDetailsForm,false);
        populateOverseasCRSTaxRelatedFields(investorDetails,crsTaxDetailsForm);
        populateCountryOfResidence(investorDetails, crsTaxDetailsForm);
    }

    public void populateCrsTaxRelatedFieldsForExistingPanInvestor(InvolvedPartyDetailsType investorDetails, IExtendedPersonDetailsForm personDetailsForm) {
        populateCRSTaxRelatedFields(investorDetails,personDetailsForm,true);
        populateCountryOfResidence(investorDetails, personDetailsForm);
    }

    // Populate the tax without using  CRS Existing PAN customers still uses this until crs retrieve goes live)
    public void populateTaxRelatedFieldsForNewInvestor(InvolvedPartyDetailsType investorDetails, ITaxDetailsForm taxDetailsForm) {
        populateTaxRelatedFields(investorDetails,taxDetailsForm,false);
        investorDetails.setCountryOfResidenceForTax(upperCase(taxDetailsForm.getTaxCountryCode()));
    }


    private boolean isNewSmsfAccount(IClientApplicationForm.AccountType accountType) {
        return accountType == NEW_INDIVIDUAL_SMSF || accountType == NEW_CORPORATE_SMSF;
    }

    private boolean isSuperAccount(IClientApplicationForm.AccountType accountType) {
        return accountType == SUPER_ACCUMULATION || accountType == SUPER_PENSION;
    }

    public void populateTaxRelatedFields(InvolvedPartyDetailsType investorDetails, ITaxDetailsForm taxDetailsForm, boolean existingInvestor){
        if (taxDetailsForm.hasTaxFileNumber()) {
            investorDetails.getTFN().add(taxDetailsForm.getTaxFileNumber());
            investorDetails.setTFNRegistration(TFNRegistrationType.ONE.value());
        //This is for super pension account because for super pension  account exemption reason is stored in taxoption.
        } else if((PENSIONER.value()).equals(taxDetailsForm.getTaxoption())) {
            setTFNRegistrationExemption(investorDetails, PENSIONER_FOR_SUPER, EXEMPT);
        } else if (taxDetailsForm.hasExemptionReason() && !(NO_EXEMPTION).equals(taxDetailsForm.getExemptionReason())) {
            setTFNRegistrationExemption(investorDetails, getExemptionReasonType(taxDetailsForm.getExemptionReason()), EXEMPT);
        } else {
            investorDetails.setTFNRegistration(TFNRegistrationType.NONE.value());
        }
    }

    private void setTFNRegistrationExemption(InvolvedPartyDetailsType investorDetails, TFNRegistrationExemptionType exemptionType, TFNRegistrationType registrationType) {
        investorDetails.setTFNRegistrationExemption(exemptionType.value());
        investorDetails.setTFNRegistration(registrationType.value());
    }


    /**
     * This method maps Avaloq internal ids to the corresponding ICC enums. New reason codes must not be added
     * in Avaloq until this mapping knows how to map them.
     */
    private TFNRegistrationExemptionType getExemptionReasonType(String exemptionReason) {
        //Convert UI exemption reason to avaloq intlId, if no code found, this means it's already in the intlId format
        ExemptionReason exemptionCode = ExemptionReason.fromValue(exemptionReason);
        if (null != exemptionCode) {
            exemptionReason = exemptionCode.toString();
        }
        switch (exemptionReason) {
            case "pensioner":
                return TFNRegistrationExemptionType.PENSIONER;
            case "social_ben":
                return TFNRegistrationExemptionType.OTHER_ELIGIBLE_BENEFIT;
            case "tax_exempt":
                return TFNRegistrationExemptionType.NOT_REQUIRED;
            case "fin_busi_provid":
                return TFNRegistrationExemptionType.FINANCE_PROVIDER;
            case "norfolk_island_res":
                return TFNRegistrationExemptionType.NORFOLK_ISLAND_RESIDENT;
            case "non_au_resi":
                return TFNRegistrationExemptionType.NON_RESIDENT;
            case "alpha_char_tfn":
                return TFNRegistrationExemptionType.ALPHA_TFN_QUOTED;
            case "":
                return TFNRegistrationExemptionType.OTHER;
            default:
                throw new IllegalArgumentException("Can not map TFN Exemption Reason " + exemptionReason);
        }
    }

    public void populateCRSTaxRelatedFields(InvolvedPartyDetailsType investorDetails, ICrsTaxDetailsForm crsTaxDetailsForm, boolean existingInvestor) {
        IAusTaxDetailsForm ausTaxDetailsForm = crsTaxDetailsForm.getAustralianTaxDetails();
        if (null != ausTaxDetailsForm) {
            if (TAX_FILE_NUMBER_PROVIDED.equals(ausTaxDetailsForm.getTaxOption()) && isNotBlank(ausTaxDetailsForm.getTFN())) {
                investorDetails.getTFN().add(ausTaxDetailsForm.getTFN());
                investorDetails.setTFNRegistration(TFNRegistrationType.ONE.value());
            } else if ((PENSIONER).equals(ausTaxDetailsForm.getTaxOption())) {
                setTFNRegistrationExemption(investorDetails, PENSIONER_FOR_SUPER, EXEMPT);
            } else if (ausTaxDetailsForm.hasExemptionReason() && !(NO_EXEMPTION).equals(ausTaxDetailsForm.getExemptionReason())) {
                setTFNRegistrationExemption(investorDetails, getExemptionReasonType(ausTaxDetailsForm.getExemptionReason().toString()), EXEMPT);
            }  else {
                investorDetails.setTFNRegistration(TFNRegistrationType.NONE.value());
                // only if tax option present then and then apply while updating. For legal entities we do not capture at all.
                if(existingInvestor && ausTaxDetailsForm.isTaxCountryAustralia() && null != ausTaxDetailsForm.getTaxOption()) {
                    investorDetails.setTFNRegistrationExemption(TFNRegistrationExemptionType.TFN_NOT_QUOTED.value());
                }

            }
        } else { //tax details can be missing for Beneficiary role - default to NONE
            investorDetails.setTFNRegistration(TFNRegistrationType.NONE.value());
        }
    }

    private void populateOverseasCRSTaxRelatedFields(InvolvedPartyDetailsType investorDetails, ICrsTaxDetailsForm crsTaxDetailsForm) {

        if(CollectionUtils.isNotEmpty(crsTaxDetailsForm.getOverseasTaxDetails())){
            final List<ForeignCountryForTaxationType> overseasTaxDetailsForms = Lambda.convert(crsTaxDetailsForm.getOverseasTaxDetails(), new Converter<IOverseasTaxDetailsForm, ForeignCountryForTaxationType>() {
                @Override
                public ForeignCountryForTaxationType convert(IOverseasTaxDetailsForm overseasTaxDetailsForm) {
                    ForeignCountryForTaxationType foreignCountryForTaxationType = new ForeignCountryForTaxationType();
                    if(TIN_PROVIDED.equals(overseasTaxDetailsForm.getTINOption())){
                        foreignCountryForTaxationType.setTIN(overseasTaxDetailsForm.getTIN());
                    } else if(EXEMPTION_REASON_PROVIDED.equals(overseasTaxDetailsForm.getTINOption())){
                        foreignCountryForTaxationType.setReasonForTaxIdentificationNumberExemption(getOverseasExemptionMapping(overseasTaxDetailsForm.getTINExemptionReason()));
                    }
                    foreignCountryForTaxationType.setCountryCode(overseasTaxDetailsForm.getOverseasTaxCountry());
                    return foreignCountryForTaxationType;
                }
            });
            investorDetails.setForeignCountriesForTaxation(getForeignCountryTaxInfo(overseasTaxDetailsForms));
        }
    }

    private ForeignCountriesForTaxationType getForeignCountryTaxInfo(List<ForeignCountryForTaxationType>overseasTaxDetailsForms) {
        ForeignCountriesForTaxationType foreignCountriesForTaxationType = new ForeignCountriesForTaxationType();
        foreignCountriesForTaxationType.getForeignCountryForTaxation().addAll(overseasTaxDetailsForms);
        return foreignCountriesForTaxationType;
    }



    private ReasonForTaxIdentificationNumberExemptionType getOverseasExemptionMapping(String tinExemptionReason) {
        switch (tinExemptionReason) {
            case "btfg$under_aged":
                return ReasonForTaxIdentificationNumberExemptionType.UNDER_AGE;
            case "btfg$tin_never_iss":
                return ReasonForTaxIdentificationNumberExemptionType.TIN_NEVER_ISSUED;
            case "btfg$tin_pend":
                return ReasonForTaxIdentificationNumberExemptionType.TIN_PENDING;
            default:
                throw new IllegalArgumentException("Cannot map TFN Exemption Reason for Overseas Tax Details" + tinExemptionReason);
        }
    }

    private void populateCountryOfResidence(InvolvedPartyDetailsType investorDetails, ICrsTaxDetailsForm crsTaxDetailsForm) {
        String countryForTax = "AU";
        List<IOverseasTaxDetailsForm> overseasTaxDetails = crsTaxDetailsForm.getOverseasTaxDetails();
        boolean populate = false;
        if (CollectionUtils.isNotEmpty(overseasTaxDetails)) {
            if (crsTaxDetailsForm instanceof ShareholderMemberDetailsForm) {
                populate = null != ((ShareholderMemberDetailsForm) crsTaxDetailsForm).getAdditionalShareholder().getTaxdetails()
                    && !(((ShareholderMemberDetailsForm) crsTaxDetailsForm).getAdditionalShareholder()).getTaxdetails().getAusTaxDetails().getIsTaxCountryAus().getValue();
            } else {
                populate = !crsTaxDetailsForm.getAustralianTaxDetails().isTaxCountryAustralia();
            }
        }
        if (populate) {
            countryForTax = crsTaxDetailsForm.hasOverseasTaxCountry() ? crsTaxDetailsForm.getOverseasTaxCountry()
                : overseasTaxDetails.get(0).getOverseasTaxCountry();
        }

        // Handled for Scenario: For Existing GCM Investors when the Is ForeignRegistered flag is populated as Null, need to set the appropriate country of residence
        if(crsTaxDetailsForm.hasOverseasTaxCountry() && CollectionUtils.isEmpty(overseasTaxDetails)){
            countryForTax = crsTaxDetailsForm.getOverseasTaxCountry();
        }
        investorDetails.setCountryOfResidenceForTax(upperCase(countryForTax));
    }
}
