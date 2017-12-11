package com.bt.nextgen.api.corporateaction.v1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.bt.nextgen.config.JsonViews;


public class CorporateActionExerciseRightsAccountElectionsDtoImpl implements CorporateActionAccountElectionsDto {
	@JsonView(JsonViews.Write.class)
	private List<CorporateActionAccountElectionDto> options;

	public CorporateActionExerciseRightsAccountElectionsDtoImpl(List<CorporateActionAccountElectionDto> options) {
		this.options = options;
	}

	public static CorporateActionExerciseRightsAccountElectionsDtoImpl createSingleAccountElection(Integer electionId, BigDecimal units,
																								   BigDecimal oversubscribe) {
		List<CorporateActionAccountElectionDto> options = new ArrayList<>();
		options.add(new CorporateActionExerciseRightsAccountElectionDtoImpl(electionId, units, oversubscribe));

		return new CorporateActionExerciseRightsAccountElectionsDtoImpl(options);
	}

	@Override
	public List<CorporateActionAccountElectionDto> getOptions() {
		return options;
	}

	@JsonIgnore
	@Override
	public CorporateActionAccountElectionDto getPrimaryAccountElection() {
		return getOptions().get(0);
	}
}
