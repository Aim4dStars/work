package com.bt.nextgen.api.draftaccount.model.form;

import com.bt.nextgen.api.draftaccount.schemas.v1.base.PaymentAuthorityEnum;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by l079353 on 7/01/2016.
 */
public class ExtendedPersonDetailsFormTest {
    @Test
    public void testIsExistingPersonWhenKeyisNull(){
        Map formData = new HashMap();
        formData.put("key", null);
        ExtendedPersonDetailsForm extendedPersonDetailsForm = new ExtendedPersonDetailsForm(formData,true, PaymentAuthorityEnum.NOPAYMENTS,false);
        assertFalse(extendedPersonDetailsForm.isExistingPerson());
        formData.remove("key");
        ExtendedPersonDetailsForm extendedPersonDetailsForm1 = new ExtendedPersonDetailsForm(formData,true, PaymentAuthorityEnum.NOPAYMENTS,false);
        assertFalse(extendedPersonDetailsForm1.isExistingPerson());

    }

    @Test
    public void testIsExistingPersonWithNotNullKey() throws IOException {
        String json = "{ \"key\": {\n" +
                "        \"clientId\": \"D3F47866AD5D9DC819D8FDBA95458EC0AEE02908D6A46FDF\"\n" +
                "      }}";
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> addressDetailsMap = mapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        ExtendedPersonDetailsForm extendedPersonDetailsForm = new ExtendedPersonDetailsForm(addressDetailsMap,true, PaymentAuthorityEnum.NOPAYMENTS,false);
        assertTrue(extendedPersonDetailsForm.isExistingPerson());
    }

}
