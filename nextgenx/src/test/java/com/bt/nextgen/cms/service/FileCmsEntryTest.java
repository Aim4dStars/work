package com.bt.nextgen.cms.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.bt.nextgen.cms.CmsEntry;

public class FileCmsEntryTest
{
	private CmsEntry pretendFileEntry;
	private CmsEntry textEntry;

	@Before
	public void setup()
	{
		pretendFileEntry = mock(CmsEntry.class);
		when(pretendFileEntry.getMetaData("type")).thenReturn("url");
		when(pretendFileEntry.getValue()).thenReturn("/helloWorld.txt");

		textEntry = mock(CmsEntry.class);
		when(textEntry.getMetaData("type")).thenReturn("other");
		when(textEntry.getValue()).thenReturn("hello world!");

	}

	@Test
	public void typeUrl_shouldBeWrapped() throws Exception
	{
		assertThat(FileCmsEntry.wrapIfValueUrl(pretendFileEntry), not(equalTo(pretendFileEntry)));
	}

	@Test
	public void allOtherTypes_notWrapped() throws Exception
	{
		assertThat(FileCmsEntry.wrapIfValueUrl(textEntry), equalTo(textEntry));
	}

	@Ignore("sammutj - TODO fix this, currently the cms setting isn't plugged into CFI effectively")
	@Test
	public void weirdSpaceInFilename()
	{
		CmsEntry spacedFileEntry = mock(CmsEntry.class);
		when(spacedFileEntry.getMetaData("type")).thenReturn("url");
		when(spacedFileEntry.getValue()).thenReturn("\t /helloWorld.txt\n");

		assertThat(FileCmsEntry.wrapIfValueUrl(spacedFileEntry).getValue(), is("hello world!"));

	}

	@Test
	public void metaGivenOffWrappedEntry() throws Exception
	{
		CmsEntry wrapped = FileCmsEntry.wrapIfValueUrl(pretendFileEntry);

		wrapped.getMetaData("random");

		verify(pretendFileEntry, times(1)).getMetaData("random");

	}

	@Test
	public void valueIsContentOfFile() throws Exception
	{
		assertThat(textEntry.getValue(), is("hello world!"));

	}

	@Ignore("sammutj - TODO fix this, currently the cms setting isn't plugged into CFI effectively")
	@Test
	public void streamsFileContents() throws Exception
	{
		String actual = new String(IOUtils.toByteArray(FileCmsEntry.wrapIfValueUrl(pretendFileEntry).getStream()));
		assertThat(actual, is("hello world!"));

	}
}
