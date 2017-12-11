package com.bt.nextgen.api.version.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.extractor.ResponseExtractor;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.installation.AvaloqVersionIntegrationService;
import com.bt.nextgen.service.avaloq.installation.impl.AvaloqInstallationInformationImpl;

@RunWith(MockitoJUnitRunner.class)
public class VersionServiceImplTest
{

	String epsRelease = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
		"<rep:rep xmlns:rep=\"http://www.avaloq.com/abs/screen_rep/hira/btfg$task_ui_chg_all_chg\" xmlns:repb=\"http://www.avaloq.com/abs/screen_rep/base\" xmlns:fldb=\"http://www.avaloq.com/abs/bb/fld_def\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
		"  <!-- 2016-01-15 12:17:01.973301000 on afd034a by AVALOQ -->\n" +
		"  <data>\n" +
		"    <release_list>\n" +
		"      <release>\n" +
		"        <release_head_list>\n" +
		"          <release_head id=\"2-1\" row_type=\"title\" hira=\"1\" level=\"release.head\">\n" +
		"            <release>\n" +
		"              <val>BTFG41_R1.IN</val>\n" +
		"            </release>\n" +
		"          </release_head>\n" +
		"        </release_head_list>\n" +
		"        <chg_list>\n" +
		"          <chg>\n" +
		"            <chg_head_list>\n" +
		"              <chg_head id=\"4-2\" row_type=\"data\" hira=\"2\" level=\"chg.head\">\n" +
		"                <chg_id>\n" +
		"                  <val>1685848</val>\n" +
		"                </chg_id>\n" +
		"                <inst_time>\n" +
		"                  <val>2016-01-15T11:32:58+11:00</val>\n" +
		"                </inst_time>\n" +
		"              </chg_head>\n" +
		"            </chg_head_list>\n" +
		"          </chg>\n" +
		"        </chg_list>\n" +
		"      </release>\n" +
		"      <release>\n" +
		"        <release_head_list>\n" +
		"          <release_head id=\"6-3\" row_type=\"title\" hira=\"1\" level=\"release.head\">\n" +
		"            <release>\n" +
		"              <val>BTFG41_R1.STABLE</val>\n" +
		"            </release>\n" +
		"          </release_head>\n" +
		"        </release_head_list>\n" +
		"        <chg_list>\n" +
		"          <chg>\n" +
		"            <chg_head_list>\n" +
		"              <chg_head id=\"8-4\" row_type=\"data\" hira=\"2\" level=\"chg.head\">\n" +
		"                <chg_id>\n" +
		"                  <val>1685829</val>\n" +
		"                </chg_id>\n" +
		"                <inst_time>\n" +
		"                  <val>2016-01-14T08:17:29+11:00</val>\n" +
		"                </inst_time>\n" +
		"              </chg_head>\n" +
		"            </chg_head_list>\n" +
		"          </chg>\n" +
		"        </chg_list>\n" +
		"      </release>\n" +
		"    </release_list>\n" +
		"  </data>\n" +
		"</rep:rep>";


	@InjectMocks
	VersionServiceImpl versionService;

	@Mock
	AvaloqVersionIntegrationService avaloqVersionIntegrationService;

	@Before
	public void setupMocks() throws Exception
	{
		ResponseExtractor<AvaloqInstallationInformationImpl> extractor = new DefaultResponseExtractor<>(AvaloqInstallationInformationImpl.class);
		AvaloqInstallationInformationImpl installationInformation = extractor.extractData(epsRelease);
		when(avaloqVersionIntegrationService.getAvaloqInstallInformation(any(ServiceErrors.class))).thenReturn(installationInformation);
	}

	@Test public void testCreateVersionString() throws Exception
	{

		String result = versionService.getFullAvaloqVersion();
		assertThat(result, is(notNullValue()));
		assertThat(result, is("Avaloq Version ID:509d54d4b3439d6324f6945371aa7629\n\rRelease name :BTFG41_R1.IN,Release version :fb87d33eb0b172f61380bca5da5dbdad\n\rChanges :1685848,\n\r\n\rRelease name :BTFG41_R1.STABLE,Release version :be1251678a6348a36612e3471be8db9c\n\rChanges :1685829,\n\r\n\r"));
	}
}