package com.bt.nextgen.api.beneficiary.report;

import com.bt.nextgen.api.beneficiary.model.BeneficiaryCsvDto;
import com.bt.nextgen.api.beneficiary.model.BeneficiaryDto;
import com.bt.nextgen.api.beneficiary.service.BeneficiaryDtoService;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.core.reporting.BaseReportV2;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.account.PensionAccountDetailImpl;
import com.bt.nextgen.service.avaloq.account.PensionType;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.account.WrapAccount;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.*;

/*
* Creating adviser beneficiary CSV report
* */
@Report("adviserBeneficiaryCsvReport")
@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_intermediary_reports')")
public class AdviserBeneficiaryCsvReport extends BaseReportV2 {

    private static final String REPORT_NAME = "Panorama Beneficiary List";
    private static final String REPORT_FILE_NAME = "Beneficiary list";
    private static final String DISCLAIMER_CONTENT = "DS-IP-0146";
    private static final String DEFAULT_VALUE = "-";

    @Autowired
    private BeneficiaryDtoService beneficiaryDtoService;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private ContentDtoService contentService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    /**
     * @inheritDoc
     */
    @Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        return getBeneficiaryDetails(params);
    }

    private List<BeneficiaryCsvDto> getBeneficiaryDetails(Map<String, Object> params) {
        final ServiceErrors serviceErrors = new FailFastErrorsImpl();
        final String brokerId = (String) params.get("brokerid");
        final List<BeneficiaryDto> beneficiaryDtos = beneficiaryDtoService.getBeneficiaryDetails(brokerId, serviceErrors, null);
        return getBeneficiaryDetailsWithAccounts(beneficiaryDtos, serviceErrors);
    }

    private List<BeneficiaryCsvDto> getBeneficiaryDetailsWithAccounts(List<BeneficiaryDto> beneficiaryDtos, ServiceErrors serviceErrors) {
        List<BeneficiaryCsvDto> beneficiaryCsvDtos = new ArrayList<>();
        for (BeneficiaryDto beneficiaryDto : beneficiaryDtos) {
            final BeneficiaryCsvDto beneficiaryCsvDto = new BeneficiaryCsvDto();
            final String accountId = new ConsistentEncodedString(beneficiaryDto.getKey().getAccountId()).plainText();
            final WrapAccount wrapAccount = getAccount(accountId, serviceErrors);
            if (AccountStatus.ACTIVE.equals(wrapAccount.getAccountStatus())) {
                beneficiaryCsvDto.setAccountName(wrapAccount.getAccountName());
                beneficiaryCsvDto.setAccountNumber(wrapAccount.getAccountNumber());
                beneficiaryCsvDto.setAccountType(getAccountType(wrapAccount));
                beneficiaryCsvDto.setDateRegistered(getFormatedDate(wrapAccount.getOpenDate()));
                beneficiaryCsvDto.setNumberOfBeneficiaries(CollectionUtils.isNotEmpty(beneficiaryDto.getBeneficiaries()) ?
                        String.valueOf(beneficiaryDto.getBeneficiaries().size()) : "0");
                beneficiaryCsvDto.setLastUpdateDate(getFormatedDate(beneficiaryDto.getBeneficiariesLastUpdatedTime()));
                beneficiaryCsvDtos.add(beneficiaryCsvDto);
            }
        }
        Collections.sort(beneficiaryCsvDtos, new Comparator<BeneficiaryCsvDto>() {
            @Override
            public int compare(BeneficiaryCsvDto o1, BeneficiaryCsvDto o2) {
                return o1.getAccountName().compareTo(o2.getAccountName());
            }
        });
        return beneficiaryCsvDtos;
    }

    private String getFormatedDate(DateTime date) {
        if (date != null) {
            return date.toString(DateTimeFormat.forPattern("dd/MM/yyyy"));
        }
        return null;
    }

    @Override
    public String getReportFileName(Collection<?> data) {
        return REPORT_FILE_NAME;
    }

    private WrapAccount getAccount(String accountId, ServiceErrors serviceErrors) {
        return accountIntegrationService.loadWrapAccountWithoutContainers(AccountKey.valueOf(accountId), serviceErrors);
    }

    private String getAccountType(WrapAccount wrapAccount) {
        String accountType = wrapAccount.getAccountStructureType().name();
        if (AccountStructureType.SUPER.equals(wrapAccount.getAccountStructureType())) {
            accountType = "Super";
            if (wrapAccount instanceof PensionAccountDetailImpl) {
                if (PensionType.TTR.equals(((PensionAccountDetailImpl) wrapAccount).getPensionType())) {
                    accountType = PensionType.TTR.getLabel();
                } else if (PensionType.TTR_RETIR_PHASE.equals(((PensionAccountDetailImpl) wrapAccount).getPensionType())) {
                    accountType = PensionType.TTR_RETIR_PHASE.getLabel();
                } else {
                    accountType = PensionType.STANDARD.getLabel();
                }
            }
        }
        return accountType;
    }

    @ReportBean("reportType")
    public String getReportType(Map<String, Object> params) {
        return REPORT_NAME;
    }

    /**
     * Retrieves the adviser name of the associated fnumber for which the policies are displayed in csv
     *
     * @param params - input/output value of the report
     * @return
     */
    @ReportBean("adviserName")
    public String retrieveAdviserName(Map<String, Object> params) {
        final ServiceErrors serviceErrors = new FailFastErrorsImpl();
        final String brokerId = (String) params.get("brokerid");
        BrokerUser brokerUser = null;
        if (brokerId != null) {
            final String plaintextBrokerId = new ConsistentEncodedString(brokerId).plainText();
            brokerUser = brokerIntegrationService.getAdviserBrokerUser(BrokerKey.valueOf(plaintextBrokerId), serviceErrors);
        } else {
            brokerUser = brokerIntegrationService.getBrokerUser(userProfileService.getActiveProfile(), serviceErrors);
        }
        return brokerUser.getFullName();
    }

    /**
     * Retrieves the disclaimer text for the content id.
     *
     * @param params input/output parameters
     * @return disclaimer text
     */
    @ReportBean("disclaimer")
    public String getDisclaimer(Map<String, Object> params) {
        final ServiceErrors serviceErrors = new FailFastErrorsImpl();
        final ContentKey key = new ContentKey(DISCLAIMER_CONTENT);
        final ContentDto content = contentService.find(key, serviceErrors);
        return content != null ? content.getContent() : "";
    }

    @ReportBean("defaultValue")
    public String getDefaultValue(Map<String, Object> params) {
        return DEFAULT_VALUE;
    }
}
