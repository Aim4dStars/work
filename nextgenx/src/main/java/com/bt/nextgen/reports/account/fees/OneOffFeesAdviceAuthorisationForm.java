package com.bt.nextgen.reports.account.fees;


import com.bt.nextgen.api.account.v1.service.WrapAccountDetailDtoService;
import com.bt.nextgen.api.fees.model.OneOffFeesDto;
import com.bt.nextgen.api.fees.service.OneOffFeesDtoService;
import com.bt.nextgen.api.fees.validation.OneOffFeesDtoErrorMapper;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.reports.account.AccountReport;
import com.bt.nextgen.service.integration.account.AccountKey;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Report("oneOffFeesAdviceAuthorisationForm")
public class OneOffFeesAdviceAuthorisationForm extends AccountReport{

    @Autowired
    private WrapAccountDetailDtoService wrapAccountDetailDtoService;


    @Autowired
    private OneOffFeesDtoService adviceFeesDtoService;

    @Autowired
    private OneOffFeesDtoErrorMapper oneOffAdviceFeesDtoErrorMapper;

    @ReportBean("reportType")
    public String getReportName(Map<String, String> params)
    {
        return "Client authorisation - advice fees";
    }


    @ReportBean("oneOffAdviceFees")
    public OneOffFeesDto retrieveOneOffAdviceFees(Map <String, String> params)
    {

        String accId = params.get("account-id");
        String feesAmount = params.get("feesAmount");
        String description = params.get("description");

        OneOffFeesDto oneOffFeesDto = new OneOffFeesDto();
        AccountKey key = AccountKey.valueOf(accId);
        oneOffFeesDto.setKey(key);
        if(description!=null && !"".equals(description))
            oneOffFeesDto.setDescription(description);
        else
            oneOffFeesDto.setDescription("-");
        oneOffFeesDto.setFeesAmount(new BigDecimal(feesAmount));

        List<OneOffFeesDto> resultList = new ArrayList<OneOffFeesDto>();
        resultList.add(oneOffFeesDto);

        return oneOffFeesDto;
    }
}

