package com.bt.nextgen.service.avaloq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.avaloq.abs.bb.fld_def.NrFld;
import com.avaloq.abs.bb.fld_def.TextFld;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;

import static com.bt.nextgen.service.AvaloqGatewayUtil.ctxIdOf;

@Service
public class AvaloqStaticServiceUtils
{

	@Autowired
	private StaticIntegrationService staticService;

	public String fromCtxId(TextFld field, CodeCategory category, ServiceErrors serviceErrors)
	{
		if (ctxIdOf(field) != null || category == null)
		{
			return staticService.loadCode(category, ctxIdOf(field), serviceErrors).getName();
		}
		return null;
	}

	public String fromCtxId(NrFld field, CodeCategory category, ServiceErrors serviceErrors)
	{
		if (ctxIdOf(field) != null || category == null)
		{
			return staticService.loadCode(category, ctxIdOf(field), serviceErrors).getName();
		}
		return null;
	}

	public AccountStructureType getAccountStructureType(NrFld field, ServiceErrors serviceErrors)
	{
		String accountStructure = staticService.loadCode(CodeCategory.ACCOUNT_STRUCTURE_TYPE, ctxIdOf(field), serviceErrors)
			.getName();
		return AccountStructureType.valueOf(accountStructure);
	}
}
