package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.model.AbstractJsonObjectMapperTest;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.DirectClientApplicationFormData;
import com.bt.nextgen.api.draftaccount.schemas.v1.OnboardingApplicationFormData;
import static com.bt.nextgen.api.draftaccount.model.form.v1.ClientApplicationFormFactoryV1.getNewDirectClientApplicationForm;
import static org.junit.Assert.assertNull;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.rules.ExpectedException;


import java.io.IOException;

/**
 * Created by L069552 on 27/02/17.
 */
public class ClientDirectApplicationFormTest extends AbstractJsonObjectMapperTest<DirectClientApplicationFormData> {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private IClientApplicationForm form;

    public ClientDirectApplicationFormTest() {
        super(DirectClientApplicationFormData.class);
    }

    @Test
    public void testDirectAccountType() throws IOException {
        initDirectForm("directIndividual_new_nonStandardAddr", IClientApplicationForm.AccountType.INDIVIDUAL);
        assertNull(form.getAccountSettings().getPowerOfAttorney());
    }

    private void initDirectForm(String resourceName, IClientApplicationForm.AccountType accountType) throws IOException {
        this.form = getNewDirectClientApplicationForm(readJsonResource(resourceName));
        exception.expect(UnsupportedOperationException.class);
        exception.expectMessage("Power of Attorney is not supported for Direct");
        this.form.getAccountSettings().getPowerOfAttorney();
    }
}
