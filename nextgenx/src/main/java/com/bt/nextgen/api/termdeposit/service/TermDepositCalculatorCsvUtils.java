package com.bt.nextgen.api.termdeposit.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.avaloq.AvaloqUtils;
import com.bt.nextgen.service.integration.asset.PaymentFrequency;
import com.bt.nextgen.service.integration.asset.Term;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail.InterestRate;

public class TermDepositCalculatorCsvUtils 
{
	private static final Logger logger = LoggerFactory.getLogger(TermDepositCalculatorCsvUtils.class);
	private static final String CSV_COMMON_HEADER = "Bank,Term,";
	private static final String MATURITY_YEARLY_HEADER = "Maturity/Yearly rates";
	private static final String MONTHLY_HEADER = "Monthly rates";
	private static final String COMMA = ",";
	private static final String EMPTY_STRING = "";
	private static final String NAME_SUFFIX = "_name";

	/**
	 * Private constructor.
	 */
	private TermDepositCalculatorCsvUtils() {

	}

	/**
	 * Method to get the term deposit rates as string where each column is separated by comma(,) and each row is separated by '\n' 
	 * @param brand
	 * @param assetMap
	 * @param cmsService
	 * @return
	 */
	public static String getTermDepositRatesCsv(String brand, Map<String, TermDepositAssetDetail> assetMap, CmsService cmsService)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(cmsService.getContent(brand+"_tdDisclaimer"));
		logger.info("TermDepositControllerUtil.toBankTermDepositModel(): Bank Id: {}", brand);
		Map<Term, Map<String, String>> maturityOrYearlyMap = new TreeMap<>(); // Map to hold the maturity and yearly rates.
		Map<Term, Map<String, String>> monthlyMap = new TreeMap<>(); // Map to hold the monthly rates.
		for(TermDepositAssetDetail termDepositAsset : assetMap.values()) 
		{
			if(brand.equalsIgnoreCase(termDepositAsset.getIssuer()))
			{
				logger.info("Asset Id: "+termDepositAsset.getAssetId()+", Asset Name: "+termDepositAsset.getIssuer()+", Frequency: "+termDepositAsset.getPaymentFrequency());
				if(termDepositAsset.getPaymentFrequency().equals(PaymentFrequency.AT_MATURITY) || termDepositAsset.getPaymentFrequency().equals(PaymentFrequency.ANNUALLY))
				{
					maturityOrYearlyMap.put(termDepositAsset.getTerm(), getRangeAndRates(termDepositAsset));
				}
				else if(termDepositAsset.getPaymentFrequency().equals(PaymentFrequency.MONTHLY))
				{
					monthlyMap.put(termDepositAsset.getTerm(), getRangeAndRates(termDepositAsset));
				}
			}
		}
		sb.append(MATURITY_YEARLY_HEADER);
		sb.append("\n");
		sb.append(getRatesCsv(brand, maturityOrYearlyMap, cmsService));
		sb.append("\n");
		sb.append(MONTHLY_HEADER);
		sb.append("\n");
		sb.append(getRatesCsv(brand, monthlyMap, cmsService));
		sb.append("\n");
		sb.append("\n");
		sb.append(cmsService.getContent(brand+"_tdFooter"));
		return sb.toString();
	}

	/**
	 * Method to create a map to hold Range as the key and corresponding Rate as a value.
	 * @param termDepositAsset
	 * @return
	 */
	protected static Map<String, String> getRangeAndRates(TermDepositAssetDetail termDepositAsset)
	{
		Map<String, String> rangeAndRates = new LinkedHashMap<>();
		Map<String, InterestRate> tempRangeAndRates = new LinkedHashMap<>();
		for (InterestRate termRate : termDepositAsset.getInterestRates()) 
		{
			String rangeKey = termRate.getLowerLimit()+ "-"+ termRate.getUpperLimit();
			if(!rangeAndRates.containsKey(rangeKey))
			{
				rangeAndRates.put(termRate.getLowerLimit()+ "-"+ termRate.getUpperLimit(), AvaloqUtils.asAvaloqRate(termRate.getRate()));
				tempRangeAndRates.put(termRate.getLowerLimit()+ "-"+ termRate.getUpperLimit(), termRate);
			}
			else
			{	// compare the priority and get the rate of higher priority value.
				if(new BigDecimal(termRate.getPriority()).compareTo(new BigDecimal(tempRangeAndRates.get(rangeKey).getPriority())) == 1)
				{
					rangeAndRates.put(rangeKey, AvaloqUtils.asAvaloqRate(termRate.getRate()));
					tempRangeAndRates.put(rangeKey, termRate);
				}
			}
		}	
		logger.info("keys:"+rangeAndRates.keySet());
		logger.info("Values:"+rangeAndRates.values());
		return rangeAndRates;
	}

	/**
	 * Method to get the maturity + yearly and monthly rates and creating the string of it.
	 * @param bankId
	 * @param dataMap
	 * @param cmsService
	 * @return
	 */
	public static String getRatesCsv(String bankId, Map<Term, Map<String, String>> dataMap, CmsService cmsService) 
	{
		StringBuilder sb = new StringBuilder();
		Set<String> rangeSet = createRangeSet(dataMap);
		createHeader(rangeSet, sb);
		for (Term term : dataMap.keySet()) 
		{
			sb.append(cmsService.getContent(bankId + NAME_SUFFIX));
			sb.append(COMMA);
			sb.append(term.getMonths());
			Map<String, String> rateMap = dataMap.get(term);
			List<String> arrangeRates = arrangeRates(rangeSet, rateMap);
			for(String rate : arrangeRates)
			{
				sb.append(COMMA);
				if(rate != null)
				{
					sb.append(rate);
				}
				else
				{
					sb.append(EMPTY_STRING);
				}
			}
			sb.append("\n");
		}

		return sb.toString();
	}

	/**
	 * Method to match the header and values.
	 * It just follows the sequence of the range and fetch values accordingly.
	 * @param rangeSet
	 * @param rateMap
	 * @return
	 */
	private static List<String> arrangeRates(Set<String> rangeSet, Map<String, String> rateMap)
	{
		List<String> rangeList = new ArrayList<>();
		Iterator<String> it = rangeSet.iterator();
		while(it.hasNext())
		{
			rangeList.add(rateMap.get(it.next()));
		}

		return rangeList;
	}

	/**
	 * Method to create the header of range-values
	 * @param rangeSet
	 * @param sb
	 * @return
	 */
	private static StringBuilder createHeader(Set<String> rangeSet, StringBuilder sb) 
	{
		sb.append(CSV_COMMON_HEADER);
		for(String range : rangeSet)
		{
			sb.append(range);
			sb.append(COMMA);
		}
		sb.append("\n");

		return sb;
	}

	/**
	 * Method to create the set of range values.
	 * @param dataMap
	 * @param sb
	 * @return
	 */
	private static Set<String> createRangeSet(Map<Term, Map<String, String>> dataMap) 
	{
		Set<String> rangeSet = new LinkedHashSet<>();

		for (Map<String, String> rangeValuesAndRates : dataMap.values()) 
		{
			rangeSet.addAll(rangeValuesAndRates.keySet());
		}

		return rangeSet;
	}
}
