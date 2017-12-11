package com.bt.nextgen.api.draftaccount.model.form;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by l079353 on 8/01/2016.
 */
public class PersonDetailsFormTest {

    @Test
    public void testIsExistingPersonWhenKeyisNull(){
        Map formData = new HashMap();
        formData.put("key", null);
        PersonDetailsForm personDetailsForm = new PersonDetailsForm(formData);
        assertFalse(personDetailsForm.isExistingPerson());
        formData.remove("key");
        PersonDetailsForm personDetailsForm1 = new PersonDetailsForm(formData);
        assertFalse(personDetailsForm1.isExistingPerson());

    }

    @Test
    public void testIsExistingPersonWithNotNullKey() throws IOException{
        String json = "{ \"key\": {\n" +
                "        \"clientId\": \"D3F47866AD5D9DC819D8FDBA95458EC0AEE02908D6A46FDF\"\n" +
                "      }}";
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> addressDetailsMap = mapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        PersonDetailsForm personForm = new PersonDetailsForm(addressDetailsMap);
        assertTrue(personForm.isExistingPerson());
    }

    @Test
    public void testIsGCMRetrievedPerson(){
        Map formData = new HashMap();
        formData.put("registered", true);
        PersonDetailsForm personDetailsForm = new PersonDetailsForm(formData);
        assertFalse(personDetailsForm.isGcmRetrievedPerson());
        formData.remove("registered");
        PersonDetailsForm personDetailsForm1 = new PersonDetailsForm(formData);
        assertFalse(personDetailsForm1.isGcmRetrievedPerson());
        formData.put("registered", false);
        PersonDetailsForm personDetailsForm2 = new PersonDetailsForm(formData);
        assertTrue(personDetailsForm2.isGcmRetrievedPerson());
    }
}
