package com.bt.nextgen.corporateaction.service;

import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionElectionResultDto;
import com.bt.nextgen.api.corporateaction.v1.service.ElectionResults;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class ElectionResultsTest {
	@Test
	public void test() {
		ElectionResults electionResults = new ElectionResults();

		electionResults.incrementPortfolioModelCount();
		assertEquals(electionResults.getPortfolioModelCount(), 1);

		electionResults.incrementSuccessCount();
		assertEquals(electionResults.getSuccessCount(), 1);

		electionResults.decrementSuccessCount();
		assertEquals(electionResults.getSuccessCount(), 0);

		electionResults.decrementSuccessCount();
		assertEquals(electionResults.getSuccessCount(), 0);

		electionResults.setResults(new ArrayList<ImCorporateActionElectionResultDto>());
		assertNotNull(electionResults.getResults());
	}
}
