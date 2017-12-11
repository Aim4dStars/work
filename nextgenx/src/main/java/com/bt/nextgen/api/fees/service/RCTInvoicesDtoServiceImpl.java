package com.bt.nextgen.api.fees.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bt.nextgen.api.fees.model.RCTInvoicesDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.fees.RCTInvoices;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.fees.RCTInvoicesFee;
import com.bt.nextgen.service.integration.fees.RCTInvoicesIntegrationService;
import com.bt.nextgen.web.controller.cash.util.Attribute;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;

@Service("RecipientCreatedTaxInvoicesDtoServiceV2")
@Transactional(value = "springJpaTransactionManager")
public class RCTInvoicesDtoServiceImpl implements RCTInvoicesDtoService {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Autowired
    private RCTInvoicesIntegrationService taxInvoiceIntegrationService;
    
	@Override
	public List<RCTInvoicesDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {

		DateTime startDate = null;
		DateTime endDate = null;

		for (ApiSearchCriteria parameter : criteriaList) {
			if (Attribute.START_DATE.equals(parameter.getProperty())) {
				startDate = new DateTime(parameter.getValue());
			} else if (Attribute.END_DATE.equals(parameter.getProperty())) {
				endDate = new DateTime(parameter.getValue());
			} 
		}

		Collection<BrokerKey> brokerKeys = getBrokers(serviceErrors);		
		Collection<RCTInvoicesFee> rctiFees = getRCTInvoicesFees(brokerKeys, startDate, endDate, serviceErrors);		
		Collection<RCTInvoicesDto> rctiDtoList = aggregateFees(rctiFees);				
		
		return new ArrayList<>(rctiDtoList);
	}

	private Collection<BrokerKey> getBrokers(ServiceErrors serviceErrors) {
		Collection<Broker> brokers = brokerIntegrationService.getBrokersForJob(userProfileService.getActiveProfile(), serviceErrors);
		Collection<BrokerKey> brokerKeys = new ArrayList<>();
        for (Broker broker : brokers){
            brokerKeys.add(broker.getKey());
        }
		return brokerKeys;
	}
	
	private Collection<RCTInvoicesFee> getRCTInvoicesFees(Collection<BrokerKey> brokerKeys, DateTime startDate, DateTime endDate,
			ServiceErrors serviceErrors) {
		Collection<RCTInvoicesFee> rctiFees = new ArrayList<>();
		for (BrokerKey brokerKey : brokerKeys) {
			RCTInvoices rctInvoices = taxInvoiceIntegrationService.getRecipientCreatedTaxInvoice(brokerKey, startDate, endDate, serviceErrors);
			Collection<RCTInvoicesFee> rctiFeesForBroker = rctInvoices.getRCTInvoicesFees();
			rctiFees.addAll(rctiFeesForBroker);
		}
		return rctiFees;
	}

	private Collection<RCTInvoicesDto> aggregateFees(Collection<RCTInvoicesFee> rctiFees) {
		Collection<RCTInvoicesDto> rctiDtoList = new ArrayList<>();
		Collection<DateTime> feePeriods = Lambda.convert(rctiFees, new Converter<RCTInvoicesFee, DateTime>() {
			public DateTime convert(RCTInvoicesFee fee) {
		    	return fee.getInvoiceDate().dayOfMonth().withMinimumValue();
			}
		});
		feePeriods = new ArrayList<>(new HashSet<>(feePeriods));
		for (DateTime period: feePeriods) {
			DateTime endOfPeriod = period.dayOfMonth().withMaximumValue();
			rctiDtoList.add(new RCTInvoicesDto(period, endOfPeriod));
		}
		return rctiDtoList;
	}
}
