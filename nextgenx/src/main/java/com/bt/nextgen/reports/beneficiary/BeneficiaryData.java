package com.bt.nextgen.reports.beneficiary;

import com.bt.nextgen.api.beneficiary.model.Beneficiary;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.beneficiary.RelationshipType;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by M035995 on 12/08/2016.
 */
public class BeneficiaryData {

    private Beneficiary beneficiary;

    private String nominationType;

    private String gender;

    private static final Integer SCALE = 2;

    public BeneficiaryData(final Beneficiary beneficiary, final StaticIntegrationService staticIntegrationService) {
        this.beneficiary = beneficiary;
        Code code = staticIntegrationService.loadCodeByAvaloqId(CodeCategory.SUPER_NOMINATION_TYPE, beneficiary.getNominationType(),
                new ServiceErrorsImpl());
        nominationType = code != null ? code.getField("btfg$ui_name").getValue() : null;
        if (beneficiary.getGender() != null) {
            code = staticIntegrationService.loadCodeByAvaloqId(CodeCategory.GENDER, beneficiary.getGender(), new ServiceErrorsImpl());
            gender = code != null ? code.getName() : null;
        }
    }

    public String getNominationType() {
        return nominationType;
    }

    public String getAllocationPercent() {
        return ReportFormatter.format(ReportFormat.PERCENTAGE, true, (new BigDecimal(beneficiary.getAllocationPercent()).
                setScale(SCALE, RoundingMode.HALF_UP)).divide(new BigDecimal("100")));
    }

    public String getRelationshipType() {
        return beneficiary.getRelationshipType() != null ? RelationshipType.findByAvaloqId(beneficiary.getRelationshipType()).getName() : null;
    }

    public String getFirstName() {
        return beneficiary.getFirstName();
    }

    public String getLastName() {
        return beneficiary.getLastName();
    }

    public String getGender() {
        return gender;
    }

    public String getPhoneNumber() {
        return ReportFormatter.formatTelephoneNumber(beneficiary.getPhoneNumber());
    }

    public String getEmail() {
        return beneficiary.getEmail();
    }

    public String getDateOfBirth() {
        return beneficiary.getDateOfBirth();
    }

    public Boolean isPrimary() {
        return getNominationType().equalsIgnoreCase(Beneficiary.NOMINATION_TYPE_AUTO_REVISIONARY);
    }

}
