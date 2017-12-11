package com.bt.nextgen.service.avaloq.modelportfolio;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.modelportfolio.common.IpsStatus;
import com.bt.nextgen.service.integration.modelportfolio.detail.ConstructionType;
import com.btfin.panorama.core.validation.Validator;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class ModelPortfolioSummaryImplIntegrationTest extends BaseSecureIntegrationTest
{
	@Autowired
	private Validator validator;

	@Before
	public void setup()
	{}

	@Test
	public void testValidation_whenModelIdIsNull_thenServiceErrors()
	{
		ModelPortfolioSummaryImpl model = new ModelPortfolioSummaryImpl();
		model.setModelName("Balanced portfolio");
		model.setModelCode("ACM123456");
		model.setLastUpdateDate(new DateTime());
		model.setLastUpdatedBy("Frank Wong");
		model.setAssetClass("Australian Shares");
		model.setInvestmentStyle("Income");
        model.setStatus(IpsStatus.PENDING);
		model.setFum(new BigDecimal("27493738.56"));
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(model, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("modelKey may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenModelNameIsNull_thenServiceErrors()
	{
		ModelPortfolioSummaryImpl model = new ModelPortfolioSummaryImpl();
        model.setModelKey(IpsKey.valueOf("1111"));
		model.setModelCode("ACM123456");
		model.setLastUpdateDate(new DateTime());
		model.setLastUpdatedBy("Frank Wong");
		model.setAssetClass("Australian Shares");
		model.setInvestmentStyle("Income");
        model.setStatus(IpsStatus.PENDING);
		model.setFum(new BigDecimal("27493738.56"));
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(model, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("modelName may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenModelCodeIsNull_thenServiceErrors()
	{
		ModelPortfolioSummaryImpl model = new ModelPortfolioSummaryImpl();
        model.setModelKey(IpsKey.valueOf("1111"));
		model.setModelName("Balanced portfolio");
		model.setLastUpdateDate(new DateTime());
		model.setLastUpdatedBy("Frank Wong");
		model.setAssetClass("Australian Shares");
		model.setInvestmentStyle("Income");
        model.setStatus(IpsStatus.PENDING);
		model.setFum(new BigDecimal("27493738.56"));
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(model, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("modelCode may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenAssetClassIsNull_thenServiceErrors()
	{
		ModelPortfolioSummaryImpl model = new ModelPortfolioSummaryImpl();
        model.setModelKey(IpsKey.valueOf("1111"));
		model.setModelName("Balanced portfolio");
		model.setModelCode("ACM123456");
		model.setLastUpdateDate(new DateTime());
		model.setLastUpdatedBy("Frank Wong");
		model.setInvestmentStyle("Income");
        model.setStatus(IpsStatus.PENDING);
		model.setFum(new BigDecimal("27493738.56"));
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(model, serviceErrors);
        Assert.assertTrue(!serviceErrors.hasErrors());
        // Assert.assertEquals("assetClass may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

    /*
     * @Test public void testValidation_whenInvestmentStyleIsNull_thenServiceErrors() { ModelPortfolioSummaryImpl model = new
     * ModelPortfolioSummaryImpl(); model.setModelKey(IpsKey.valueOf("1111")); model.setModelName("Balanced portfolio");
     * model.setModelCode("ACM123456"); model.setLastUpdateDate(new DateTime()); model.setLastUpdatedBy("Frank Wong");
     * model.setAssetClass("Australian Shares"); model.setStatus(IpsStatus.PENDING); model.setFum(new BigDecimal("27493738.56"));
     * ServiceErrors serviceErrors = new ServiceErrorsImpl(); validator.validate(model, serviceErrors);
     * Assert.assertTrue(serviceErrors.hasErrors()); Assert.assertEquals("investmentStyle may not be null",
     * serviceErrors.getErrorList().iterator().next().getReason()); }
     */
	@Test
	public void testValidation_whenStatusIsNull_thenServiceErrors()
	{
		ModelPortfolioSummaryImpl model = new ModelPortfolioSummaryImpl();
        model.setModelKey(IpsKey.valueOf("1111"));
		model.setModelName("Balanced portfolio");
		model.setModelCode("ACM123456");
		model.setLastUpdateDate(new DateTime());
		model.setLastUpdatedBy("Frank Wong");
		model.setAssetClass("Australian Shares");
		model.setInvestmentStyle("Income");
		model.setFum(new BigDecimal("27493738.56"));
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(model, serviceErrors);
        Assert.assertTrue(serviceErrors.hasErrors());
        Assert.assertEquals("status may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenFumIsNull_thenServiceErrors()
	{
		ModelPortfolioSummaryImpl model = new ModelPortfolioSummaryImpl();
        model.setModelKey(IpsKey.valueOf("1111"));
		model.setModelName("Balanced portfolio");
		model.setModelCode("ACM123456");
		model.setLastUpdateDate(new DateTime());
		model.setLastUpdatedBy("Frank Wong");
		model.setAssetClass("Australian Shares");
		model.setInvestmentStyle("Income");
        model.setStatus(IpsStatus.PENDING);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(model, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("fum may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenObjectIsNull_thenServiceErrors()
	{
		ModelPortfolioSummaryImpl model = null;
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(model, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("Failed to validate - object is null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenValid_thenNoServiceError()
	{
		ModelPortfolioSummaryImpl model = new ModelPortfolioSummaryImpl();
        model.setModelKey(IpsKey.valueOf("1111"));
		model.setModelName("Balanced portfolio");
		model.setModelCode("ACM123456");
		model.setLastUpdateDate(new DateTime());
		model.setLastUpdatedBy("Frank Wong");
		model.setAssetClass("Australian Shares");
		model.setInvestmentStyle("Income");
        model.setStatus(IpsStatus.PENDING);
		model.setFum(new BigDecimal("27493738.56"));
        model.setModelConstruction(ConstructionType.FIXED_AND_FLOATING);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(model, serviceErrors);
		Assert.assertFalse(serviceErrors.hasErrors());
	}
}
