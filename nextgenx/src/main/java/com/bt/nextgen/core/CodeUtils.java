package com.bt.nextgen.core;

import com.avaloq.abs.bb.fld_def.IdFld;
import com.bt.nextgen.payments.domain.PaymentFrequency;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.avaloq.StaticCodeInterface;
import com.bt.nextgen.service.integration.code.Code;

import java.util.Collection;
import java.util.List;

public class CodeUtils
{
	public static final int MILLI_SECONDS = 60000;
	public static final int TIMECAP = 10;

	/*public static Map<String, List<StaticCode>> initCache(List<Tab> tabList)
	{
		Map<String, List<StaticCode>> codes = new HashMap<>();
		String codeCategory;
		List<Code> codeList;
		List<StaticCode> staticCodes;
		for (Tab tab : tabList)
		{
			codeCategory = tab.getHead().getTabName().getVal().getValue();
			staticCodes = new ArrayList<>();
			codes.put(codeCategory, staticCodes);
			codeList = tab.getCodeList().getCode();
			for (Code code : codeList)
			{
				staticCodes.add(new StaticCode(asString(code.getHead().getCodeId()), asString(code.getHead().getName()),
					asString(code.getHead().getIntlId())));
			}
		}
		return codes;
	}*/
	
	
	public static Code getCodeByName(Collection <Code> codes, String name)
	{

		for (Code staticCode : codes)
		{
			if (staticCode.getName().trim().equalsIgnoreCase(name.trim()))
			{
				return staticCode;
			}
		}
		return null;
	}

	public static StaticCodeInterface findCodeByName(List <StaticCodeInterface> codes, String name)
	{

		for (StaticCodeInterface staticCode : codes)
		{
			if (staticCode.getName().trim().equalsIgnoreCase(name.trim()))
			{
				return staticCode;
			}
		}
		return null;
	}

	public static StaticCodeInterface findCodeById(List <StaticCodeInterface> codes, String id)
	{

		for (StaticCodeInterface staticCode : codes)
		{
			if (staticCode.getId().trim().equals(id.trim()))
			{
				return staticCode;
			}
		}
		return null;
	}

	public static StaticCodeInterface findCodeByValue(List <StaticCodeInterface> codes, String value)
	{

		for (StaticCodeInterface staticCode : codes)
		{
			if (staticCode.getValue().trim().equals(value.trim()))
			{
				return staticCode;
			}
		}
		return null;
	}

	public static IdFld findFrequencyIdOf(List <StaticCodeInterface> codes, String frequency)
	{
		return AvaloqGatewayUtil.createIdVal(findCodeByValue(codes, PaymentFrequency.valueOf(frequency.toUpperCase()).getAvaloqKey()).getId());

	}

	public static boolean isMoreThanTenMins(long startTime)
	{
		double diff = System.currentTimeMillis() - startTime;
		if (diff / MILLI_SECONDS > TIMECAP)
		{
			return true;
		}
		return false;
	}
}
