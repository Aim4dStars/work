package com.bt.nextgen.api.draftaccount.service;

import ch.lambdaj.Lambda;
import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.selectFirst;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.client.model.InvestorDto;
import com.bt.nextgen.api.client.model.RegisteredEntityDto;
import com.bt.nextgen.api.client.model.TaxResidenceCountriesDto;
import com.bt.nextgen.api.client.util.ClientTxnDtoConverter;
import com.bt.nextgen.api.draftaccount.builder.v3.TINExemptionEnum;
import com.bt.nextgen.api.draftaccount.model.form.ICrsTaxDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IOrganisationForm;
import com.bt.nextgen.api.draftaccount.model.form.IOverseasTaxDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.v1.OverseasTaxDetailsForm;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.service.avaloq.domain.CountryNameConverter;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.panorama.core.security.integration.domain.InvestorDetail;
import com.bt.nextgen.service.integration.domain.Organisation;
import com.bt.nextgen.service.integration.domain.PersonDetail;
import com.bt.nextgen.service.integration.userinformation.TaxResidenceCountry;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.btfin.panorama.core.util.StringUtil.isNotNullorEmpty;

/**
 * Created by L069552 on 9/03/17.
 */
@Service
@Transactional
public class CRSTaxDetailHelperServiceImpl implements CRSTaxDetailHelperService {

    private static final String AUSTRALIA = "Australia";

    @Autowired
    private CountryNameConverter countryNameConverter;

    @Autowired
    private StaticIntegrationService staticService;


    @Override
    public void populateCRSTaxDetailsForOrganization(Organisation organisation,RegisteredEntityDto registeredEntityDto){
        if(organisation.getResiCountryForTax() != null){
            registeredEntityDto.setResiCountryforTax(organisation.getResiCountryForTax());
        }
        if(CollectionUtils.isNotEmpty(organisation.getTaxResidenceCountries())){
            List<TaxResidenceCountry> filteredTaxResidenceCountries = ClientTxnDtoConverter.filterTaxResidenceCountriesByValidity(organisation.getTaxResidenceCountries());
            registeredEntityDto.setTaxResidenceCountries(getTaxResidenceCountriesForApplicationDocument(filteredTaxResidenceCountries));
        }
        registeredEntityDto.setOverseasTaxResident(getOverseasTaxResidentValue(organisation));
    }

    @Override
    public void populateCRSTaxDetailsForOrganization(IOrganisationForm organisationForm,RegisteredEntityDto registeredEntityDto){

        ICrsTaxDetailsForm crsTaxDetailsForm = organisationForm.getCrsTaxDetails();

            registeredEntityDto.setResiCountryforTax(AUSTRALIA);            //Default Country for residence is Australia for Organisational Entities
            if(isOverseasTaxDetailsPresent(crsTaxDetailsForm)){
                registeredEntityDto.setTaxResidenceCountries(getTaxResidenceCountriesForClientApplication(crsTaxDetailsForm.getOverseasTaxDetails()));
            }
            registeredEntityDto.setOverseasTaxResident(isOverseasTaxDetailsPresent(crsTaxDetailsForm));

    }

    @Override
    public void populateCRSTaxDetailsForIndividual(IPersonDetailsForm personDetailsForm,InvestorDto investorDto) {
        investorDto.setResiCountryforTax(getCountryOfResidence(personDetailsForm));
        if (isOverseasTaxDetailsPresent(personDetailsForm)) {
            investorDto.setTaxResidenceCountries(getTaxResidenceCountriesForClientApplication(personDetailsForm.getOverseasTaxDetails()));
        }

        investorDto.setOverseasTaxResident(personDetailsForm.getIsOverseasTaxRes());

    }

    @Override
    public void populateCRSTaxDetailsForIndividual(PersonDetail personDetail,InvestorDto investorDto, boolean isExistingUser, Map<String,Boolean> cisKeysToOverseasDetail,
    String cisKey) {
        investorDto.setResiCountryforTax(personDetail.getResiCountryForTax());
        if (CollectionUtils.isNotEmpty(personDetail.getTaxResidenceCountries())) {
            List<TaxResidenceCountry> filteredTaxResidenceCountries = ClientTxnDtoConverter.filterTaxResidenceCountriesByValidity(personDetail.getTaxResidenceCountries());
            investorDto.setTaxResidenceCountries(getTaxResidenceCountriesForApplicationDocument(filteredTaxResidenceCountries));
        }
        if (!isExistingUser) {
            investorDto.setOverseasTaxResident(getOverseasTaxResidentValue(personDetail));
        }else{
            if(null != cisKeysToOverseasDetail && !cisKeysToOverseasDetail.isEmpty() &&  CollectionUtils.isEmpty(personDetail.getTaxResidenceCountries())){
                investorDto.setOverseasTaxResident(cisKeysToOverseasDetail.get(cisKey));
            }
        }
    }

    private String getCountryOfResidence(ICrsTaxDetailsForm crsTaxDetailsForm) {
        String countryForTax = AUSTRALIA;

        List<IOverseasTaxDetailsForm> overseasTaxDetails = crsTaxDetailsForm.getOverseasTaxDetails();
        boolean isOverseasCountrySelected = false;
        if (CollectionUtils.isNotEmpty(overseasTaxDetails)) {
                isOverseasCountrySelected = !crsTaxDetailsForm.getAustralianTaxDetails().isTaxCountryAustralia();
        }

        // Handle to populate the country of residence when the selected overseasCountry is populated but the overseas country list is null
        // Scenario would happen when isForeignRegistered flag is Null
        if(CollectionUtils.isEmpty(overseasTaxDetails) && isNotNullorEmpty(crsTaxDetailsForm.getOverseasTaxCountry())){
            countryForTax = crsTaxDetailsForm.getOverseasTaxCountry();
            isOverseasCountrySelected = true;
        }

        if(isOverseasCountrySelected){
            countryForTax = crsTaxDetailsForm.hasOverseasTaxCountry() ? crsTaxDetailsForm.getOverseasTaxCountry()
                    : overseasTaxDetails.get(0).getOverseasTaxCountry();

            Code countryNameCode = staticService.loadCodeByUserId(CodeCategory.COUNTRY, countryForTax, new FailFastErrorsImpl());
            return countryNameCode != null ? countryNameCode.getName() : countryForTax;

        }
        return countryForTax;
    }


    private  boolean isOverseasTaxDetailsPresent(ICrsTaxDetailsForm crsTaxDetailsForm){
        return
                CollectionUtils.isNotEmpty(crsTaxDetailsForm.getOverseasTaxDetails());
    }

    private boolean getOverseasTaxResidentValue(InvestorDetail investorDetail){
        return
                CollectionUtils.isNotEmpty(investorDetail.getTaxResidenceCountries());
    }

    private List<TaxResidenceCountriesDto> getTaxResidenceCountriesForApplicationDocument(List<TaxResidenceCountry> taxResidenceCountries) {
        List<TaxResidenceCountriesDto> taxResidenceCountriesDtoList = Lambda.convert(taxResidenceCountries, new Converter<TaxResidenceCountry, TaxResidenceCountriesDto>() {

                    @Override
                    public TaxResidenceCountriesDto convert(TaxResidenceCountry taxResidenceCountry) {
                        TaxResidenceCountriesDto taxResidenceCountriesDto = new TaxResidenceCountriesDto();
                        taxResidenceCountriesDto.setTaxResidenceCountry(taxResidenceCountry.getCountryName());
                        taxResidenceCountriesDto.setTaxExemptionReason(taxResidenceCountry.getTinExemption());
                        taxResidenceCountriesDto.setTin(taxResidenceCountry.getTin());
                        return taxResidenceCountriesDto;
                    }
        });
        return taxResidenceCountriesDtoList;
    }

    private List<TaxResidenceCountriesDto> getTaxResidenceCountriesForClientApplication(List<IOverseasTaxDetailsForm> overseasTaxDetailsForms){

        List<TaxResidenceCountriesDto> taxResidenceCountriesDtoList = Lambda.convert(overseasTaxDetailsForms,new Converter<OverseasTaxDetailsForm,TaxResidenceCountriesDto>(){

            @Override
            public TaxResidenceCountriesDto convert(OverseasTaxDetailsForm overseasTaxDetailsForm) {
                TaxResidenceCountriesDto taxResidenceCountriesDto = new TaxResidenceCountriesDto();
                Code countryNameCode = staticService.loadCodeByUserId(CodeCategory.COUNTRY, overseasTaxDetailsForm.getOverseasTaxCountry(), new FailFastErrorsImpl());
                taxResidenceCountriesDto.setTaxResidenceCountry(countryNameCode != null ? countryNameCode.getName() : overseasTaxDetailsForm.getOverseasTaxCountry());
                if(StringUtils.isNotBlank(overseasTaxDetailsForm.getTIN())){
                    taxResidenceCountriesDto.setTaxExemptionReason(getTinExemptionReason(TINExemptionEnum.TAX_IDENTIFICATION_NUMBER.getExemption()));
                }else{
                    taxResidenceCountriesDto.setTaxExemptionReason(getTinExemptionReason(overseasTaxDetailsForm.getTINExemptionReason()));
                }
                taxResidenceCountriesDto.setTin(overseasTaxDetailsForm.getTIN());
                return taxResidenceCountriesDto;
            }
        });

        return taxResidenceCountriesDtoList;
    }

    private String getTinExemptionReason(final String uiTinExemptionReason){

        Collection<Code> codes = staticService.loadCodes(CodeCategory.TIN_EXEMPTION_REASONS, new FailFastErrorsImpl());
        Code code = selectFirst(codes, new LambdaMatcher<Code>() {
            @Override
            protected boolean matchesSafely(Code code) {
                return code.getIntlId().equals(uiTinExemptionReason);
            }
        });
        return code != null ? code.getName() : uiTinExemptionReason;
    }

}