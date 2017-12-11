package com.bt.nextgen.core.reporting;

import com.bt.nextgen.cms.CmsEntry;
import com.bt.nextgen.cms.service.CmsService;
import org.junit.Test;
import org.mockito.Mockito;

import static com.bt.nextgen.core.reporting.ReportIdentity.ReportIdentityString.asIdentity;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReportCmsEntryImplTest
{
	@Test
	public void testIsAvailable() throws Exception
	{
        ReportCmsEntryImpl cmsEntry = new ReportCmsEntryImpl(CmsService.MISSING, "baseDir", asIdentity("anything"));
		assertThat(cmsEntry.isAvailable(), equalTo(false));

        cmsEntry = new ReportCmsEntryImpl(mock(CmsEntry.class), "baseDir", asIdentity("anything"));
		assertThat(cmsEntry.isAvailable(), equalTo(true));
	}

	@Test
	public void testGetType() throws Exception
	{
		CmsEntry entry = mock(CmsEntry.class);
		when(entry.getMetaData(eq("contentType"))).thenReturn("application/xml");
        assertThat(new ReportCmsEntryImpl(entry, "baseDir", asIdentity("any")).getType(), equalTo("application/xml"));

		when(entry.getMetaData(eq("contentType"))).thenReturn("application/vnd.jasper");
        assertThat(new ReportCmsEntryImpl(entry, "baseDir", asIdentity("any")).getType(), equalTo("application/vnd.jasper"));
	}

	@Test
	public void testGetAsStream() throws Exception
	{
		CmsEntry entry = mock(CmsEntry.class);
        Mockito.when(entry.getValue()).thenReturn("cms-test.xml");
        assertNotNull(new ReportCmsEntryImpl(entry, "", asIdentity("any")).getAsStream());
	}
}
