package com.bt.nextgen.service.integration.externalasset.builder;

import org.apache.commons.lang.StringUtils;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.TransactionStatusImpl;
import com.bt.nextgen.service.integration.TransactionStatus;
import com.btfin.abs.err.v1_0.ErrType;
import com.btfin.abs.trxservice.extlhold.v1_0.ExtlHoldRsp;

@SuppressWarnings(
{
	"squid:S1172", "squid:S1068", "squid:S1067", "findbugs:URF_UNREAD_FIELD", "squid:MethodCyclomaticComplexity",
	"checkstyle:com.puppycrawl.tools.checkstyle.checks.metrics.NPathComplexityCheck"
})
public final class ExternalAssetResponseBuilder
{
	private static final String SUCCESS = "SUCCESS";
	private static final String SAVED = "saved";
	private static final String FAILED = "notSaved";
	private static final String DUPLICATE = "duplicatesDetected";

	private ExternalAssetResponseBuilder()
	{

	}

	/**
	* Converts an avaloq external asset response object into a transaction status object
	* @param rsp
	* @param serviceErrors
	* @return
	*/
	public static TransactionStatus toExternalAssetResponse(ExtlHoldRsp rsp, ServiceErrors serviceErrors)
	{
		TransactionStatus transaction = new TransactionStatusImpl();
		String result = null;

		//For Success
		if (rsp != null && rsp.getData() != null)
		{
			result = rsp.getData().getStatus().getVal();
			if (StringUtils.isNotEmpty(result) && SUCCESS.equalsIgnoreCase(result))
			{
				transaction.setStatus(SAVED);
				return transaction;
			}

			//For Duplicate entry
			else if (rsp.getRsp().getExec() != null && rsp.getRsp().getExec().getErrList() != null
				&& !rsp.getRsp().getExec().getErrList().getErr().isEmpty())
			{
				ErrType errType = rsp.getRsp().getExec().getErrList().getErr().get(0).getType();
				if (errType.equals(ErrType.UI))
				{
					transaction.setStatus(DUPLICATE);
					return transaction;
				}
			}
		}

		//If some problem occured 
		transaction.setStatus(FAILED);
		return transaction;

	}
}
