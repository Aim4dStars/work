package com.bt.nextgen.logon.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.Matchers.*;
import org.hamcrest.core.Is;
import org.junit.Test;

import com.bt.nextgen.core.web.model.PhoneModel;
import com.bt.nextgen.web.controller.cash.util.Attribute;

public class LogonControllerUtilTest
{

	@Test
	public void testGetMobileList()
	{
		List <PhoneModel> mobileList = LogonControllerUtil.getMobileList(null);
		assertThat(mobileList, nullValue());

		List <PhoneModel> phoneModelList = new ArrayList <PhoneModel>();
		PhoneModel testPhoneModel1 = new PhoneModel();
		testPhoneModel1.setPhoneNumber("9999199990");
		testPhoneModel1.setPrimary(true);
		testPhoneModel1.setType(Attribute.LANDLINE);
		phoneModelList.add(testPhoneModel1);
		mobileList = LogonControllerUtil.getMobileList(phoneModelList);
		assertThat(mobileList, nullValue());

		PhoneModel testPhoneModel2 = new PhoneModel();
		testPhoneModel2.setPhoneNumber("9999199990");
		testPhoneModel2.setPrimary(true);
		testPhoneModel2.setType(Attribute.MOBILE);
		phoneModelList.add(testPhoneModel2);

		mobileList = LogonControllerUtil.getMobileList(phoneModelList);
		assertThat(mobileList, notNullValue());
		assertThat(mobileList.size(), Is.is(1));

		for (PhoneModel phoneModel : mobileList)
		{
			assertThat(phoneModel.getPhoneNumber(), Is.is("9999199990"));
			assertThat(phoneModel.isPrimary(), Is.is(true));
			assertThat(phoneModel.getType(), Is.is(Attribute.MOBILE));
		}

	}

	@Test
	public void testGetLandLineList()
	{
		List <PhoneModel> landLineList = LogonControllerUtil.getLandLineList(null);
		assertThat(landLineList, nullValue());

		List <PhoneModel> phoneModelList = new ArrayList <PhoneModel>();
		PhoneModel testPhoneModel1 = new PhoneModel();
		testPhoneModel1.setPhoneNumber("9999199990");
		testPhoneModel1.setPrimary(true);
		testPhoneModel1.setType(Attribute.MOBILE);
		phoneModelList.add(testPhoneModel1);
		landLineList = LogonControllerUtil.getLandLineList(phoneModelList);
		assertThat(landLineList, nullValue());

		PhoneModel testPhoneModel2 = new PhoneModel();
		testPhoneModel2.setPhoneNumber("9999199990");
		testPhoneModel2.setPrimary(true);
		testPhoneModel2.setType(Attribute.LANDLINE);
		phoneModelList.add(testPhoneModel2);

		landLineList = LogonControllerUtil.getLandLineList(phoneModelList);
		assertThat(landLineList, notNullValue());
		assertThat(landLineList.size(), Is.is(1));

		for (PhoneModel phoneModel : landLineList)
		{
			assertThat(phoneModel.getPhoneNumber(), Is.is("9999199990"));
			assertThat(phoneModel.isPrimary(), Is.is(true));
			assertThat(phoneModel.getType(), Is.is(Attribute.LANDLINE));
		}

	}

	@Test
	public void testGetPhoneModelListByType()
	{
		List <PhoneModel> phoneModelListToTest = LogonControllerUtil.getPhoneModelListByType(null, null);
		assertThat(phoneModelListToTest, nullValue());

		phoneModelListToTest = LogonControllerUtil.getPhoneModelListByType(null, Attribute.LANDLINE);
		assertThat(phoneModelListToTest, nullValue());

		List <PhoneModel> phoneModelList = new ArrayList <PhoneModel>();
		PhoneModel testPhoneModel1 = new PhoneModel();
		testPhoneModel1.setPhoneNumber("9999199990");
		testPhoneModel1.setPrimary(true);
		testPhoneModel1.setType(Attribute.MOBILE);
		phoneModelList.add(testPhoneModel1);
		
		phoneModelListToTest = LogonControllerUtil.getPhoneModelListByType(phoneModelList, "");
		assertThat(phoneModelListToTest, nullValue());
		
		phoneModelListToTest = LogonControllerUtil.getPhoneModelListByType(phoneModelList, Attribute.MOBILE);
		assertThat(phoneModelListToTest, notNullValue());
		for (PhoneModel phoneModel : phoneModelListToTest)
		{
			assertThat(phoneModel.getPhoneNumber(), Is.is("9999199990"));
			assertThat(phoneModel.isPrimary(), Is.is(true));
			assertThat(phoneModel.getType(), Is.is(Attribute.MOBILE));
		}
		
		PhoneModel testPhoneModel2 = new PhoneModel();
		testPhoneModel2.setPhoneNumber("9999191190");
		testPhoneModel2.setPrimary(true);
		testPhoneModel2.setType(Attribute.LANDLINE);
		phoneModelList.add(testPhoneModel2);
		
		phoneModelListToTest = LogonControllerUtil.getPhoneModelListByType(phoneModelList, Attribute.LANDLINE);
		assertThat(phoneModelListToTest, notNullValue());
		for (PhoneModel phoneModel : phoneModelListToTest)
		{
			assertThat(phoneModel.getPhoneNumber(), Is.is("9999191190"));
			assertThat(phoneModel.isPrimary(), Is.is(true));
			assertThat(phoneModel.getType(), Is.is(Attribute.LANDLINE));
		}

	}

}
