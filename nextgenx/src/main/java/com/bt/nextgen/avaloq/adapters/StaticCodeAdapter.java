package com.bt.nextgen.avaloq.adapters;

import com.avaloq.abs.screen_rep.hira.web_ui_lov_export.Code;
import com.avaloq.abs.screen_rep.hira.web_ui_lov_export.CodeHead;
import com.bt.nextgen.service.avaloq.StaticCodeInterface;

import static com.bt.nextgen.service.AvaloqGatewayUtil.asString;

public class StaticCodeAdapter implements StaticCodeInterface
{

	private Code baseCode;

	private String name;

	public StaticCodeAdapter(Code code)
	{
		this.baseCode = code;
	}

	@Override
	public String getId()
	{
		if (baseCode != null && baseCode.getCodeHeadList() != null && baseCode.getCodeHeadList().getCodeHead().size() == 1)
		{
			CodeHead codeHead = baseCode.getCodeHeadList().getCodeHead().get(0);
			return asString(codeHead.getCodeId());
		}
		else
			return null;
	}

	@Override
	public String getName()
	{
		if (this.name != null)
			return name;
		else if (baseCode != null && baseCode.getCodeHeadList() != null && baseCode.getCodeHeadList().getCodeHead().size() == 1)
		{
			CodeHead codeHead = baseCode.getCodeHeadList().getCodeHead().get(0);
			return asString(codeHead.getName());
		}
		else
			return null;
	}

	@Override
	public String getValue()
	{
		if (baseCode != null && baseCode.getCodeHeadList() != null && baseCode.getCodeHeadList().getCodeHead().size() == 1)
		{
			CodeHead codeHead = baseCode.getCodeHeadList().getCodeHead().get(0);
			return asString(codeHead.getIntlId());
		}
		else
			return null;
	}

	@Override
	public void setName(String name)
	{
		if (name != null)
		{
			this.name = name;
		}

	}

}
