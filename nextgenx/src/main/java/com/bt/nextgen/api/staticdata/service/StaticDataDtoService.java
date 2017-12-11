package com.bt.nextgen.api.staticdata.service;

import com.bt.nextgen.api.staticdata.model.StaticCodeDto;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;

public interface StaticDataDtoService extends SearchByCriteriaDtoService <StaticCodeDto>
{
	static final String COUNTRY_LIST = "countries";
	static final String AUSTRALIN_STATES = "states";
	static final String TAX_OPTIONS = "taxoptions";
	static final String TAX_EXEMPTION_REASONS = "taxexemptionreasons";
}
