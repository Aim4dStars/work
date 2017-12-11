package com.bt.nextgen.content.api.model;

import com.bt.nextgen.cms.CmsEntry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Some basic unit tests for the ContentDto class.
 */
public class ContentDtoTest {

	private ContentDto content;

	private ObjectMapper mapper;

	@Before
	public void initMapper() {
		mapper = new ObjectMapper();
	}

	@Test
	public void contentDtoCanSerialize() {
		assertTrue(mapper.canSerialize(ContentDto.class));
	}

	@Test
	public void toJsonWithRawContent() throws Exception {
		String expected = "{'key':{'contentId':'test-id'},'content':'Raw content','type':'Content'}";
		expected = expected.replace('\'', '"');
		content = new ContentDto("test-id", "Raw content");
		assertEquals(expected, mapper.writeValueAsString(content));
	}

	@Test
	public void toJsonWithCmsEntry() throws Exception {
		String expected = "{'key':{'contentId':'test-id'},'content':'CmsEntry content','type':'Content'}";
		expected = expected.replace('\'', '"');
		CmsEntry entry = mock(CmsEntry.class);
		when(entry.getValue()).thenReturn("CmsEntry content");
		content = new ContentDto("test-id", entry);
		assertEquals(expected, mapper.writeValueAsString(content));
	}
}
