package com.bt.nextgen.clients.util;

import com.bt.nextgen.clients.api.model.AddressDto;
import com.bt.nextgen.clients.domain.ClientDomainList;
import com.bt.nextgen.clients.web.model.ClientModel;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.core.web.model.AddressModel;
import com.bt.nextgen.portfolio.web.model.StatementDetailModel;
import com.bt.nextgen.portfolio.web.model.StatementTypeModel;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class ClientUtilTest
{

	@Test
	public void testConvertClientStatementDomainToModel_Qrtly() throws Exception
	{
		Date effectiveDate = ApiFormatter.parseDate("30 Jun 2013").toDate();
		Map <String, StatementTypeModel> statementTypeModelMap = new HashMap <>();
		ClientUtil.convertClientStatementDomainToModel("STMQTR", "100951", effectiveDate, statementTypeModelMap);
		StatementTypeModel qrtlyStmtModel = statementTypeModelMap.get(Attribute.QRTLY_STMTS);
		List <StatementDetailModel> statementDetailModels = qrtlyStmtModel.getStatementDetailList();
		assertThat(statementTypeModelMap, Is.is(notNullValue()));
		assertThat(qrtlyStmtModel.getStatementType(), Is.is(Attribute.QRTLY_STMTS));
		assertThat(statementDetailModels.size(), Is.is(1));
	}

	@Test
	public void testConvertClientStatementDomainToModel_QrtlyPayg() throws Exception
	{
		Date effectiveDate = ApiFormatter.parseDate("30 Jun 2013").toDate();
		Map <String, StatementTypeModel> statementTypeModelMap = new HashMap <>();
		ClientUtil.convertClientStatementDomainToModel("STPAYG", "100951", effectiveDate, statementTypeModelMap);
		StatementTypeModel qrtlyStmtModel = statementTypeModelMap.get(Attribute.QRTLY_PAYG_STMTS);
		List <StatementDetailModel> statementDetailModels = qrtlyStmtModel.getStatementDetailList();
		assertThat(statementTypeModelMap, Is.is(notNullValue()));
		assertThat(qrtlyStmtModel.getStatementType(), Is.is(Attribute.QRTLY_PAYG_STMTS));
		assertThat(statementDetailModels.size(), Is.is(1));
	}

	@Test
	public void testConvertClientStatementDomainToModel_Estatement() throws Exception
	{
		Date effectiveDate = ApiFormatter.parseDate("30 Jun 2013").toDate();
		Map <String, StatementTypeModel> statementTypeModelMap = new HashMap <>();
		ClientUtil.convertClientStatementDomainToModel("EMAILF", "100951", effectiveDate, statementTypeModelMap);
		StatementTypeModel qrtlyStmtModel = statementTypeModelMap.get(Attribute.ACCOUNT_CONFIRMATIONS);
		List <StatementDetailModel> statementDetailModels = qrtlyStmtModel.getStatementDetailList();
		assertThat(statementTypeModelMap, Is.is(notNullValue()));
		assertThat(qrtlyStmtModel.getStatementType(), Is.is(Attribute.ACCOUNT_CONFIRMATIONS));
		assertThat(statementDetailModels.size(), Is.is(1));
		StatementDetailModel statementDetailModel = statementDetailModels.get(0);
		assertThat(statementDetailModel.getPeriodTypeStatement(), Is.is(Attribute.FAILURE_NOTIFICATION));

	}

	@Test
	public void testConvertClientStatementDomainToModel_ExitClosure() throws Exception
	{
		Date effectiveDate = ApiFormatter.parseDate("30 Jun 2013").toDate();
		Map <String, StatementTypeModel> statementTypeModelMap = new HashMap <>();
		ClientUtil.convertClientStatementDomainToModel("EXTCLO", "100951", effectiveDate, statementTypeModelMap);
		StatementTypeModel qrtlyStmtModel = statementTypeModelMap.get(Attribute.ACCOUNT_CONFIRMATIONS);
		List <StatementDetailModel> statementDetailModels = qrtlyStmtModel.getStatementDetailList();
		assertThat(statementTypeModelMap, Is.is(notNullValue()));
		assertThat(qrtlyStmtModel.getStatementType(), Is.is(Attribute.ACCOUNT_CONFIRMATIONS));
		assertThat(statementDetailModels.size(), Is.is(1));
		StatementDetailModel statementDetailModel = statementDetailModels.get(0);
		assertThat(statementDetailModel.getPeriodTypeStatement(), Is.is(Attribute.EXIT_STMTS));

	}

	@Test
	public void testToClientModel() throws Exception
	{
		ClientDomainList clientDomainList = (ClientDomainList)JaxbUtil.unmarshall("/webservices/response/AdvanceClientSearchResponse.xml",
			ClientDomainList.class);
		List <ClientModel> clientModels = ClientUtil.toClientModel(clientDomainList.getClients());
		assertThat(clientModels, Is.is(notNullValue()));
		assertThat(clientModels.size(), Is.is(1));
		assertThat(clientModels.get(0).getWrapAccounts().get(0).getAccountType(), Is.is("Individual"));
	}

	@Test
	public void testgetAddressDetails()
	{
		AddressModel address = new AddressModel();
		address.setAddressLine1("SEC LANE");
		address.setAddressLine2("18 NEWCITY Cl");
		address.setFloorNumber("20");
		address.setCity("NEWCITY");
		address.setCountry("Canada");
		address.setBuildingName("High Rise");
		address.setPin("3024");
		address.setSuburb("SEC LANE");

		AddressDto addressDto = ClientUtil.getAddressDetails(address);

		assertNotNull(addressDto);
		assertThat(addressDto.getAddressLine1(), Is.is("SEC LANE"));
		assertThat(addressDto.getAddressLine2(), Is.is("18 NEWCITY Cl"));
		assertThat(addressDto.getFloorNumber(), Is.is("20"));
		assertThat(addressDto.getCity(), Is.is("NEWCITY"));
		assertThat(addressDto.getCountry(), Is.is("Canada"));
		assertThat(addressDto.getBuildingName(), Is.is("High Rise"));
		assertThat(addressDto.getPin(), Is.is("3024"));
		assertThat(addressDto.getSuburb(), Is.is("SEC LANE"));

	}

	@Test
	public void testgetAddressModelDetails()
	{
		AddressDto address = new AddressDto();
		address.setAddressLine1("SEC LANE");
		address.setAddressLine2("18 NEWCITY Cl");
		address.setFloorNumber("20");
		address.setCity("NEWCITY");
		address.setCountry("Canada");
		address.setBuildingName("High Rise");
		address.setPin("3024");
		address.setSuburb("SEC LANE");

		AddressModel addressModel = ClientUtil.getAddressModelDetails(address);
		assertNotNull(addressModel);
		assertThat(addressModel.getAddressLine1(), Is.is("SEC LANE"));
		assertThat(addressModel.getAddressLine2(), Is.is("18 NEWCITY Cl"));
		assertThat(addressModel.getFloorNumber(), Is.is("20"));
		assertThat(addressModel.getCity(), Is.is("NEWCITY"));
		assertThat(addressModel.getCountry(), Is.is("Canada"));
		assertThat(addressModel.getBuildingName(), Is.is("High Rise"));
		assertThat(addressModel.getPin(), Is.is("3024"));
		assertThat(addressModel.getSuburb(), Is.is("SEC LANE"));
	}
}
