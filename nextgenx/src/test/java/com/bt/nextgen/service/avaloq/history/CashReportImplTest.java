package com.bt.nextgen.service.avaloq.history;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;

import com.bt.nextgen.service.integration.history.CashRateComponent;
import com.bt.nextgen.service.integration.history.InterestDate;

public class CashReportImplTest
{

	private DateTime rateDate1 = new DateTime(2013,4,12,0,0);
	private DateTime rateDate2 = new DateTime(2013,5,13,0,0);
	private DateTime rateDate3 = new DateTime(2013,7,13,0,0);
	private DateTime rateDate4 = new DateTime(2013,8,4,0,0);
	private DateTime rateDate5 = new DateTime(2014,1,4,0,0);
	private DateTime rateDate6 = new DateTime(2014,2,4,0,0);
	private DateTime rateDate7 = new DateTime(2014,2,5,0,0);

	private InterestDateTestImpl base1 = new InterestDateTestImpl(rateDate1,new BigDecimal("1.5"));
	private InterestDateTestImpl base2 = new InterestDateTestImpl(rateDate3,new BigDecimal("1.7"));
	private InterestDateTestImpl base3 = new InterestDateTestImpl(rateDate4,new BigDecimal("1.6"));
	private InterestDateTestImpl base4 = new InterestDateTestImpl(rateDate5,new BigDecimal("1.8"));

	private InterestDateTestImpl margin1 = new InterestDateTestImpl(rateDate1,new BigDecimal("-0.5"));
	private InterestDateTestImpl margin2 = new InterestDateTestImpl(rateDate2,new BigDecimal("0"));
	private InterestDateTestImpl margin3 = new InterestDateTestImpl(rateDate7,new BigDecimal("-0.25"));

	private InterestDateTestImpl special1 = new InterestDateTestImpl(rateDate2,new BigDecimal("0.1"));
	private InterestDateTestImpl special2 = new InterestDateTestImpl(rateDate4,new BigDecimal("0.2"));
	private InterestDateTestImpl special3 = new InterestDateTestImpl(rateDate6,new BigDecimal("0"));



	@Test
	public void testGetInterestRates_orderingOnBase() throws Exception
	{
		CashReportImpl reportTest = new CashReportImpl();

		List<InterestDate> base = new ArrayList<>();
		base.add(base2);
		base.add(base1);

		CashRateComponent baseComponent = new CashRateComponentTestImpl("base rates id", "base", base);

		reportTest.setBaseCashRateComponent(baseComponent);

		List<InterestDate> simpleBaseList = reportTest.getInterestRates();
		assertThat(simpleBaseList, is(notNullValue()));
		assertThat(simpleBaseList.size(), is(2));
		assertThat(simpleBaseList.get(0).getEffectiveDate(), is(rateDate1));
		assertThat(simpleBaseList.get(0).getInterestRate(), equalTo(new BigDecimal("1.5")));
		assertThat(simpleBaseList.get(1).getEffectiveDate(), is(rateDate3));
		assertThat(simpleBaseList.get(1).getInterestRate(), equalTo(new BigDecimal("1.7")));




	}

	@Test
	public void testInterestRates_withMarginAndBase()throws Exception
	{

		CashReportImpl reportTest = new CashReportImpl();

		List<InterestDate> base = new ArrayList<>();
		base.add(base2);
		base.add(base1);
		CashRateComponent baseComponent = new CashRateComponentTestImpl("base rates id", "base", base);
		reportTest.setBaseCashRateComponent(baseComponent);

		List<InterestDate> margin = new ArrayList<>();
		margin.add(margin2);
		margin.add(margin1);

		CashRateComponent marginComponent = new CashRateComponentTestImpl("margin contrib", "margin", margin);
		reportTest.setMarginCashRateComponent(marginComponent);


		List<InterestDate> simpleBaseList = reportTest.getInterestRates();
		assertThat(simpleBaseList, is(notNullValue()));
		assertThat(simpleBaseList.size(), is(3));
		assertThat(simpleBaseList.get(0).getEffectiveDate(), is(rateDate1));
		assertThat(simpleBaseList.get(0).getInterestRate(), equalTo(new BigDecimal("1.0")));
		assertThat(simpleBaseList.get(1).getEffectiveDate(), is(rateDate2));
		assertThat(simpleBaseList.get(1).getInterestRate(), equalTo(new BigDecimal("1.5")));
		assertThat(simpleBaseList.get(2).getEffectiveDate(), is(rateDate3));
		assertThat(simpleBaseList.get(2).getInterestRate(), equalTo(new BigDecimal("1.7")));
	}

	@Test
	public void testInterestRates_withMarginBaseAndSpecial ()throws Exception
	{
		CashReportImpl reportTest = new CashReportImpl();

		List<InterestDate> base = new ArrayList<>();
		base.add(base2);
		base.add(base4);
		base.add(base3);
		base.add(base1);
		CashRateComponent baseComponent = new CashRateComponentTestImpl("base rates id", "base", base);
		reportTest.setBaseCashRateComponent(baseComponent);

		List<InterestDate> margin = new ArrayList<>();
		margin.add(margin2);
		margin.add(margin3);
		margin.add(margin1);
		CashRateComponent marginComponent = new CashRateComponentTestImpl("margin contrib", "margin", margin);
		reportTest.setMarginCashRateComponent(marginComponent);

		List<InterestDate> special = new ArrayList<>();
		special.add(special2);
		special.add(special1);
		special.add(special3);
		CashRateComponent specialComponent = new CashRateComponentTestImpl("special contrib", "special", special);
		reportTest.setSpecialCashRateComponent(specialComponent);

		List<InterestDate> simpleBaseList = reportTest.getInterestRates();
		assertThat(simpleBaseList, is(notNullValue()));
		assertThat(simpleBaseList.size(), is(7));
		assertThat(simpleBaseList.get(0).getEffectiveDate(), is(rateDate1));
		assertThat(simpleBaseList.get(0).getInterestRate(), equalTo(new BigDecimal("1.0")));
		assertThat(simpleBaseList.get(1).getEffectiveDate(), is(rateDate2));
		assertThat(simpleBaseList.get(1).getInterestRate(), equalTo(new BigDecimal("1.6")));
		assertThat(simpleBaseList.get(2).getEffectiveDate(), is(rateDate3));
		assertThat(simpleBaseList.get(2).getInterestRate(), equalTo(new BigDecimal("1.8")));
		assertThat(simpleBaseList.get(3).getEffectiveDate(), is(rateDate4));
		assertThat(simpleBaseList.get(3).getInterestRate(), equalTo(new BigDecimal("1.8")));
		assertThat(simpleBaseList.get(4).getEffectiveDate(), is(rateDate5));
		assertThat(simpleBaseList.get(4).getInterestRate(), equalTo(new BigDecimal("2.0")));
		assertThat(simpleBaseList.get(5).getEffectiveDate(), is(rateDate6));
		assertThat(simpleBaseList.get(5).getInterestRate(), equalTo(new BigDecimal("1.8")));
		assertThat(simpleBaseList.get(6).getEffectiveDate(), is(rateDate7));
		assertThat(simpleBaseList.get(6).getInterestRate(), equalTo(new BigDecimal("1.55")));



	}

	class InterestDateTestImpl implements InterestDate
	{

		DateTime effectiveFrom;
		BigDecimal rate;

		InterestDateTestImpl(DateTime effectiveFrom, BigDecimal rate )
		{
			this.effectiveFrom = effectiveFrom;
			this.rate = rate;

		}


		@Override public DateTime getEffectiveDate()
		{
			return this.effectiveFrom;
		}

		@Override public BigDecimal getInterestRate()
		{
			return this.rate;
		}
	}


	private class CashRateComponentTestImpl implements CashRateComponent
	{

		private String rateId;
		private String componentName;
		private List<InterestDate> interestDates;

		private CashRateComponentTestImpl(String rateId,String componentName, List<InterestDate> interestDates )
		{
			this.rateId = rateId;
			this.componentName = componentName;
			this.interestDates = interestDates;

		}

		@Override public String getCashRateComponentId()
		{
			return rateId;
		}

		@Override public String getCashRateComponentName()
		{
			return componentName;
		}

		@Override public List<InterestDate> getInterestDates()
		{
			return interestDates;
		}

		@Override public BigDecimal getSummatedRate()
		{
			return null;
		}
	}



}
