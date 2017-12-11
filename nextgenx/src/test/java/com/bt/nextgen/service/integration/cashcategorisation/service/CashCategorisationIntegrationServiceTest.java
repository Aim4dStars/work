package com.bt.nextgen.service.integration.cashcategorisation.service;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.TransactionStatus;
import com.btfin.panorama.core.security.integration.account.PersonKey;
import com.bt.nextgen.service.integration.cashcategorisation.model.CashCategorisationAction;
import com.bt.nextgen.service.integration.cashcategorisation.model.CashCategorisationSubtype;
import com.bt.nextgen.service.integration.cashcategorisation.model.CategorisableCashTransaction;
import com.bt.nextgen.service.integration.cashcategorisation.model.CategorisableCashTransactionImpl;
import com.bt.nextgen.service.integration.cashcategorisation.model.Contribution;
import com.bt.nextgen.service.integration.cashcategorisation.model.MemberContributionImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CashCategorisationIntegrationServiceTest extends BaseSecureIntegrationTest
{
	@Autowired
	@Qualifier("CashCategorisationIntegrationServiceImpl")
	CashCategorisationIntegrationService cashCategorisationIntegrationService;

	@Test
	public void saveNewMemberContributions()
	{
		List <Contribution> contributionList = new ArrayList <>();

		Contribution split1 = new MemberContributionImpl();
		split1.setAmount(new BigDecimal(100));
		split1.setCashCategorisationSubtype(CashCategorisationSubtype.EMPLOYER);
		split1.setPersonKey(PersonKey.valueOf("23456"));
		contributionList.add(split1);

		CategorisableCashTransaction transaction = new CategorisableCashTransactionImpl();
		//transaction.setCategorisationType(CashCategorisationType.CONTRIBUTION);
		transaction.setAccountKey(new AccountKey("66667"));
		transaction.setDocId("111122");
		transaction.setContributionSplit(contributionList);

		TransactionStatus transactionStatus = cashCategorisationIntegrationService.saveOrUpdate(CashCategorisationAction.ADD,
			transaction);

		assertEquals(true, transactionStatus.isSuccessful());
	}

	@Test
	@SecureTestContext
	public void loadCashContributions()
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();

		List <Contribution> contributions = cashCategorisationIntegrationService.loadCashContributionsForTransaction("904863", serviceErrors);

		assertNotNull(contributions);

		assertEquals(4, contributions.size());
		// Other test cases covered in cash contribution tests
	}
}