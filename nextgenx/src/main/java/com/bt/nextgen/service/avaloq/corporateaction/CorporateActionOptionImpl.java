package com.bt.nextgen.service.avaloq.corporateaction;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

@ServiceBean(xpath = "par_list")
public class CorporateActionOptionImpl implements CorporateActionOption {
	@ServiceElement(xpath = "key/val")
	private String key;

	@ServiceElement(xpath = "val/val")
	private String value;

	public CorporateActionOptionImpl() {
	}

	public CorporateActionOptionImpl(String key, String value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public boolean hasValue() {
		return StringUtils.isNotBlank(value);
	}

	@Override
	public BigDecimal getBigDecimalValue() {
		return value != null ? new BigDecimal(value) : null;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
