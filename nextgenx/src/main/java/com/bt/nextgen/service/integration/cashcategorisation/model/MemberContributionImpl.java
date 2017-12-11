package com.bt.nextgen.service.integration.cashcategorisation.model;

import java.math.BigDecimal;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.btfin.panorama.core.security.integration.account.PersonKey;
import com.bt.nextgen.service.integration.cashcategorisation.builder.AccountKeyConverterV2;
import com.bt.nextgen.service.integration.cashcategorisation.builder.PersonKeyConverter;
import com.bt.nextgen.service.integration.externalasset.builder.IsoDateTimeConverter;
import org.joda.time.DateTime;

/**
 * Contribution to a specific SMSF member<p>
 */
@ServiceBean(xpath = "dt_head")
public class MemberContributionImpl implements Contribution
{

	@ServiceElement(xpath = "bp_id/val", converter = AccountKeyConverterV2.class)
	private AccountKey accountKey;

	@ServiceElement(xpath = "doc_id/val")
	private String docId;

	@ServiceElement(xpath = "person_id/val", converter = PersonKeyConverter.class)
	private PersonKey personKey;

	@ServiceElement(xpath = "amount/val", converter = BigDecimalConverter.class)
	private BigDecimal amount;

	@ServiceElement(xpath = "cash_cat_subtype_id/val", staticCodeCategory = "CASH_CATEGORY_SUB_TYPE")
	private CashCategorisationSubtype cashCategorisationSubtype;

	@ServiceElement(xpath = "conc_type_id/val", staticCodeCategory = "CONTRIBUTION_CLASSIFICATION")
	private ContributionClassification contributionClassification;

	@ServiceElement(xpath = "descn/val")
	private String description;

	@ServiceElement(xpath = "doc_trx_date/val", converter = IsoDateTimeConverter.class)
	private DateTime transactionDate;

	@Override
	public PersonKey getPersonKey()
	{
		return personKey;
	}

	@Override
	public BigDecimal getAmount()
	{
		return amount;
	}

	@Override
	public CashCategorisationSubtype getCashCategorisationSubtype()
	{
		return cashCategorisationSubtype;
	}

	@Override
	public void setPersonKey(PersonKey personKey)
	{
		this.personKey = personKey;
	}

	@Override
	public void setAmount(BigDecimal amount)
	{
		this.amount = amount;
	}

	@Override
	public void setCashCategorisationSubtype(CashCategorisationSubtype cashCategorisationSubtype)
	{
		this.cashCategorisationSubtype = cashCategorisationSubtype;
	}

	@Override
	public AccountKey getAccountKey()
	{
		return accountKey;
	}

	@Override
	public void setAccountKey(AccountKey accountKey)
	{
		this.accountKey = accountKey;
	}

	@Override
	public String getDocId()
	{
		return docId;
	}

	@Override
	public void setDocId(String docId)
	{
		this.docId = docId;
	}

	@Override
	public ContributionClassification getContributionClassification() {
		return contributionClassification;
	}

	@Override
	public void setContributionClassification(ContributionClassification contributionClassification) {
		this.contributionClassification = contributionClassification;
	}

	@Override
	public String getDescription()
	{
		return description;
	}

	@Override
	public void setDescription(String description)
	{
		this.description = description;
	}

	public DateTime getTransactionDate()
	{
		return transactionDate;
	}

	public void setTransactionDate(DateTime transactionDate)
	{
		this.transactionDate = transactionDate;
	}
}