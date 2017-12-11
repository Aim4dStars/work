package com.bt.nextgen.cms.web.tags;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;

import javax.servlet.jsp.JspWriter;

import org.junit.Before;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockJspWriter;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;

import com.bt.nextgen.cms.service.CmsService;

public abstract class AbstractTagTest {

    protected MockServletContext mockServletContext;
    protected MockPageContext mockPageContext;
    protected MockHttpServletRequest mockHttpServletRequest;
    protected MockHttpServletResponse mockHttpServletResponse;
    protected MockJspWriter mockJspWriter;
    protected WebApplicationContext wac;

    @Before
    public void initMockServletBeans() throws UnsupportedEncodingException {
        mockServletContext = new MockServletContext();
        mockHttpServletRequest = new MockHttpServletRequest(mockServletContext);
        mockHttpServletResponse = new MockHttpServletResponse();
        mockJspWriter = new MockJspWriter(mockHttpServletResponse.getWriter());
        mockPageContext = new MockPageContext(mockServletContext,
            mockHttpServletRequest, mockHttpServletResponse) {
            @Override
            public JspWriter getOut() {
                return mockJspWriter;
            }
        };
     
        wac =   mock(WebApplicationContext.class);
    }

    protected void expectExecuteEndTag(CmsService cmsService) {
        when(wac.getBean(CmsService.class)).thenReturn(cmsService);
        when(wac.getServletContext()).thenReturn(mockServletContext);
    }
}
