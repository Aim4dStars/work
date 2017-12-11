package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.schemas.v1.base.PaymentAuthorityEnum;
import com.bt.nextgen.api.draftaccount.schemas.v1.customer.Customer;
import com.bt.nextgen.api.draftaccount.schemas.v1.customer.Key;
import com.bt.nextgen.config.JsonObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Created by m040398 on 4/04/2016.
 */
public class ExtendedPersonDetailsFormTest {

    private Customer personDetails;
    private ObjectMapper objectMapper;

    @Before
    public void init() {
        this.personDetails = new Customer();
        objectMapper = new JsonObjectMapper();
    }

    @Test
    public void testIsExistingPersonWhenKeyisNull(){
        ExtendedPersonDetailsForm extendedPersonDetailsForm = new ExtendedPersonDetailsForm(1, personDetails,true, PaymentAuthorityEnum.NOPAYMENTS,false);
        assertFalse(extendedPersonDetailsForm.isExistingPerson());
        this.personDetails.setKey(null);
        ExtendedPersonDetailsForm extendedPersonDetailsForm1 = new ExtendedPersonDetailsForm(2, personDetails,true, PaymentAuthorityEnum.NOPAYMENTS,false);
        assertFalse(extendedPersonDetailsForm1.isExistingPerson());

    }

    @Test
    public void testIsExistingPersonWithNotNullKey() throws IOException {
        String json = "{" +
                "        \"clientId\": \"D3F47866AD5D9DC819D8FDBA95458EC0AEE02908D6A46FDF\"\n" +
                "      }}";

        personDetails.setKey(objectMapper.readValue(json, Key.class));
        ExtendedPersonDetailsForm extendedPersonDetailsForm = new ExtendedPersonDetailsForm(1, personDetails,true,PaymentAuthorityEnum.NOPAYMENTS,false);
        assertTrue(extendedPersonDetailsForm.isExistingPerson());
    }
}
