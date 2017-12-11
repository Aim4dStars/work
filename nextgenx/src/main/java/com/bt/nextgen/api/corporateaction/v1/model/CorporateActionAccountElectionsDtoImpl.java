package com.bt.nextgen.api.corporateaction.v1.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.bt.nextgen.config.JsonViews;


public class CorporateActionAccountElectionsDtoImpl implements CorporateActionAccountElectionsDto {
	@JsonView(JsonViews.Write.class)
	private List<CorporateActionAccountElectionDto> options;

	public CorporateActionAccountElectionsDtoImpl() {
		// Empty constructor
	}

	public CorporateActionAccountElectionsDtoImpl(List<CorporateActionAccountElectionDto> options) {
		this.options = options;
	}

	public CorporateActionAccountElectionsDtoImpl(CorporateActionAccountElectionDto corporateActionAccountElectionDto) {
		options = new ArrayList<>(1);
		options.add(corporateActionAccountElectionDto);
	}

	public static CorporateActionAccountElectionsDtoImpl createSingleAccountElection(Integer electionId) {
		return new CorporateActionAccountElectionsDtoImpl(new CorporateActionAccountElectionDtoImpl(electionId));
	}

	public static CorporateActionAccountElectionsDtoImpl createSingleAccountElection(Integer electionId, BigDecimal units) {
		return new CorporateActionAccountElectionsDtoImpl(new CorporateActionAccountElectionDtoImpl(electionId, units));
	}

	public List<CorporateActionAccountElectionDto> getOptions() {
		return options;
	}

	@JsonIgnore
	@Override
	public CorporateActionAccountElectionDto getPrimaryAccountElection() {
		return options.get(0);
	}
}
