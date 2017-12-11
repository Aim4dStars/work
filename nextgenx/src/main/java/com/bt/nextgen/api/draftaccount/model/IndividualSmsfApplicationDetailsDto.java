package com.bt.nextgen.api.draftaccount.model;

import com.bt.nextgen.api.account.v1.model.LinkedAccountDto;
import com.bt.nextgen.api.account.v1.model.PersonRelationDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bt.nextgen.api.broker.model.BrokerDto;
import com.bt.nextgen.api.client.model.InvestorDto;
import com.bt.nextgen.api.client.model.SmsfDto;
import com.bt.nextgen.service.integration.domain.InvestorRole;

public class IndividualSmsfApplicationDetailsDto extends
		ClientApplicationDetailsDto {

	private SmsfDto smsf;
	private List<InvestorDto> trustees = new ArrayList<>();
	private List<InvestorDto> members = new ArrayList<>();

    public IndividualSmsfApplicationDetailsDto withSmsf(SmsfDto smsf){
        this.smsf = smsf;
        return this;
    }
    
    public IndividualSmsfApplicationDetailsDto withMembers(List<InvestorDto> members){
        this.members = members;
        return this;
    }
    
    public IndividualSmsfApplicationDetailsDto withTrustees(List<InvestorDto> trustees){
        this.trustees = trustees;
        return this;
    }
    
	public SmsfDto getSmsf() {
		return smsf;
	}

	public List<InvestorDto> getTrustees() {
		return trustees;
	}

	public List<InvestorDto> getMembers() {
		return members;
	}
}
