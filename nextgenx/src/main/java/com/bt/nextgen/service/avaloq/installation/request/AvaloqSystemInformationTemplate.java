package com.bt.nextgen.service.avaloq.installation.request;

import java.util.ArrayList;
import java.util.List;

import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqTemplate;
import com.bt.nextgen.service.avaloq.AvaloqType;

public enum AvaloqSystemInformationTemplate implements AvaloqTemplate
{

	AVALOQ_INSTALLATION_DETAILS("BTFG$TASK_UI_CHG.ALL#CHG")
		{
			@Override public List<AvaloqParameter> getValidParamters()
			{
				AvaloqParameter release = new AvaloqParameter()
				{
					@Override public AvaloqType getParamType()
					{
						return AvaloqType.VAL_TEXTVAL;
					}

					@Override public String getParamName()
					{
						return "rel";
					}
				};

				AvaloqParameter installTimeFrom = new AvaloqParameter()
				{
					@Override public AvaloqType getParamType()
					{
						return AvaloqType.VAL_DATETIMEVAL;
					}

					@Override public String getParamName()
					{
						return "inst_time_from";
					}
				};

				AvaloqParameter installTimeTo =  new AvaloqParameter()
				{
					@Override public AvaloqType getParamType()
					{
						return AvaloqType.VAL_DATETIMEVAL;
					}

					@Override public String getParamName()
					{
						return "inst_time_to";
					}
				};
				List<AvaloqParameter> params = new ArrayList();
				params.add(release);
				params.add(installTimeTo);
				params.add(installTimeFrom);
				return params;
			}
		};

	AvaloqSystemInformationTemplate(String templateName)
	{
		this.templateName = templateName;
	}

	private String templateName;

	@Override public String getTemplateName()
	{
		return templateName;
	}



}
