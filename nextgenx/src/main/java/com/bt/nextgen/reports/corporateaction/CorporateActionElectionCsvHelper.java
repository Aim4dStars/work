package com.bt.nextgen.reports.corporateaction;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.fees.DollarFeesComponent;
import com.bt.nextgen.service.avaloq.fees.FeesComponents;
import com.bt.nextgen.service.avaloq.fees.FeesType;
import com.bt.nextgen.service.integration.fees.FeesSchedule;
import com.bt.nextgen.service.integration.fees.FeesScheduleIntegrationService;

@Service
public class CorporateActionElectionCsvHelper {
    @Autowired
    private FeesScheduleIntegrationService feesScheduleIntegrationService;

    private ServiceErrors serviceErrors = new FailFastErrorsImpl();

    public Map<String, BigDecimal> getOngoingAdviceFee(CorporateActionAccountDetailsDto account) {
        Map<String, BigDecimal> ongoingAdviceFeeMap = new LinkedHashMap<>();

        List<FeesSchedule> fees = feesScheduleIntegrationService.getFees(EncodedString.toPlainText(account.getAccountKey()), serviceErrors);

        if (fees != null && !fees.isEmpty()) {
            for (FeesSchedule feesSchedule : fees) {
                if (feesSchedule.getFeesType().equals(FeesType.ONGOING_FEE)) {
                    for (FeesComponents feeModel : feesSchedule.getFeesComponents()) {
                        if (feeModel instanceof DollarFeesComponent) {
                            ongoingAdviceFeeMap.put(account.getClientId(), ((DollarFeesComponent) feeModel).getDollar());
                        }
                    }
                }
            }
        }

        return ongoingAdviceFeeMap;
    }
}

