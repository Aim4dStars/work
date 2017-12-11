package com.bt.nextgen.api.termdeposit.service;

import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.avaloq.AvaloqUtils;
import com.bt.nextgen.service.integration.asset.PaymentFrequency;
import com.bt.nextgen.service.integration.asset.Term;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TermDepositRateCalculatorCsvUtils {
    private static final Logger logger = LoggerFactory.getLogger(TermDepositRateCalculatorCsvUtils.class);
    private static final String CSV_COMMON_HEADER = "Bank,Term,";
    private static final String MATURITY_YEARLY_HEADER = "Maturity/Yearly rates";
    private static final String MONTHLY_HEADER = "Monthly rates";
    private static final String COMMA = ",";
    private static final String EMPTY_STRING = "";
    private static final String NAME_SUFFIX = "_name";

    @Autowired
    private CmsService cmsService;

    /**
     * Method to get the term deposit rates as string where each column is separated by comma(,) and each row is
     * separated by '\n'
     */
    public String getTermDepositRatesCsv(String brand, List<TermDepositInterestRate> termDepositInterestRates) {
        StringBuilder sb = new StringBuilder();
        sb.append(cmsService.getContent(brand + "_tdDisclaimer"));
        logger.info("TermDepositControllerUtil.toBankTermDepositModel(): Bank Id: {}", brand);
        Map<Term, Map<String, String>> maturityOrYearlyMap = new TreeMap<>();
        Map<Term, Map<String, String>> monthlyMap = new TreeMap<>();
        for (TermDepositInterestRate interestRate : termDepositInterestRates) {
            if (brand.equalsIgnoreCase(interestRate.getIssuerId())) {
                if (interestRate.getPaymentFrequency().equals(PaymentFrequency.AT_MATURITY) || interestRate
                        .getPaymentFrequency().equals(PaymentFrequency.ANNUALLY))
                {
                    addRate(maturityOrYearlyMap, interestRate);
                } else if (interestRate.getPaymentFrequency().equals(PaymentFrequency.MONTHLY)) {
                    addRate(monthlyMap, interestRate);
                }
            }
        }
        sb.append(MATURITY_YEARLY_HEADER);
        sb.append("\n");
        sb.append(getRatesCsv(brand, maturityOrYearlyMap));
        sb.append("\n");
        sb.append(MONTHLY_HEADER);
        sb.append("\n");
        sb.append(getRatesCsv(brand, monthlyMap));
        sb.append("\n");
        sb.append("\n");
        sb.append(cmsService.getContent(brand + "_tdFooter"));
        return sb.toString();
    }

    public Map<Term, Map<String, String>> addRate(Map<Term, Map<String, String>> map,
            TermDepositInterestRate interestRate) {
        if (interestRate == null) {
            return map;
        }
        Map<Term, Map<String, String>> result = map;
        if (map == null) {
            result = new HashMap<>();
        }
        Map<String, String> rangeAndRateMap = result.get(interestRate.getTerm());
        rangeAndRateMap = addRangeAndRates(rangeAndRateMap, interestRate);
        result.put(interestRate.getTerm(), rangeAndRateMap);
        return result;
    }

    public Map<String, String> addRangeAndRates(Map<String, String> map, TermDepositInterestRate interestRate) {
        if (interestRate == null) {
            return map;
        }
        Map<String, String> result = map;
        if (map == null) {
            result = new HashMap<>();
        }
        //        rate.setScale(2, BigDecimal.ROUND_FLOOR).toString();
        String rangeKey = interestRate.getLowerLimit().setScale(0).toString() + "-" + interestRate.getUpperLimit()
                .setScale(0).toString();
        result.put(rangeKey, AvaloqUtils.asAvaloqRate(interestRate.getRate()));
        return result;
    }

    /**
     * Method to get the maturity + yearly and monthly rates and creating the string of it.
     */
    public String getRatesCsv(String bankId, Map<Term, Map<String, String>> dataMap) {
        StringBuilder sb = new StringBuilder();
        Set<String> rangeSet = createRangeSet(dataMap);
        createHeader(rangeSet, sb);
        for (Term term : dataMap.keySet()) {
            sb.append(cmsService.getContent(bankId + NAME_SUFFIX));
            sb.append(COMMA);
            sb.append(term.getMonths());
            Map<String, String> rateMap = dataMap.get(term);
            if(rateMap != null && !rateMap.isEmpty()){
                List<String> arrangeRates = arrangeRates(rangeSet, rateMap);
                for (String rate : arrangeRates) {
                    sb.append(COMMA);
                    if (rate != null) {
                        sb.append(rate);
                    } else {
                        sb.append(EMPTY_STRING);
                    }
                }
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Method to match the header and values.
     * It just follows the sequence of the range and fetch values accordingly.
     */
    private static List<String> arrangeRates(Set<String> rangeSet, Map<String, String> rateMap) {
        List<String> rangeList = new ArrayList<>();
        Iterator<String> it = rangeSet.iterator();
        while (it.hasNext()) {
            rangeList.add(rateMap.get(it.next()));
        }
        return rangeList;
    }

    /**
     * Method to create the header of range-values
     */
    private static StringBuilder createHeader(Set<String> rangeSet, StringBuilder sb) {
        sb.append(CSV_COMMON_HEADER);
        for (String range : rangeSet) {
            sb.append(range);
            sb.append(COMMA);
        }
        sb.append("\n");
        return sb;
    }

    /**
     * Method to create the set of range values.
     */
    private static Set<String> createRangeSet(Map<Term, Map<String, String>> dataMap) {
        Set<String> rangeSet = new LinkedHashSet<>();
        for (Map<String, String> rangeValuesAndRates : dataMap.values()) {
            rangeSet.addAll(rangeValuesAndRates.keySet());
        }
        return rangeSet;
    }
}
