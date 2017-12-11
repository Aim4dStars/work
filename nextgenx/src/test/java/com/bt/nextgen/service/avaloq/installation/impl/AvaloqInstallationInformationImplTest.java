package com.bt.nextgen.service.avaloq.installation.impl;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.extractor.ResponseExtractor;

public class AvaloqInstallationInformationImplTest
{

	String simpleRelease = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
		"<rep:rep xmlns:rep=\"http://www.avaloq.com/abs/screen_rep/hira/btfg$task_ui_chg_all_chg\" xmlns:repb=\"http://www.avaloq.com/abs/screen_rep/base\" xmlns:fldb=\"http://www.avaloq.com/abs/bb/fld_def\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
		"  <!-- 2016-01-15 12:17:01.973301000 on afd034a by AVALOQ -->\n" +
		"  <metadata>\n" +
		"    <empty_rep>\n" +
		"      <val>false</val>\n" +
		"    </empty_rep>\n" +
		"    <current_of_time>\n" +
		"      <val>2016-01-15T01:17:01+11:00</val>\n" +
		"    </current_of_time>\n" +
		"  </metadata>\n" +
		"  <def>\n" +
		"    <version>2.0</version>\n" +
		"    <rep_def>\n" +
		"      <style>hira</style>\n" +
		"      <lang id=\"1\">en</lang>\n" +
		"      <hira_def>\n" +
		"        <grp_list>\n" +
		"          <grp_def>\n" +
		"            <name>release</name>\n" +
		"            <hira>1</hira>\n" +
		"            <has_head>true</has_head>\n" +
		"            <has_foot>false</has_foot>\n" +
		"          </grp_def>\n" +
		"          <grp_def>\n" +
		"            <name>chg</name>\n" +
		"            <hira>2</hira>\n" +
		"            <has_head>true</has_head>\n" +
		"            <has_foot>false</has_foot>\n" +
		"          </grp_def>\n" +
		"        </grp_list>\n" +
		"      </hira_def>\n" +
		"      <row_def>\n" +
		"        <fld_list>\n" +
		"          <fld_def>\n" +
		"            <name>release</name>\n" +
		"            <type>TextFld</type>\n" +
		"            <lbl>release</lbl>\n" +
		"          </fld_def>\n" +
		"          <fld_def>\n" +
		"            <name>chg_id</name>\n" +
		"            <type>NrFld</type>\n" +
		"            <lbl>chg_id</lbl>\n" +
		"          </fld_def>\n" +
		"          <fld_def>\n" +
		"            <name>chg_name</name>\n" +
		"            <type>TextFld</type>\n" +
		"            <lbl>chg_name</lbl>\n" +
		"          </fld_def>\n" +
		"          <fld_def>\n" +
		"            <name>inst_time</name>\n" +
		"            <type>DateTimeFld</type>\n" +
		"            <lbl>inst_time</lbl>\n" +
		"          </fld_def>\n" +
		"          <fld_def>\n" +
		"            <name>task_id</name>\n" +
		"            <type>NrFld</type>\n" +
		"            <lbl>task_id</lbl>\n" +
		"          </fld_def>\n" +
		"          <fld_def>\n" +
		"            <name>task_name</name>\n" +
		"            <type>TextFld</type>\n" +
		"            <lbl>task_name</lbl>\n" +
		"          </fld_def>\n" +
		"          <fld_def>\n" +
		"            <name>user_1</name>\n" +
		"            <type>TextFld</type>\n" +
		"            <lbl>user_1</lbl>\n" +
		"          </fld_def>\n" +
		"          <fld_def>\n" +
		"            <name>user_2</name>\n" +
		"            <type>TextFld</type>\n" +
		"            <lbl>user_2</lbl>\n" +
		"          </fld_def>\n" +
		"          <fld_def>\n" +
		"            <name>user_3</name>\n" +
		"            <type>TextFld</type>\n" +
		"            <lbl>user_3</lbl>\n" +
		"          </fld_def>\n" +
		"          <fld_def>\n" +
		"            <name>user_4</name>\n" +
		"            <type>TextFld</type>\n" +
		"            <lbl>user_4</lbl>\n" +
		"          </fld_def>\n" +
		"        </fld_list>\n" +
		"      </row_def>\n" +
		"    </rep_def>\n" +
		"  </def>\n" +
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
		"                <chg_name>\n" +
		"                  <val>Avaloq Value Stream: change_list interface</val>\n" +
		"                </chg_name>\n" +
		"                <inst_time>\n" +
		"                  <val>2016-01-15T11:32:58+11:00</val>\n" +
		"                </inst_time>\n" +
		"                <task_id>\n" +
		"                  <val>2297783</val>\n" +
		"                </task_id>\n" +
		"                <task_name>\n" +
		"                  <val>Avaloq Value Stream: change_list interface</val>\n" +
		"                </task_name>\n" +
		"                <user_1>\n" +
		"                  <val>Remund Alain</val>\n" +
		"                </user_1>\n" +
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
		"          </chg>\n" +
		"        </chg_list>\n" +
		"      </release>\n" +
		"    </release_list>\n" +
		"  </data>\n" +
		"</rep:rep>";


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

	String differentRelease = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
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
		"                  <val>1622248</val>\n" +
		"                </chg_id>\n" +
		"                <inst_time>\n" +
		"                  <val>2016-01-15T11:32:58+11:00</val>\n" +
		"                </inst_time>\n" +
		"              </chg_head>\n" +
		"            </chg_head_list>\n" +
		"          </chg>\n" +
		" <chg>"+
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

	@Test
	public void testParsing() throws Exception
	{
		ResponseExtractor<AvaloqInstallationInformationImpl> extractor = new DefaultResponseExtractor<>(AvaloqInstallationInformationImpl.class);
		AvaloqInstallationInformationImpl installationInformation = extractor.extractData(simpleRelease);
		assertThat(installationInformation,is(notNullValue()));

		assertThat(installationInformation.getAvaloqReleasePackages(),is(notNullValue()));
		assertThat(installationInformation.getAvaloqReleasePackages().size(),is(2));
		assertThat(installationInformation.getAvaloqReleasePackages().get(0).getAvaloqReleaseName(),is("BTFG41_R1.IN"));

	}

	@Test
	public void testUniquenessOfVersion() throws Exception
	{
		ResponseExtractor<AvaloqInstallationInformationImpl> extractor = new DefaultResponseExtractor<>(AvaloqInstallationInformationImpl.class);
		AvaloqInstallationInformationImpl installationInformation = extractor.extractData(simpleRelease);

		assertThat(installationInformation.getInstallationUid(),is(notNullValue()));
		assertThat(installationInformation.getInstallationUid(),is(not(containsString("STABLE"))));

		AvaloqInstallationInformationImpl epsInstallationInformation = extractor.extractData(epsRelease);

		assertThat(installationInformation.getInstallationUid(),is(epsInstallationInformation.getInstallationUid()));

		AvaloqInstallationInformationImpl differentInstallation = extractor.extractData(differentRelease);
		assertThat(differentInstallation.getInstallationUid(), is(not(installationInformation.getInstallationUid())));


	}

}