package com.bt.nextgen.service.integration.corporateaction;

import java.math.BigDecimal;

public interface CorporateActionOption {
	String getKey();

	String getValue();

	boolean hasValue();

	BigDecimal getBigDecimalValue();
}
