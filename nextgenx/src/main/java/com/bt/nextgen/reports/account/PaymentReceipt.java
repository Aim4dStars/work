package com.bt.nextgen.reports.account;

import com.bt.nextgen.api.account.v1.model.PaymentDto;
import com.bt.nextgen.api.account.v1.service.PaymentDtoService;
import com.bt.nextgen.api.cashcategorisation.model.CategorisableCashTransactionDto;
import com.bt.nextgen.api.cashcategorisation.model.CategorisedTransactionDto;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.core.reporting.stereotype.ReportImage;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.web.ApiFormatter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.Renderable;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Report("paymentPdfReceipt")
@SuppressWarnings({"findbugs:DLS_DEAD_LOCAL_STORE","squid:S1172","squid:S1160"})
public class PaymentReceipt extends AccountReport
{
    @Autowired
    private PaymentDtoService paymentService;

    @ReportBean("cashCategorisationDto")
    public CategorisableCashTransactionDto getSubCategory(Map <String, String> params)
    {
        String categoryLength = params.get("categorisationLength");
        CategorisableCashTransactionDto categoryDto = null;
        if (StringUtils.isNotBlank(categoryLength)) {
            int categorisationLength = Integer.parseInt(categoryLength);
            if (categorisationLength>0) {
                categoryDto = new CategorisableCashTransactionDto();
                String categoryLevel = params.get("categoryLevel"); //if member?, Member
                if ("member".equalsIgnoreCase(categoryLevel)) {
                    categoryLevel = "Member";
                } else {
                    categoryLevel = "Sub-category";
                }
                String categoryLabel = params.get("categoryLabel");
                categoryDto.setCategorisationLevel(categoryLevel);
                categoryDto.setTransactionCategory(categoryLabel);
                //categoryDto.set
                String subcategoryLabel = "subcategory";
                String amountLabel = "amount";
                String label = "label";
                List<CategorisedTransactionDto> categorisedTransactionDtos = new ArrayList<>();
                categoryDto.setMemberContributionDtoList(categorisedTransactionDtos);
                for (int i = 0; i < categorisationLength; i++) {
                    CategorisedTransactionDto categorisedTransactionDto = new CategorisedTransactionDto();
                    String amountParameter = subcategoryLabel + i + amountLabel;
                    String labelParameter = subcategoryLabel + i + label;
                    BigDecimal amount = new BigDecimal(params.get(amountParameter));
                    categorisedTransactionDto.setAmount(amount);
                    categorisedTransactionDto.setContributionSubType(params.get(labelParameter));
                    categorisedTransactionDtos.add(categorisedTransactionDto);
                }
            }
        }
        return categoryDto;
    }

    @ReportBean("paymentReceipt")
    public PaymentDto getTransactions(Map <String, String> params)
    {


        String receiptNo = EncodedString.toPlainText(params.get("receiptNo")).toString();



        Map <String, PaymentDto> mapPayments = paymentService.loadPaymentReciepts();

        PaymentDto paymentObj = mapPayments.get(receiptNo);

        if (StringUtils.isNotBlank(paymentObj.getFrequency()))
        {

            if ((!"setDate".equals(paymentObj.getEndRepeat())) && (!"setNumber".equals(paymentObj.getEndRepeat())))
            {

                paymentObj.setRepeatEndDate("No end date");
            }
        }

        else
        {
            paymentObj.setRepeatEndDate("");
        }

        paymentObj.setUpdatedBsbFromPayDto(ApiFormatter.formatBsb(paymentObj.getFromPayDto().getCode()));
        paymentObj.setUpdatedBsbToPayteeDto(ApiFormatter.formatBsb(paymentObj.getToPayteeDto().getCode()));

        PaymentDto resultList = new PaymentDto();
        resultList = paymentObj;

        return resultList;
    }

    @ReportBean("reportType")
    public String getReportName(Map <String, String> params)
    {
        return "Payment Receipt";
    }

    @ReportImage("paymentFromToIcon")
    public Renderable getPaymentFromToIcon(Map <String, String> params) throws JRException, IOException
    {
        String paymentIcon = cmsService.getContent("paymentFromToIcon");
        return this.getRasterImage(paymentIcon);

    }

}