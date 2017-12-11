package com.bt.nextgen.util.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

public class PathBuilderTest
{
	@Test
	public void shouldCreateGetLinkForGetHandler() throws Exception
	{
		String path = com.bt.nextgen.core.web.util.PathBuilder.pathTo(GetHandler.class).withVar("documentId",
			"new").build();
		assertThat(path, is("/new/success.go"));
	}

	@Test
	public void shouldReturnPathAsToString() throws Exception
	{
		String path = com.bt.nextgen.core.web.util.PathBuilder.pathTo(GetHandler.class).withVar("documentId",
			"new").toString();
		assertThat(path, is("/new/success.go"));
	}

	@Test
	public void shouldCreateRedirectGetLinkForGetHandler() throws Exception
	{
		RedirectView path = com.bt.nextgen.core.web.util.PathBuilder.pathTo(GetHandler.class).withVar("documentId",
			"new").redirect();
		assertThat(path.getUrl(), is("/new/success.go"));
	}

	@Test
	public void shouldCreateLinkWithQueryString()
	{
		String path = com.bt.nextgen.core.web.util.PathBuilder.pathTo(GetHandler.class).withVar("documentId",
			"new").withQueryParam("key", "val").build();

		assertThat(path, is("/new/success.go?key=val"));
	}

	@Test
	public void shouldCreatePostLinkToPostHandler() throws Exception
	{
		String path = com.bt.nextgen.core.web.util.PathBuilder.pathTo(PostHandler.class).POST().withVar("documentId",
			"old").build();
		assertThat(path, is("/old/error"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailToCreatePostLinkForGetHandler() throws Exception
	{
		com.bt.nextgen.core.web.util.PathBuilder.pathTo(GetHandler.class).POST().withVar("documentId", "new").build();
	}

	@Test
	public void shouldCreateLinkToNamedMethod() throws Exception
	{
		String path = com.bt.nextgen.core.web.util.PathBuilder.pathTo(PostHandler.class).withMethod(
			"handlePostRequest").withVar("documentId", "old").build();

		assertThat(path, is("/old/error"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailCreateLinkToAnUnAnnotatedNamedMethod() throws Exception
	{
		com.bt.nextgen.core.web.util.PathBuilder.pathTo(PostHandler.class).withMethod("test").withVar("documentId",
			"old").build();
	}

	@RequestMapping("/{documentId}/success.go")
	private class GetHandler
	{

		@RequestMapping(method = RequestMethod.GET)
		public String handleGetRequest()
		{
			throw new UnsupportedOperationException();
		}
	}

	private class PostHandler
	{

		@RequestMapping(value = "/{documentId}/error", method = RequestMethod.POST)
		public String handlePostRequest()
		{
			throw new UnsupportedOperationException();
		}

		public String test()
		{
			throw new UnsupportedOperationException();
		}
	}
}