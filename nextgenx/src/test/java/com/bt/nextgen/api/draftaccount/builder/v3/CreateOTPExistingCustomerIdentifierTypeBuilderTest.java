package com.bt.nextgen.api.draftaccount.builder.v3;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import ns.btfin_com.party.v3_0.CustomerNoAllIssuerType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.CreateOTPExistingCustomerIdentifierType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.btfin.panorama.test.schema.AbstractSchemaValidatorTest;

@RunWith(MockitoJUnitRunner.class)
public class CreateOTPExistingCustomerIdentifierTypeBuilderTest extends AbstractSchemaValidatorTest {

    private CreateOTPExistingCustomerIdentifierTypeBuilder createOTPExistingCustomerIdentifierTypeBuilder;

    private String gcmId;

    public CreateOTPExistingCustomerIdentifierTypeBuilderTest() {
        super("schema/btesb/BTFin/Product/Panorama/CredentialService/V1/CredentialRequestV1_0.xsd");
    }

    @Before
    public void initBuilderAndMocks() throws Exception {
        gcmId = "1234567";
        this.createOTPExistingCustomerIdentifierTypeBuilder = new CreateOTPExistingCustomerIdentifierTypeBuilder();
    }

    @Test
    public void shouldContainTheGCMIdAndIssuerTypeST_GEORGE() throws Exception {
        this.createOTPExistingCustomerIdentifierTypeBuilder.setId(gcmId);
    	this.createOTPExistingCustomerIdentifierTypeBuilder.setType(CustomerNoAllIssuerType.ST_GEORGE);
    	
    	CreateOTPExistingCustomerIdentifierType request = createOTPExistingCustomerIdentifierTypeBuilder.build();

        assertThat(request.getCustomerNumberIdentifier().getCustomerNumber(), is(gcmId));
        assertThat(request.getCustomerNumberIdentifier().getCustomerNumberIssuer(), is(CustomerNoAllIssuerType.ST_GEORGE));
    }

    @Test
    public void shouldContainGCMIdAndIssuerTypeBT_PANORAMA() throws Exception {
        this.createOTPExistingCustomerIdentifierTypeBuilder.setId(gcmId);
    	this.createOTPExistingCustomerIdentifierTypeBuilder.setType(CustomerNoAllIssuerType.BT_PANORAMA);
        CreateOTPExistingCustomerIdentifierType request = (CreateOTPExistingCustomerIdentifierType) createOTPExistingCustomerIdentifierTypeBuilder.build();

        assertThat(request.getCustomerNumberIdentifier().getCustomerNumber(), is(gcmId));
        assertThat(request.getCustomerNumberIdentifier().getCustomerNumberIssuer(), is(CustomerNoAllIssuerType.BT_PANORAMA));
    }

    @Test
    public void shouldContainGCMIdAndIssuerTypeWESTPAC() throws Exception {
        this.createOTPExistingCustomerIdentifierTypeBuilder.setId(gcmId);
    	this.createOTPExistingCustomerIdentifierTypeBuilder.setType(CustomerNoAllIssuerType.WESTPAC);
    	CreateOTPExistingCustomerIdentifierType request = (CreateOTPExistingCustomerIdentifierType) createOTPExistingCustomerIdentifierTypeBuilder.build();

    	 assertThat(request.getCustomerNumberIdentifier().getCustomerNumber(), is(gcmId));
         assertThat(request.getCustomerNumberIdentifier().getCustomerNumberIssuer(), is(CustomerNoAllIssuerType.WESTPAC));
    }

    @Test
    public void shouldContainGCMIdAndIssuerTypeWESTPAC_LEGACY() throws Exception {
        this.createOTPExistingCustomerIdentifierTypeBuilder.setId(gcmId);
    	this.createOTPExistingCustomerIdentifierTypeBuilder.setType(CustomerNoAllIssuerType.WESTPAC_LEGACY);
    	CreateOTPExistingCustomerIdentifierType request = (CreateOTPExistingCustomerIdentifierType) createOTPExistingCustomerIdentifierTypeBuilder.build();

    	 assertThat(request.getCustomerNumberIdentifier().getCustomerNumber(), is(gcmId));
         assertThat(request.getCustomerNumberIdentifier().getCustomerNumberIssuer(), is(CustomerNoAllIssuerType.WESTPAC_LEGACY));
    }
    
    @Test
    public void notContainingGCMId() throws Exception {
    	this.createOTPExistingCustomerIdentifierTypeBuilder.setType(CustomerNoAllIssuerType.WESTPAC_LEGACY);
    	CreateOTPExistingCustomerIdentifierType request = (CreateOTPExistingCustomerIdentifierType) createOTPExistingCustomerIdentifierTypeBuilder.build();

    	 assertNull(request.getCustomerNumberIdentifier().getCustomerNumber());
         assertThat(request.getCustomerNumberIdentifier().getCustomerNumberIssuer(), is(CustomerNoAllIssuerType.WESTPAC_LEGACY));
    }

}
