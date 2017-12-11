package com.bt.nextgen.api.transactioncategorisation.service;

import com.bt.nextgen.api.smsf.constants.SortableCategories;
import com.bt.nextgen.api.staticdata.model.StaticCodeDto;
import com.bt.nextgen.api.staticdata.model.StaticCodeOrderedDto;
import com.bt.nextgen.api.transactioncategorisation.model.TransactionCategoryDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.cashcategorisation.model.CashCategorisationType;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.Field;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bt.nextgen.core.util.Properties;

import java.util.*;

@Service
public class TransactionCategoryDtoServiceImpl implements TransactionCategoryDtoService
{

	private static final String CASH_CAT_SUB_TYPE = "cash_cat_subtype";
	private static final String EXTL_FLD_MODAL = "modal";
	private static final String EXTL_FLD_TRANSACTION_META_TYPE = "txntype";
	private static final String PAYMENT = "payment";
	private static final String DEPOSIT = "deposit";
	private static final String CONTRIBUTION_SUB_CAT = "membersubcat";

	@Autowired
	private StaticIntegrationService staticIntegrationService;

	@Override
	public List <TransactionCategoryDto> search(List <ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors)
	{
        return createCategoryList(staticIntegrationService.loadCodes(CodeCategory.CASH_CATEGORY_TYPE, serviceErrors), staticIntegrationService.loadCodes(CodeCategory.CASH_CATEGORY_SUB_TYPE, serviceErrors));
	}

	private List<TransactionCategoryDto> createCategoryList(Collection<Code> categoryCodes, Collection<Code> subTypeCategoryCodes)
	{

		List <TransactionCategoryDto> categoryList = new ArrayList <TransactionCategoryDto>();
		for (Code code : categoryCodes)
		{
			TransactionCategoryDto dto= new TransactionCategoryDto();
			setDtoProperties(code,dto);

			List<StaticCodeDto> subtypeList = new ArrayList<StaticCodeDto>();
			//Set subcategory list based on toggle
			for(Code subTypeCode : subTypeCategoryCodes)
			{
				//Setting subcategories list
				for (Iterator <Field> iterator = subTypeCode.getFields().iterator(); iterator.hasNext();)
				{
					Field field =  iterator.next();
					if (field.getValue().equals(code.getCodeId()))
					{
						subtypeList.add(new StaticCodeDto(subTypeCode.getCodeId(), subTypeCode.getName(), subTypeCode.getUserId(), subTypeCode.getIntlId(), CASH_CAT_SUB_TYPE));
					}
				}
			}

			//sorting all the subcategories by order
			if(subtypeList!=null && subtypeList.size()>1) {
				subtypeList = sortSubCategoriesByOrder(subtypeList);
			}

			dto.setSubCategories(subtypeList);
			categoryList.add(dto);
		}
		sortTransactionCategories(categoryList);
		return categoryList;
	}

	private void setDtoProperties(Code code, TransactionCategoryDto dto)
	{
		dto.setIntlId(code.getIntlId());
		dto.setLabel(code.getName());

		if(code.getFields()!=null) {
			for (Iterator<Field> iterator = code.getFields().iterator(); iterator.hasNext(); ) {
				Field field = iterator.next();

				if (field.getName().equals(EXTL_FLD_MODAL)) {
					dto.setCategorisationLevel(field.getValue());
				} else if (field.getName().equals(EXTL_FLD_TRANSACTION_META_TYPE)) {
					if ("1".equals(field.getValue())) {
						dto.setTransactionMetaType(DEPOSIT);
					} else if ("2".equals(field.getValue())) {
						dto.setTransactionMetaType(PAYMENT);
					}
				}
			}
		}

		// Transaction meta type (deposit or payments) sourced from enum until avaloq can support.
		if (StringUtils.isEmpty(dto.getTransactionMetaType()) && CashCategorisationType.getByAvaloqInternalId(dto.getIntlId()) != null)
		{
			dto.setTransactionMetaType(CashCategorisationType.getByAvaloqInternalId(dto.getIntlId()).getOrderType().getDisplayId());
		}
	}

	private List<StaticCodeDto> sortSubCategoriesByOrder(List<StaticCodeDto> subtypeList)
	{
		List<StaticCodeDto> subCategoryList = new ArrayList<StaticCodeDto>();
		final Map<String, Integer> sortOrder = SortableCategories.getSortOrder(CodeCategory.CASH_CATEGORY_SUB_TYPE.getCode());
		if (sortOrder != null) {
			subCategoryList = orderStaticCodes(subtypeList, sortOrder);
		}
		return subCategoryList;
	}

	private List<StaticCodeDto> orderStaticCodes(List<StaticCodeDto> codes, Map<String, Integer> mapping)
	{
		// Empty order mapping table -- return unsorted static codes
		if (mapping.isEmpty()) {
			return codes;
		}

		List<StaticCodeOrderedDto> orderedStaticCodeList = new ArrayList<>(codes.size());
		for (StaticCodeDto staticCode : codes)
		{
			Integer order = mapping.get(staticCode.getIntlId());
			if(order!=null) {
				orderedStaticCodeList.add(new StaticCodeOrderedDto(staticCode, order));
			}
		}
		return performOrdering(orderedStaticCodeList);
	}

	private List<StaticCodeDto> performOrdering(List<StaticCodeOrderedDto> orderedStaticCodeList)
	{
		Collections.sort(orderedStaticCodeList, BY_ORDER);
		final List<StaticCodeDto> staticCodeList = new ArrayList<>(orderedStaticCodeList.size());
		for (StaticCodeOrderedDto orderedDto : orderedStaticCodeList)
		{
			staticCodeList.add(new StaticCodeDto(orderedDto));
		}
		return staticCodeList;
	}

	private static final Comparator<StaticCodeOrderedDto> BY_ORDER = new Comparator<StaticCodeOrderedDto>() {
		@Override
		public int compare(StaticCodeOrderedDto o1, StaticCodeOrderedDto o2) {
			if (o1.getOrder() > o2.getOrder()) {
				return 1;
			} else {
				return -1;
			}
		}
	};

	private void sortTransactionCategories(List<TransactionCategoryDto> categoryList)
	{
		Collections.sort(categoryList, new Comparator <TransactionCategoryDto>()

		{
			public int compare(TransactionCategoryDto transactionCategoryDto1, TransactionCategoryDto transactionCategoryDto2)
			{
				StringBuilder categoryName1 = new StringBuilder();
				StringBuilder categoryName2 = new StringBuilder();
				categoryName1.append(transactionCategoryDto1.getLabel());
				categoryName2.append(transactionCategoryDto2.getLabel());
				return categoryName1.toString().toUpperCase().compareTo(categoryName2.toString().toUpperCase());

			}
		});
	}

}
