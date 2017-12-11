package com.bt.nextgen.portfolio.api.service;


//@RunWith(MockitoJUnitRunner.class)
public class TermDepositDtoServiceTest
{
	/**
	 * to be rewritten without using valuation
		@InjectMocks
		private TermDepositDtoServiceImpl termDepositService;
				@InjectMocks
		private ValuationDtoServiceImpl valuationService;
		
		@Mock
		BankingReferenceDataDao bankingReferenceDataDao;
		
		private PortfolioInterface portfolio;
		
		@Before
		public void setup() throws Exception
		{
			portfolio = new PortfolioModel();

			CashAccountModel cashAccount = new CashAccountModel();
			cashAccount.setIdpsAccountName("idpsAccountName");
			cashAccount.setBsb("bsb");
			cashAccount.setCashAccountNumber("cashAccountNumber");
			cashAccount.setTotalBalance("$123,456.00");
			cashAccount.setAvailableBalance("$123,000.00");
			cashAccount.setInterestRate("12.99%");
			cashAccount.setInterestEarned("$1,111.00");
			portfolio.setCashAccount(cashAccount);

			TermDepositAccountModel termDeposit = new TermDepositAccountModel();
			termDeposit.setBrandName("brandName");
			termDeposit.setBrandClass("brandClass");
			termDeposit.setInvestmentAmount("$111,111.00");
			termDeposit.setInterestRate("12.12");
			termDeposit.setInterestPaid("$4,123.00");
			termDeposit.setMaturityDate("10 Nov 2013");
			termDeposit.setStatus("status");
			termDeposit.setMaturityInstruction("maturityInstruction");
			termDeposit.setBalanceOnMaturity("$115,634.00");
			termDeposit.setInvestmentId("1231");
			termDeposit.setAccountNumber("1232");
			termDeposit.setInterestAccrued("$30.00");
			termDeposit.setPercentageofTermCompleted("20.10%");
			termDeposit.setWithdrawalTotalAmount("$12,320.00");
			termDeposit.setWithdrawnInterestRate("1.45%");
			termDeposit.setMaturityInstruction(Constants.RENEW_INVEST_AMOUNT);
			portfolio.setTermDepositAccounts(Collections.singletonList(termDeposit));
			

			portfolio.setBalance(BigDecimal.valueOf(1221690L));
			
			Map <String, List <StaticCodeInterface>> staticCodes = Mockito.mock(HashMap.class);
			Mockito.when(bankingReferenceDataDao.loadAllStaticCodes()).thenReturn(staticCodes);
			Mockito.when(staticCodes.get(anyString())).thenReturn(new ArrayList <StaticCodeInterface>());		
		}
		
		
		@Test
		public void testGetTD_WithInvalidAccountNumber()
		{
			InvestmentKey key = new InvestmentKey("123", "123", "14438");
			ValuationDto valuation = valuationService.getValuationFromPortfolio(
					getValuationKey(key), portfolio);
			TermDepositValuationDto tdDto = termDepositService.getTermDepositDto(valuation, key);
						
			assertNull(tdDto);
		}
		
		
		@Test
		public void testGetTD_WithValidAccountNumber()
		{
			InvestmentKey key = new InvestmentKey("123", "123", "1231");
			ValuationDto valuation = valuationService.getValuationFromPortfolio(getValuationKey(key), portfolio);
			TermDepositValuationDto tdDto = termDepositService.getTermDepositDto(valuation, key);
										
			TermDepositAccountModel termDeposit = portfolio.getTermDepositAccounts().get(0);
			
			assertEquals(tdDto.getAccountId(), termDeposit.getAccountNumber());
			assertEquals(Format.asCurrency(tdDto.getInterestAccrued()), termDeposit.getInterestAccrued());
			assertEquals(Format.asCurrency(tdDto.getWithdrawnTotalAmount()), termDeposit.getWithdrawalTotalAmount());
			assertEquals(tdDto.getWithdrawnInterestRate(), Format.percentToNumber(termDeposit.getWithdrawnInterestRate()));
			
			assertNotNull(valuation);
		}

		
		private DatedPortfolioKey getValuationKey(InvestmentKey invKey)
		{		
			return new DatedPortfolioKey(EncodedString.fromPlainText(invKey.getClientId()).toString(), 
									EncodedString.fromPlainText(invKey.getPortfolioId()).toString(), 
									new DateTime());
		}
		*/
}
