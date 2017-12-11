package com.bt.nextgen.api.draftaccount.model.form.v1;

import java.io.IOException;

import com.bt.nextgen.api.draftaccount.schemas.v1.base.IdentificationTypeEnum;
import org.junit.Before;
import org.junit.Test;

import com.bt.nextgen.api.draftaccount.model.AbstractJsonObjectMapperTest;
import com.bt.nextgen.api.draftaccount.model.form.IIdentityVerificationDocuments;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.IdentityDocument;
import com.bt.nextgen.api.draftaccount.util.XMLGregorianCalendarUtil;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;

/**
 * Tests for the {@code IdentityVerificationForm} class.
 */
public class IdentityVerificationFormTest extends AbstractJsonObjectMapperTest<IdentityDocument> {
    private IdentityVerificationForm identityVerificationForm;

    public IdentityVerificationFormTest() {
        super(IdentityDocument.class);
    }

    @Before
    public void initFormWithIdentificationVerificationForm() throws IOException {
        initIdentityVerificationForms("company-new-1");
    }

    @Test
    public void test_validNonPhotoIdentification() {
        final IIdentityVerificationDocuments documents = identityVerificationForm.getNonPhotoDocuments();
        assertThat(documents.getIdentityDocuments().get(0).getIdentificationType(), is(IdentificationTypeEnum.BIRTHCERTIFICATE));
        assertThat(documents.getIdentityDocuments().get(0).getDocumentIssuer(), is("NSW"));
        assertThat(documents.getIdentityDocuments().get(0).getIssueDate(), is(XMLGregorianCalendarUtil.date("15/07/1978", "dd/MM/yyyy")));
        assertThat(documents.getIdentityDocuments().get(0).getDocumentNumber(), is("112344324"));
        assertThat(documents.getIdentityDocuments().get(0).getVerificationSource(), is("original"));
        assertThat(documents.getIdentityDocuments().get(0).getEnglishTranslation(), is("Not Applicable"));
    }

    @Test
    public void test_noPhotoIdentification() {
        assertNull(identityVerificationForm.getPhotoDocuments());
    }

    @Test
    public void test_noInternationalIdentification() {
        assertNull(identityVerificationForm.getInternationalDocuments());
    }

    private void initIdentityVerificationForms(String resourceName) throws IOException {
        final IdentityDocument identityDocument = readJsonResource(resourceName, "identitydocument");
        identityVerificationForm = new IdentityVerificationForm(identityDocument);
    }
}
