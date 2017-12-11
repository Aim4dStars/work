package com.bt.nextgen.service.avaloq.corporateaction;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSelectedOptionDto;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionPosition;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionValidationError;

import java.util.List;

public class CorporateActionElectionGroupImpl implements CorporateActionElectionGroup {
	private String orderNumber;

	private String ipsId;

	private List<CorporateActionPosition> positions;

	private CorporateActionSelectedOptionDto selectedOption;

	private List<CorporateActionOption> options;

	private List<CorporateActionValidationError> electionErrors;

	public CorporateActionElectionGroupImpl() {
	}

	public CorporateActionElectionGroupImpl(String orderNumber, CorporateActionSelectedOptionDto selectedOption,
											List<CorporateActionPosition> positions,
											List<CorporateActionOption> options) {
		this.orderNumber = orderNumber;
		this.selectedOption = selectedOption;
		this.positions = positions;
		this.options = options;
	}

	@Override
	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	@Override
	public List<CorporateActionPosition> getPositions() {
		return positions;
	}

	public void setPositions(List<CorporateActionPosition> positions) {
		this.positions = positions;
	}

	@Override
	public List<CorporateActionValidationError> getValidationErrors() {
		return electionErrors;
	}

	public void setElectionErrors(
			List<CorporateActionValidationError> electionErrors) {
		this.electionErrors = electionErrors;
	}

	@Override
	public List<CorporateActionOption> getOptions() {
		return options;
	}

	public void setOptions(List<CorporateActionOption> options) {
		this.options = options;
	}

	public CorporateActionSelectedOptionDto getSelectedOption() {
		return selectedOption;
	}

	public void setSelectedOption(CorporateActionSelectedOptionDto selectedOption) {
		this.selectedOption = selectedOption;
	}

	@Override
	public String getIpsId() {
		return ipsId;
	}

	@Override
	public void setIpsId(String ipsId) {
		this.ipsId = ipsId;
	}
}
