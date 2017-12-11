package com.bt.nextgen.api.termdeposit.service;

import com.bt.nextgen.api.account.v1.model.AccountKey;

import java.util.ArrayList;
import java.util.List;

import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.web.ApiFormatter;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bt.nextgen.api.termdeposit.model.TermDepositDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.termdeposit.TermDeposit;
import com.bt.nextgen.service.integration.termdeposit.TermDepositIntegrationService;

@Service
public class TermDepositsDtoServiceImpl implements TermDepositsDtoService
{
	@Autowired
	private StaticIntegrationService staticService;

	@Autowired
	TermDepositIntegrationService termDepositIntegrationService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Override
    public List <TermDepositDto> findAll(ServiceErrors serviceErrors)
    {
        List<Broker> brokers = brokerIntegrationService.getBrokersForJob(userProfileService.getActiveProfile(), serviceErrors);
        List<BrokerKey> brokerKeys = new ArrayList<>();
        for (Broker broker : brokers){
            brokerKeys.add(broker.getKey());
        }
        List <TermDeposit> termDepositModelList = termDepositIntegrationService.loadTermDeposit(brokerKeys, serviceErrors);
        return toTermDepositDto(termDepositModelList);
    }

	private List <TermDepositDto> toTermDepositDto(List <TermDeposit> termDepositModelList)
	{
		List <TermDepositDto> resultList = new ArrayList <>();

		for (TermDeposit termDepositModel : termDepositModelList)
		{
			TermDepositDto dto = new TermDepositDto();

			dto.setAccountName(termDepositModel.getAccountName());
			dto.setBrandLogoUrl(termDepositModel.getBrandLogoUrl());
			dto.setMaturityDate(ApiFormatter.asShortDate(termDepositModel.getMaturityDate()));
			dto.setPrincipalAmount(termDepositModel.getPrincipalAmount());
			dto.setDaysToMaturity(termDepositModel.getDaysToMaturity());
			dto.setAccountKey(new AccountKey(EncodedString.fromPlainText(termDepositModel.getAccountId()).toString()));
			resultList.add(dto);
		}

		return resultList;
	}
}
