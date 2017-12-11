package com.bt.nextgen.api.corporateaction.v1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.bt.nextgen.config.JsonViews;


public class CorporateActionBuyBackAccountElectionsDtoImpl implements CorporateActionAccountElectionsDto {
	@JsonView(JsonViews.Write.class)
	private List<CorporateActionAccountElectionDto> options;

	@JsonView(JsonViews.Write.class)
	private Integer minimumPriceId;

	public CorporateActionBuyBackAccountElectionsDtoImpl(List<CorporateActionAccountElectionDto> options, Integer minimumPriceId) {
		this.options = options;
		this.minimumPriceId = minimumPriceId;
	}

	public static CorporateActionBuyBackAccountElectionsDtoImpl createSingleAccountElection(Integer electionId, BigDecimal units,
																							Integer minimumPriceId) {
		List<CorporateActionAccountElectionDto> options = new ArrayList<>();
		options.add(new CorporateActionBuyBackAccountElectionDtoImpl(electionId, units));

		return new CorporateActionBuyBackAccountElectionsDtoImpl(options, minimumPriceId);
	}

	@Override
	public List<CorporateActionAccountElectionDto> getOptions() {
		return options;
	}

	public Integer getMinimumPriceId() {
		return minimumPriceId;
	}

	@JsonIgnore
	@Override
	public CorporateActionAccountElectionDto getPrimaryAccountElection() {
		return getOptions().get(0);
	}
}
