package com.bt.nextgen.service.avaloq.installation.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.nullValue;

import org.joda.time.DateTime;
import org.junit.Test;

import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.extractor.ResponseExtractor;
import com.bt.nextgen.service.avaloq.installation.AvaloqChange;

public class AvaloqChangeImplTest
{

	String singleAuthorChangeXml = "<chg>\n" +
		"            <chg_head_list>\n" +
		"              <chg_head id=\"8-4\" row_type=\"data\" hira=\"2\" level=\"chg.head\">\n" +
		"                <chg_id>\n" +
		"                  <val>1685830</val>\n" +
		"                </chg_id>\n" +
		"                <chg_name>\n" +
		"                  <val>SELF 4eye chk for DEMO_TRX_MP_CTON</val>\n" +
		"                </chg_name>\n" +
		"                <inst_time>\n" +
		"                  <val>2016-01-14T08:17:29+11:00</val>\n" +
		"                </inst_time>\n" +
		"                <task_id>\n" +
		"                  <val>2297765</val>\n" +
		"                </task_id>\n" +
		"                <task_name>\n" +
		"                  <val>SELF: switch off 4eye chk for DEMO_TRX_MP_CTON</val>\n" +
		"                </task_name>\n" +
		"                <user_1>\n" +
		"                  <val>Huvanandana Jacqueline</val>\n" +
		"                </user_1>\n" +
		"              </chg_head>\n" +
		"            </chg_head_list>\n" +
		"          </chg>";

	String changeXml = "<chg>\n" +
		"            <chg_head_list>\n" +
		"              <chg_head id=\"8-4\" row_type=\"data\" hira=\"2\" level=\"chg.head\">\n" +
		"                <chg_id>\n" +
		"                  <val>1685829</val>\n" +
		"                </chg_id>\n" +
		"                <chg_name>\n" +
		"                  <val>SELF 4eye chk for DEMO_TRX_MP_CTON</val>\n" +
		"                </chg_name>\n" +
		"                <inst_time>\n" +
		"                  <val>2016-01-14T08:17:29+11:00</val>\n" +
		"                </inst_time>\n" +
		"                <task_id>\n" +
		"                  <val>2297765</val>\n" +
		"                </task_id>\n" +
		"                <task_name>\n" +
		"                  <val>SELF: switch off 4eye chk for DEMO_TRX_MP_CTON</val>\n" +
		"                </task_name>\n" +
		"                <user_1>\n" +
		"                  <val>Huvanandana Jacqueline</val>\n" +
		"                </user_1>\n" +
		"				 <user_2>\n" +
		"                  <val>Barker Andy</val>\n" +
		"                </user_2>"+
		"				 <user_3>\n" +
		"                  <val>Remund Alain</val>\n" +
		"                </user_3>" +
		"				<user_4>\n" +
		"                  <val>Buechi Martin</val>\n" +
		"                </user_4>"+
		"   				<user_5>\n" +
		"                  <val>Tonini Micheal</val>\n" +
		"                </user_5>"+
		"              </chg_head>\n" +
		"            </chg_head_list>\n" +
		"          </chg>";


	String change3AuthXml = "<chg>\n" +
		"            <chg_head_list>\n" +
		"              <chg_head id=\"8-4\" row_type=\"data\" hira=\"2\" level=\"chg.head\">\n" +
		"                <chg_id>\n" +
		"                  <val>1685828</val>\n" +
		"                </chg_id>\n" +
		"                <chg_name>\n" +
		"                  <val>SELF 4eye chk for DEMO_TRX_MP_CTON</val>\n" +
		"                </chg_name>\n" +
		"                <inst_time>\n" +
		"                  <val>2016-01-14T08:17:29+11:00</val>\n" +
		"                </inst_time>\n" +
		"                <task_id>\n" +
		"                  <val>2297765</val>\n" +
		"                </task_id>\n" +
		"                <task_name>\n" +
		"                  <val>SELF: switch off 4eye chk for DEMO_TRX_MP_CTON</val>\n" +
		"                </task_name>\n" +
		"                <user_1>\n" +
		"                  <val>Huvanandana Jacqueline</val>\n" +
		"                </user_1>\n" +
		"				 <user_2>\n" +
		"                  <val>Barker Andy</val>\n" +
		"                </user_2>"+
		"				 <user_3>\n" +
		"                  <val>Remund Alain</val>\n" +
		"                </user_3>" +
		"              </chg_head>\n" +
		"            </chg_head_list>\n" +
		"          </chg>";

	String epsChangeXMl = "<chg>\n" +
		"<chg_head_list>\n" +
		"<chg_head id=\"8-4\" row_type=\"data\" hira=\"2\" level=\"chg.head\">\n" +
		"<chg_id><val>1575963</val></chg_id>\n" +
		"<inst_time><val>2016-02-01T08:13:06+11:00</val></inst_time>\n" +
		"</chg_head>\n" +
		"</chg_head_list>\n" +
		"</chg>";


	String epsChange3AuthXml = "<chg>\n" +
		"            <chg_head_list>\n" +
		"              <chg_head id=\"8-4\" row_type=\"data\" hira=\"2\" level=\"chg.head\">\n" +
		"                <chg_id>\n" +
		"                  <val>1685828</val>\n" +
		"                </chg_id>\n" +
		"<inst_time><val>2016-01-02T08:13:06+11:00</val></inst_time>\n" +
		"</chg_head>\n" +
		"</chg_head_list>\n" +
		"</chg>";

	@Test
	public void testParsingFullChange() throws Exception
	{
		ResponseExtractor<AvaloqChangeHolder> extractor = new DefaultResponseExtractor<>(AvaloqChangeHolder.class);
		AvaloqChangeHolder testH = extractor.extractData(changeXml);
		AvaloqChange test = testH.getChange();

		assertThat(test,is(not(nullValue())));

		assertThat(test.getId(), is("1685829"));
		assertThat(test.getName(), is("SELF 4eye chk for DEMO_TRX_MP_CTON"));
		assertThat(test.getInstallationTime().getMillis(),is((new DateTime("2016-01-14T08:17:29+11:00")).getMillis()));
		assertThat(test.getTaskId(),is("2297765"));
		assertThat(test.getTaskName(),is("SELF: switch off 4eye chk for DEMO_TRX_MP_CTON"));
		assertThat(test.getAuthors(),is(notNullValue()));
		assertThat(test.getAuthors().get(0),is("Huvanandana Jacqueline"));
		assertThat(test.getAuthors().get(1),is("Barker Andy"));
		assertThat(test.getAuthors().get(2),is("Remund Alain"));
		assertThat(test.getAuthors().get(3),is("Buechi Martin"));
		assertThat(test.getAuthors().get(4),is("Tonini Micheal"));




	}

	@Test
	public void testParsingSingleAuthorChange() throws Exception
	{
		ResponseExtractor<AvaloqChangeHolder> extractor = new DefaultResponseExtractor<>(AvaloqChangeHolder.class);
		AvaloqChangeHolder testH = extractor.extractData(singleAuthorChangeXml);
		AvaloqChange test = testH.getChange();

		assertThat(test,is(not(nullValue())));

		assertThat(test.getId(), is("1685830"));
		assertThat(test.getAuthors(),is(notNullValue()));
		assertThat(test.getAuthors().get(0),is("Huvanandana Jacqueline"));
		assertThat(test.getAuthors().size(),is(1));

	}

	@Test
	public void testParsingThreeAuthorChange() throws Exception
	{
		ResponseExtractor<AvaloqChangeHolder> extractor = new DefaultResponseExtractor<>(AvaloqChangeHolder.class);
		AvaloqChangeHolder testH = extractor.extractData(change3AuthXml);
		AvaloqChange test = testH.getChange();

		assertThat(test,is(not(nullValue())));

		assertThat(test.getId(), is("1685828"));
		assertThat(test.getAuthors(),is(notNullValue()));
		assertThat(test.getAuthors().get(0),is("Huvanandana Jacqueline"));
		assertThat(test.getAuthors().get(1),is("Barker Andy"));
		assertThat(test.getAuthors().get(2),is("Remund Alain"));
		assertThat(test.getAuthors().size(),is(3));

	}

	@Test
	public void testParsingEPSChange() throws Exception
	{
		ResponseExtractor<AvaloqChangeHolder> extractor = new DefaultResponseExtractor<>(AvaloqChangeHolder.class);
		AvaloqChangeHolder testH = extractor.extractData(epsChangeXMl);
		AvaloqChange test = testH.getChange();

		assertThat(test,is(not(nullValue())));

		assertThat(test.getId(), is("1575963"));
		assertThat(test.getAuthors(),is(notNullValue()));
		assertThat(test.getAuthors().size(),is(0));
		assertThat(test.getInstallationTime().getMillis(),is((new DateTime("2016-02-01T08:13:06+11:00")).getMillis()));

	}


	@Test
	public void testCompareFunction()throws Exception
	{
		ResponseExtractor<AvaloqChangeHolder> extractor = new DefaultResponseExtractor<>(AvaloqChangeHolder.class);
		AvaloqChangeHolder testH = extractor.extractData(singleAuthorChangeXml);

		AvaloqChangeHolder testLowerH = extractor.extractData(epsChangeXMl);

		AvaloqChange test = testH.getChange();
		AvaloqChange testLower = testLowerH.getChange();

		assertThat(test.compareTo(testLower), is(greaterThan(0)) );

		assertThat(testLower.compareTo(test), is(lessThan(0)));

		assertThat(testLower.compareTo(test), is (0-test.compareTo(testLower)));

		AvaloqChangeHolder testDuplicateH = extractor.extractData(singleAuthorChangeXml);
		AvaloqChange testDuplicate = testDuplicateH.getChange();

		assertThat(testDuplicate.compareTo(test), is(0));

	}


	@Test
	public void testEqualsFunction() throws Exception
	{
		ResponseExtractor<AvaloqChangeHolder> extractor = new DefaultResponseExtractor<>(AvaloqChangeHolder.class);
		AvaloqChangeHolder epsChangeH = extractor.extractData(epsChange3AuthXml);
		AvaloqChange epsChange = epsChangeH.getChange();

		AvaloqChangeHolder nonEpsChangeH = extractor.extractData(change3AuthXml);
		AvaloqChange nonEpsChange = nonEpsChangeH.getChange();

		assertThat(epsChange.equals(nonEpsChange), is(true));

		assertThat(epsChange.compareTo(nonEpsChange), is(0));

	}

}