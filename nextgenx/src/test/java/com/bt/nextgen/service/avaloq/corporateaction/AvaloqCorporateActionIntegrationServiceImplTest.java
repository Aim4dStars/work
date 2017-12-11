package com.bt.nextgen.service.avaloq.corporateaction;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.corporateaction.CorporateAction;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetailsResponse;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionTransactionDetails;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AvaloqCorporateActionIntegrationServiceImplTest extends BaseSecureIntegrationTest {
	@Autowired
	private CorporateActionIntegrationService corporateActionIntegrationService;

	@Before
	public void setup() {
	}

	@Test
	public void testVoluntaryCorporateAction_whenValidResponse_thenObjectCreatedAndNoServiceErrors() {
		ServiceErrors serviceErrors = new ServiceErrorsImpl();

		List<CorporateAction> caList =
				corporateActionIntegrationService
						.loadVoluntaryCorporateActions(new DateTime(), new DateTime(), Arrays.asList(""), serviceErrors);

		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNotNull(caList);
		Assert.assertTrue(caList.size() == 11);
	}

	@Test
	public void testVoluntaryCorporateAction_whenNullDates_thenObjectCreatedAndNoServiceErrors() {
		ServiceErrors serviceErrors = new ServiceErrorsImpl();

		List<CorporateAction> caList =
				corporateActionIntegrationService.loadVoluntaryCorporateActions(null, null, Arrays.asList(""), serviceErrors);

		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNotNull(caList);
		Assert.assertTrue(caList.size() == 11);
	}

	@Test
	public void testVoluntaryCorporateActionDetails_whenValidResponse_thenObjectCreatedAndNoServiceErrors() {
		ServiceErrors serviceErrors = new ServiceErrorsImpl();

		CorporateActionDetailsResponse response =
				corporateActionIntegrationService.loadCorporateActionDetails("0", serviceErrors);

		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNotNull(response.getCorporateActionDetailsList());
		Assert.assertTrue(response.getCorporateActionDetailsList().size() == 1);
		CorporateActionDetailsResponseImpl responseImpl = (CorporateActionDetailsResponseImpl)response;
		responseImpl.setCorporateActionDetailsList(null);
		Assert.assertNull(responseImpl.getCorporateActionDetailsList());
	}

	@Test
	public void testVoluntaryCorporateActionAccountsDetails_whenValidResponse_thenObjectCreatedAndNoServiceErrors() {
		ServiceErrors serviceErrors = new ServiceErrorsImpl();

		List<CorporateActionAccount> caaList =
				corporateActionIntegrationService.loadCorporateActionAccountsDetails("0", serviceErrors);

		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNotNull(caaList);
		Assert.assertTrue(caaList.size() == 1);
	}

	@Test
	public void testVoluntaryCorporateActionTransDetails_whenValidResponse_thenObjectCreatedAndNoServiceErrors() {
		ServiceErrors serviceErrors = new ServiceErrorsImpl();

		List<String> orderNumbers = new ArrayList<>();
		orderNumbers.add("784609");

		List<CorporateActionTransactionDetails> tsList =
				corporateActionIntegrationService.loadCorporateActionTransactionDetails(orderNumbers, serviceErrors);

		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNotNull(tsList);
		Assert.assertTrue(tsList.size() == 2);
	}

    @Test
    public void testVoluntaryCorporateActionTransDetailsForIm_whenValidResponse_thenObjectCreatedAndNoServiceErrors() {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();

        List<String> orderNumbers = new ArrayList<>();
        orderNumbers.add("784609");

        List<CorporateActionTransactionDetails> tsList = corporateActionIntegrationService
                .loadCorporateActionTransactionDetailsForIm("imId", orderNumbers, serviceErrors);

        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertNotNull(tsList);
        Assert.assertTrue(tsList.size() == 2);
    }
}
