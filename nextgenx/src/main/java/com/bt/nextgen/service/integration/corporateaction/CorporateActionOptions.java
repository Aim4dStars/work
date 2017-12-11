package com.bt.nextgen.service.integration.corporateaction;

import java.math.BigDecimal;
import java.util.Map;

public class CorporateActionOptions<T> {
	private Map<T, String> options;

	public CorporateActionOptions(Map<T, String> options) {
		this.options = options;
	}

	public boolean hasValue(T key) {
		return options.get(key) != null;
	}

	public String getString(T key) {
		return options.get(key);
	}

	public int getInt(T key) {
		String value = options.get(key);

		return value != null ? Integer.parseInt(value) : 0;
	}

	public Integer getInteger(T key) {
		String value = options.get(key);

		return value != null ? Integer.valueOf(value) : null;
	}

	public BigDecimal getBigDecimal(T key) {
		String value = options.get(key);

		return value != null ? new BigDecimal(value) : null;
	}

	public boolean isEmpty() {
		return options == null || options.isEmpty();
	}
}
