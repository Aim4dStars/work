package com.bt.nextgen.service.integration.cashcategorisation.builder;


import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.avaloq.TransactionStatusImpl;
import com.bt.nextgen.service.integration.TransactionStatus;
import com.btfin.panorama.core.security.integration.account.PersonKey;
import com.bt.nextgen.service.integration.cashcategorisation.model.CashCategorisationAction;
import com.bt.nextgen.service.integration.cashcategorisation.model.CashCategorisationSubtype;
import com.bt.nextgen.service.integration.cashcategorisation.model.CategorisableCashTransaction;
import com.bt.nextgen.service.integration.cashcategorisation.model.Contribution;
import com.btfin.abs.common.v1_0.Hdr;
import com.btfin.abs.trxservice.cashcat.v1_0.ActionType;
import com.btfin.abs.trxservice.cashcat.v1_0.CashCatReq;
import com.btfin.abs.trxservice.cashcat.v1_0.CashCatRsp;
import com.btfin.abs.trxservice.cashcat.v1_0.Data;
import com.btfin.abs.trxservice.cashcat.v1_0.MbrCat;
import com.btfin.abs.trxservice.cashcat.v1_0.MbrCatList;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * Utility class to create Cash Categorisation request and response objects
 */
public final class MemberContributionConverter
{
	private static final String SUCCESS = "SUCCESS";

	private MemberContributionConverter()
	{
	}

	public static CashCatReq toCashCategorisationRequest(CashCategorisationAction action, final CategorisableCashTransaction cashTransToSplit)
	{
		Data data = new Data();

		data.setBp(AvaloqGatewayUtil.createIdVal(cashTransToSplit.getAccountKey().getAccountId()));
		data.setDoc(AvaloqGatewayUtil.createNumberVal(cashTransToSplit.getDocId()));

		if (cashTransToSplit.getContributionSplit() != null && !(cashTransToSplit.getContributionSplit().isEmpty()))
		{
			data.setMbrCatList(createMemberContributionList(cashTransToSplit.getContributionSplit()));
		}

		if (action == CashCategorisationAction.ADD)
			data.setAction(ActionType.ADD);
		else if (action == CashCategorisationAction.REMOVE)
			data.setAction(ActionType.REMOVE);
		else
			throw new IllegalArgumentException("Unsupported cash categorisation action");

		CashCatReq cashCatReq = new CashCatReq();
		Hdr header = AvaloqGatewayUtil.createHdr();
		cashCatReq.setHdr(header);
		cashCatReq.setData(data);

		return cashCatReq;
	}

	private static MbrCatList createMemberContributionList(List<Contribution> contributionList)
	{
		MbrCatList memberContribList = new MbrCatList();

		for (Contribution split : contributionList)
		{
			MbrCat contribution = createMemberContribution(split.getPersonKey(), split.getAmount(), split.getCashCategorisationSubtype());
			memberContribList.getMbrCat().add(contribution);
		}

		return memberContribList;
	}

	private static MbrCat createMemberContribution(PersonKey personKey, BigDecimal amount, CashCategorisationSubtype subtype)
	{
		MbrCat memberContribution = new MbrCat();

		// If no person key then assume this is a "fund" level categorisation
		if (personKey != null)
		{
			memberContribution.setPerson(AvaloqGatewayUtil.createIdVal(personKey.getId()));
		}

		memberContribution.setAmount(AvaloqGatewayUtil.createNumberVal(amount));
		memberContribution.setCashCatSubtype(AvaloqGatewayUtil.createExtlIdVal(subtype.getAvaloqInternalId()));

		return memberContribution;
	}

	public static TransactionStatus toCashCategorisationResponse(CashCatRsp rsp)
	{
		TransactionStatus status = new TransactionStatusImpl();
		String result = null;

		if (rsp!=null && rsp.getData()!=null){

			result = rsp.getData().getStatus().getVal();
			if (StringUtils.isNotEmpty(result) && SUCCESS.equalsIgnoreCase(result)) {
				status.setSuccessful(true);
				return status;
			}
		}

		status.setSuccessful(false);
		return status;
	}
}
