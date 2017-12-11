package com.bt.nextgen.api.inspecietransfer.v3.util.vetting;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class TaxParcelFileSanitizerTest {

    // List containing all XSS js string to be sanitized.
    private List<String> testList;

    @Before
    public void setup() {        
        testList = Arrays.asList(
                "<img src=x onerror=\"&#0000106&#0000097&#0000118&#0000097&#0000115&#0000099&#0000114&#0000105&#0000112&#0000116&#0000058&#0000097&#0000108&#0000101&#0000114&#0000116&#0000040&#0000039&#0000088&#0000083&#0000083&#0000039&#0000041\">",
                        "<IMG SRC=\"livescript:[code]\">",
                        "'';!--\"<XSS>=&{()}",
                        "<SCRIPT SRC=http://xss.rocks/xss.js></SCRIPT>", 
                        "<IMG SRC=\"javascript:alert('XSS');\">",
                        "<IMG SRC=javascript:alert(&quot;XSS&quot;)>",
                        "<IMG SRC=javascript:alert(String.fromCharCode(88,83,83))>",
                        "<IMG SRC=# onmouseover=\"alert('xxs')\">",
                        "<IMG SRC=&#106;&#97;&#118;&#97;&#115;&#99;&#114;&#105;&#112;&#116;&#58;&#97;&#108;&#101;&#114;&#116;&#40;&#39;&#88;&#83;&#83;&#39;&#41;>",
                        "perl -e 'print \"<IMG SRC=java\0script:alert(\"XSS\")>\";' > out",
                        "<SCRIPT/XSS SRC=\"http://xss.rocks/xss.js\"></SCRIPT>", 
                        "<<SCRIPT>alert(\"XSS\");//<</SCRIPT>",
                        "<iframe src=http://xss.rocks/scriptlet.html <",
                        "</script><script>alert('XSS');</script>");
    }

    
    @Test
    public void testSanitizer() {
        PolicyFactory policy = Sanitizers.FORMATTING;
        for(String s : testList) {
            String safeHTML = policy.sanitize(s);
            System.out.println(s + " : [" + safeHTML + "]");
            Assert.assertTrue(safeHTML != null);
            Assert.assertTrue(!s.equals(safeHTML));

            // Sanitize a null string instance
            safeHTML = policy.sanitize(null);
            Assert.assertTrue(safeHTML != null);
            Assert.assertTrue(StringUtils.isEmpty(safeHTML));
        }
    }
    
    @Test
    public void testSanitizer_validText() {
        PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
        testFlatText(policy, "BHP");
        testFlatText(policy, "12.123123");
        testFlatText(policy, "BTL2340DF");
        testFlatText(policy, "Account name free flow text");
    }

    private void testFlatText(PolicyFactory policy, String txtValue) {
        Assert.assertEquals(txtValue, policy.sanitize(txtValue));
    }
}
