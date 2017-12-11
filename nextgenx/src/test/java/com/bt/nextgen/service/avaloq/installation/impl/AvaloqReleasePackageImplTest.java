package com.bt.nextgen.service.avaloq.installation.impl;


import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.service.avaloq.installation.AvaloqReleasePackage;

public class AvaloqReleasePackageImplTest
{

	String releaseString = "<release>\n" +
		"<release_head_list>\n" +
		"<release_head id=\"2-1\" row_type=\"title\" hira=\"1\" level=\"release.head\">\n" +
		"<release><val>BTFG310_IP30</val></release>\n" +
		"</release_head>\n" +
		"</release_head_list>\n" +
		"<chg_list>\n" +
		"<chg>\n" +
		"<chg_head_list>\n" +
		"<chg_head id=\"4-2\" row_type=\"data\" hira=\"2\" level=\"chg.head\">\n" +
		"<chg_id><val>1617544</val></chg_id>\n" +
		"<chg_name><val>QC11457 - PERF: fix null twrr in service where period start is last day of TD</val></chg_name>\n" +
		"<inst_time><val>2016-01-28T01:46:59+11:00</val></inst_time>\n" +
		"<task_id><val>2236208</val></task_id>\n" +
		"<task_name><val>QC11457 - PERF: fix null twrr in service where period start is last day of TD</val></task_name>\n" +
		"<user_1><val>Young Adam</val></user_1>\n" +
		"</chg_head>\n" +
		"</chg_head_list>\n" +
		"</chg>\n" +
		"<chg>\n" +
		"<chg_head_list>\n" +
		"<chg_head id=\"6-3\" row_type=\"data\" hira=\"2\" level=\"chg.head\">\n" +
		"<chg_id><val>1617584</val></chg_id>\n" +
		"<chg_name><val>QC11507 - trx history for RIP</val></chg_name>\n" +
		"<inst_time><val>2016-01-28T01:46:59+11:00</val></inst_time>\n" +
		"<task_id><val>2236244</val></task_id>\n" +
		"<task_name><val>QC - November release</val></task_name>\n" +
		"<user_1><val>Roth Patric</val></user_1>\n" +
		"<user_2><val>Lei Liu</val></user_2>\n" +
		"</chg_head>\n" +
		"</chg_head_list>\n" +
		"</chg>\n" +
		"<chg>\n" +
		"<chg_head_list>\n" +
		"<chg_head id=\"8-4\" row_type=\"data\" hira=\"2\" level=\"chg.head\">\n" +
		"<chg_id><val>1617589</val></chg_id>\n" +
		"<chg_name><val>US1381 - Credit check and queuing</val></chg_name>\n" +
		"<inst_time><val>2016-01-28T01:46:59+11:00</val></inst_time>\n" +
		"<task_id><val>2236245</val></task_id>\n" +
		"<task_name><val>QC - November release</val></task_name>\n" +
		"<user_1><val>Roth Patric</val></user_1>\n" +
		"</chg_head>\n" +
		"</chg_head_list>\n" +
		"</chg>\n" +
		"<chg>\n" +
		"<chg_head_list>\n" +
		"<chg_head id=\"10-5\" row_type=\"data\" hira=\"2\" level=\"chg.head\">\n" +
		"<chg_id><val>1617601</val></chg_id>\n" +
		"<chg_name><val>QC11468 - Cost rules</val></chg_name>\n" +
		"<inst_time><val>2016-01-28T01:46:59+11:00</val></inst_time>\n" +
		"<task_id><val>2236262</val></task_id>\n" +
		"<task_name><val>QC - November release</val></task_name>\n" +
		"<user_1><val>Roth Patric</val></user_1>\n" +
		"<user_2><val>Lei Liu</val></user_2>\n" +
		"</chg_head>\n" +
		"</chg_head_list>\n" +
		"</chg>" +
		"</chg_list>" +
		"</release>";

	String epsReleaseString = "<release>\n" +
		"<release_head_list>\n" +
		"<release_head id=\"2-1\" row_type=\"title\" hira=\"1\" level=\"release.head\">\n" +
		"<release><val>BTFG310_IP30</val></release>\n" +
		"</release_head>\n" +
		"</release_head_list>\n" +
		"<chg_list>\n" +
		"<chg>\n" +
		"<chg_head_list>\n" +
		"<chg_head id=\"4-2\" row_type=\"data\" hira=\"2\" level=\"chg.head\">\n" +
		"<chg_id><val>1617544</val></chg_id>\n" +
		"<inst_time><val>2016-01-28T01:26:59+11:00</val></inst_time>\n" +
		"</chg_head>\n" +
		"</chg_head_list>\n" +
		"</chg>\n" +
		"<chg>\n" +
		"<chg_head_list>\n" +
		"<chg_head id=\"6-3\" row_type=\"data\" hira=\"2\" level=\"chg.head\">\n" +
		"<chg_id><val>1617584</val></chg_id>\n" +
		"<inst_time><val>2016-01-18T01:46:59+11:00</val></inst_time>\n" +
		"</chg_head>\n" +
		"</chg_head_list>\n" +
		"</chg>\n" +
		"<chg>\n" +
		"<chg_head_list>\n" +
		"<chg_head id=\"10-5\" row_type=\"data\" hira=\"2\" level=\"chg.head\">\n" +
		"<chg_id><val>1617601</val></chg_id>\n" +
		"<inst_time><val>2016-01-28T01:46:44+11:00</val></inst_time>\n" +
		"</chg_head>\n" +
		"</chg_head_list>\n" +
		"</chg>" +
		"<chg>\n" +
		"<chg_head_list>\n" +
		"<chg_head id=\"8-4\" row_type=\"data\" hira=\"2\" level=\"chg.head\">\n" +
		"<chg_id><val>1617589</val></chg_id>\n" +
		"<inst_time><val>2016-01-21T01:46:59+11:00</val></inst_time>\n" +
		"</chg_head>\n" +
		"</chg_head_list>\n" +
		"</chg>\n" +
		"</chg_list>" +
		"</release>";

	String differentReleaseString = "<release>\n" +
		"<release_head_list>\n" +
		"<release_head id=\"2-1\" row_type=\"title\" hira=\"1\" level=\"release.head\">\n" +
		"<release><val>BTFG310_IP30</val></release>\n" +
		"</release_head>\n" +
		"</release_head_list>\n" +
		"<chg_list>\n" +
		"<chg>\n" +
		"<chg_head_list>\n" +
		"<chg_head id=\"4-2\" row_type=\"data\" hira=\"2\" level=\"chg.head\">\n" +
		"<chg_id><val>1617544</val></chg_id>\n" +
		"<inst_time><val>2016-01-28T01:26:59+11:00</val></inst_time>\n" +
		"</chg_head>\n" +
		"</chg_head_list>\n" +
		"</chg>\n" +
		"<chg>\n" +
		"<chg_head_list>\n" +
		"<chg_head id=\"6-3\" row_type=\"data\" hira=\"2\" level=\"chg.head\">\n" +
		"<chg_id><val>1617584</val></chg_id>\n" +
		"<inst_time><val>2016-01-18T01:46:59+11:00</val></inst_time>\n" +
		"</chg_head>\n" +
		"</chg_head_list>\n" +
		"</chg>\n" +
		"</chg_list>" +
		"</release>";

	@Test
	public void testReleaseParsing()throws Exception
	{
		DefaultResponseExtractor<AvaloqReleaseHolder> extractor = new DefaultResponseExtractor<>(AvaloqReleaseHolder.class);

		AvaloqReleaseHolder releaseHolder = extractor.extractData(releaseString);
		AvaloqReleasePackage release = releaseHolder.getReleasePackage();

		assertThat(release,is(notNullValue()));

		assertThat(release.getAvaloqReleaseName(),is("BTFG310_IP30"));

		assertThat(release.getAvaloqChanges(),is(notNullValue()));

		assertThat(release.getAvaloqChanges().size(),is(4));


	}


	@Test
	public void testChangeSum() throws Exception
	{
		DefaultResponseExtractor<AvaloqReleaseHolder> extractor = new DefaultResponseExtractor<>(AvaloqReleaseHolder.class);

		AvaloqReleaseHolder releaseHolder = extractor.extractData(releaseString);
		AvaloqReleasePackage release = releaseHolder.getReleasePackage();

		assertThat(release,is(notNullValue()));

		String md5sum = release.getAvaloqPackageUid();

		assertThat(md5sum,is(notNullValue()));

		assertThat(md5sum,not(containsString("1617584")));

		assertThat(release.getAvaloqPackageUid(),is(md5sum));

		AvaloqReleaseHolder epsReleaseHolder = extractor.extractData(epsReleaseString);

		assertThat(epsReleaseHolder.getReleasePackage().getAvaloqPackageUid(),is(md5sum));

		AvaloqReleaseHolder differentReleaseHolder = extractor.extractData(differentReleaseString);

		assertThat(differentReleaseHolder.getReleasePackage().getAvaloqPackageUid(),is(not((md5sum))));
	}



}