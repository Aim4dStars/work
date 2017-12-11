package com.bt.nextgen.service.integration.cashcategorisation.service;

import java.io.Serializable;
import java.sql.Date;
import java.util.*;

import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.web.ApiFormatter;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.cashcategorisation.model.*;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.staticdata.model.StaticCodeDto;
import com.bt.nextgen.api.transactioncategorisation.model.TransactionCategoryDto;
import com.bt.nextgen.api.transactioncategorisation.service.TransactionCategoryDtoServiceImpl;
import com.bt.nextgen.api.transactionhistory.model.SmsfMembersDto;
import com.bt.nextgen.api.transactionhistory.service.RetrieveSmsfMembersDtoServiceImpl;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.web.controller.cash.util.Attribute;

@Service
@SuppressWarnings({"squid:MethodCyclomaticComplexity"})
public class RetrieveCashContributionDtoServiceImpl implements RetrieveCashContributionDtoService
{
	@Autowired
	@Qualifier("CashCategorisationIntegrationServiceImpl")
	private CashCategorisationIntegrationService cashCategorisationIntegrationService;

	@Autowired
	private RetrieveSmsfMembersDtoServiceImpl retrieveSmsfMembersDtoServiceImpl;

	@Autowired
	private TransactionCategoryDtoServiceImpl transactionCategoryDtoServiceImpl;

	private static final Logger logger = LoggerFactory.getLogger(com.bt.nextgen.service.integration.cashcategorisation.service.RetrieveCashContributionDtoServiceImpl.class);

	@Override
	public CategorisableCashTransactionDto find(DepositKey key, ServiceErrors serviceErrors)
	{
		CategorisableCashTransactionDto dto = new CategorisableCashTransactionDto();
		List <MemberContributionDto> contributionList = new ArrayList <MemberContributionDto>();
		List <Contribution> contributions;

		if (key.getDepositId() != null)
		{
			contributions = cashCategorisationIntegrationService.loadCashContributionsForTransaction(key.getDepositId(), serviceErrors);
		}
		else
		{
			serviceErrors.addError(new ServiceErrorImpl("Invalid document id provided"));
			logger.warn("Invalid document id provided");
			throw new IllegalArgumentException("Invalid document id provided");
		}

		buildCashContributionDto(contributions, contributionList, key.getAccountId(), serviceErrors);
		dto.setMemberContributionDtoList(contributionList);

		return dto;
	}

	void buildCashContributionDto(List <Contribution> contributions, List <MemberContributionDto> contributionList, String accId,
		ServiceErrors serviceErrors)
	{

		for (Contribution contributionImpl : contributions)
		{
			MemberContributionDto dto = new MemberContributionDto();
			dto.setAmount(contributionImpl.getAmount());
			if (contributionImpl.getCashCategorisationSubtype() != null)
			{
				dto.setContributionSubType(contributionImpl.getCashCategorisationSubtype().toString());
			}
			if(contributionImpl.getPersonKey()!=null)
			{
				List <ApiSearchCriteria> criteriaList = new ArrayList <ApiSearchCriteria>();

				criteriaList.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS, accId, OperationType.STRING));

				List <SmsfMembersDto> smsfList = retrieveSmsfMembersDtoServiceImpl.search(criteriaList, serviceErrors);

				if (smsfList != null)
				{
					for (SmsfMembersDto smsfDto : smsfList)
					{
						if (smsfDto.getPersonId().equals(contributionImpl.getPersonKey().getId()))
						{
							dto.setFullName(smsfDto.getFirstName() + " " + smsfDto.getLastName());
						}
					}
				}
				dto.setPersonId(contributionImpl.getPersonKey().getId());
			}
			//Set transaction Category
			List <ApiSearchCriteria> criteriaListForCatType = new ArrayList<>();
			List<TransactionCategoryDto> tranCatDtoList = transactionCategoryDtoServiceImpl.search(criteriaListForCatType, serviceErrors);
			setCategoryTypeInDto(tranCatDtoList,dto);
            dto.setDocId(contributionImpl.getDocId());
            dto.setDescription(contributionImpl.getDescription());
            dto.setTransactionDate(ApiFormatter.asShortDate(contributionImpl.getTransactionDate()));
            dto.setSortDate(contributionImpl.getTransactionDate());

			contributionList.add(dto);
		}


		//Sorting subcategories by order
		if(contributionList!=null && contributionList.size()>1) {
			//For Member sorting
			Collections.sort(contributionList, new MemberNameComparator());
			//For subCategory sorting
			sortSubCategoriesByOrder(contributionList);
		}

	}

	static class MemberNameComparator implements Comparator <MemberContributionDto>, Serializable {
		private static final long serialVersionUID = 1L;
		@Override
        public int compare(MemberContributionDto memberDto1, MemberContributionDto memberDto2)
        {
            String fullName1 = memberDto1.getFullName();
            String fullName2 = memberDto2.getFullName();

            int nameComp = 0;

            if (StringUtils.isEmpty(fullName2) && !StringUtils.isEmpty(fullName1))
            {
                nameComp = 1;
            }
            else if (StringUtils.isEmpty(fullName1) && !StringUtils.isEmpty(fullName2))
            {
                nameComp = -1;
            }
			else if ( (fullName1==null && fullName2 ==null) ||  (StringUtils.isEmpty(fullName1) && StringUtils.isEmpty(fullName2)))
			{
				nameComp = 0;
			}
			else
			{
				nameComp= fullName1.toLowerCase().compareTo(fullName2.toLowerCase());
			}


            if (nameComp != 0 )
            {
                return nameComp;
            }
            else
            {
                String contriSubType1 = memberDto1.getContributionSubType() != null ? CashCategorisationSubtype.getByAvaloqInternalId(memberDto1.getContributionSubType()).getName() : "";
                String contriSubType2 = memberDto2.getContributionSubType() != null ? CashCategorisationSubtype.getByAvaloqInternalId(memberDto2.getContributionSubType()).getName() : "";
                return contriSubType1.toLowerCase().compareTo(contriSubType2.toLowerCase());
            }

        }
    }

    public CategorisableCashTransactionDto getCashTransactionsForAccounts(List <ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors)
    {

        String accountId = null;
        Date financialYearDate = null;
        String strDateVal = null;

        for (ApiSearchCriteria criteria : criteriaList)
        {
            if ("accountId".equalsIgnoreCase(criteria.getProperty()))
            {
                accountId = criteria.getValue();
            }
            else if ("financialYearDate".equalsIgnoreCase(criteria.getProperty()))
            {
                strDateVal = criteria.getValue();
                financialYearDate = Date.valueOf(strDateVal);
            }
        }

        CategorisableCashTransactionDto dto = new CategorisableCashTransactionDto();
        List <MemberContributionDto> contributionList = new ArrayList <MemberContributionDto>();
        //List <Contribution> contributions = new ArrayList <Contribution>();
        List <Contribution> contributions = null;

        if (accountId!=null && financialYearDate!=null)
        {
            contributions = cashCategorisationIntegrationService.loadCashContributionsForAccount(AccountKey.valueOf(accountId), financialYearDate, null, serviceErrors); //Null category will return all category
            buildCashContributionDto(contributions, contributionList, EncodedString.fromPlainText(accountId).toString(), serviceErrors);
        }

        dto.setMemberContributionDtoList(contributionList);
        dto.setAccountId(accountId);
        //dto.setAccountId(new EncodedString(key.getAccountId()).plainText());
        return dto;
    }
	private void setCategoryTypeInDto(List<TransactionCategoryDto> tranCatDtoList, MemberContributionDto dto)
	{
		for(TransactionCategoryDto catDto : tranCatDtoList){
			
			if(!(catDto.getSubCategories().isEmpty())){
				for(StaticCodeDto subCategory: catDto.getSubCategories()){
					if(subCategory.getIntlId().equals(dto.getContributionSubType())){
						dto.setTransactionCategory(catDto.getIntlId());
						return;
					}
				}
			}
			else{
				dto.setTransactionCategory(catDto.getIntlId());
				
			}
		}
	}

	private void sortSubCategoriesByOrder(List <MemberContributionDto> contributionList)
	{
		//To check for payment category records
		if(contributionList.get(0).getPersonId()==null)
		{
			Collections.sort(contributionList, BY_ORDER);
		}
	}

	private static final Comparator<MemberContributionDto> BY_ORDER = new Comparator<MemberContributionDto>() {
		@Override
		public int compare(MemberContributionDto o1, MemberContributionDto o2)
		{
			Integer order1= CashCategorisationSubtype.getByAvaloqInternalId(o1.getContributionSubType()).getOrder();
			Integer order2= CashCategorisationSubtype.getByAvaloqInternalId(o2.getContributionSubType()).getOrder();
			return order1.compareTo(order2);
		}
	};
}
