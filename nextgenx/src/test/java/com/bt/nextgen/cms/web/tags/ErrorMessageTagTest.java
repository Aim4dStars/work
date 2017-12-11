package com.bt.nextgen.cms.web.tags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import static org.mockito.Mockito.mock;
import org.junit.Before;
import org.junit.Test;

public class ErrorMessageTagTest extends AbstractTagTest {

    private ErrorMessageTag emt;

    private MockBodyContent mockBodyContent;

    @Before
    public void initTag() {
        mockBodyContent = new MockBodyContent(mockPageContext);
        emt = new ErrorMessageTag();
        emt.setPageContext(mockPageContext);
    }

    @Test
    public void nullParentTagThrowsJspException() throws JspException {
        emt.doInitBody();
        emt.setBodyContent(mockBodyContent);
        try {
            emt.doAfterBody();
            fail("Expecting JspException");
        } catch (JspException jspe) {
            assertEquals("Null parent tag", jspe.getMessage());
        }
    }

    @Test
    public void wrongParentClassThrowsJspException() throws JspException {
        Tag parent = mock(Tag.class);
        emt.setParent(parent);
        emt.doInitBody();
        emt.setBodyContent(mockBodyContent);
        try {
            emt.doAfterBody();
            fail("Expecting JspException");
        } catch (JspException jspe) {
            assertTrue(jspe.getMessage().startsWith("Invalid class"));
        }
    }

    @Test
    public void afterBodyWithCorrectParentTag() throws JspException {
        ContentFragmentTag parent = new ContentFragmentTag();
        emt.setParent(parent);
        emt.doInitBody();
        emt.setBodyContent(mockBodyContent);
        assertEquals(Tag.SKIP_BODY, emt.doAfterBody());
        assertNull(parent.getErrorMessage());
    }
}
