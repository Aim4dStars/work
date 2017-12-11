package util;

import java.util.HashMap;
import java.util.Map;

import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.btfin.panorama.core.conversion.CodeCategory;

public class StaticCodeCategories
{

	static Map <String, CodeImpl> codeCategoryMap = new HashMap <>();

	static
	{
		codeCategoryMap.put(CodeCategory.ACCOUNT_STRUCTURE_TYPE + "20613", new CodeImpl("20613", "C", "Company", "btfg$company"));
		codeCategoryMap.put(CodeCategory.ACCOUNT_STRUCTURE_TYPE + "20611", new CodeImpl("20611", "I", "Individual", "btfg$indvl"));
		codeCategoryMap.put(CodeCategory.ACCOUNT_STRUCTURE_TYPE + "20612", new CodeImpl("20612", "J", "Joint", "btfg$acc"));
		codeCategoryMap.put(CodeCategory.ACCOUNT_STRUCTURE_TYPE + "20615", new CodeImpl("20615", "S", "SMSF", "btfg$smsf"));
		codeCategoryMap.put(CodeCategory.ACCOUNT_STRUCTURE_TYPE + "20614", new CodeImpl("20614", "T", "Trust", "btfg$trust"));

		codeCategoryMap.put(CodeCategory.CAPITAL_TAX_TAX + "4", new CodeImpl("4",
			"MIN_GAIN",
			"Minimum Gain / Maximum Loss",
			"min_gain"));
		codeCategoryMap.put(CodeCategory.CAPITAL_TAX_TAX + "5", new CodeImpl("5",
			"MAX_GAIN",
			"Maximum Gain / Minimum Loss",
			"max_gain"));
		codeCategoryMap.put(CodeCategory.PERSON_ASSOCIATION + "5021", new CodeImpl("5021",
			"SECRETARY",
			"Secretary",
			"btfg$secretary"));

	}

	public static CodeImpl retrieveCodeCategoryMap(String key)
	{
		return codeCategoryMap.get(key);
	}

}
