package com.bt.nextgen.core.reporting.view;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.bt.nextgen.core.reporting.ReportIdentity;
import com.bt.nextgen.core.reporting.ReportSource;

@Component
public class ReportViewFactoryImpl implements ReportViewFactory, ApplicationContextAware
{
	private ApplicationContext ctx;

	private ReportSource reportSource;

	@Override
	public void setApplicationContext(ApplicationContext appContext) throws BeansException
	{
		this.ctx = appContext;
	}

	@Autowired
	public void setReportSource(ReportSource reportSource)
	{
		this.reportSource = reportSource;
	}

	@Override
	public ReportView createReportView(ReportIdentity reportId)
	{
		Map <String, Object> reportViews = ctx.getBeansWithAnnotation(com.bt.nextgen.core.reporting.stereotype.ReportView.class);

		ReportView reportView = (ReportView)reportViews.get(reportId.getTemplateKey() + "View");

		if (reportView == null)
		{
			reportView = new DefaultReportViewImpl();
		}

		reportView.init(reportId, reportSource);

		return reportView;
	}

}
