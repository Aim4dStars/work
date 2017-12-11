package avaloq;

import com.bt.nextgen.addressbook.web.model.GenericPayee;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.payments.domain.PayeeType;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: l053474
 * Date: 30/08/13
 * Time: 11:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class AvaloqExternalTest  {

    String bsb = "012012";
    String accountNo = "123312312";
    String billerCode = "524181";
    String crn = "0110382304001";
    String name = "dummy_name";
    String nickname = "dummy_nickname";
    GenericPayee payanyone = new GenericPayee();
    GenericPayee bpay = new GenericPayee();
    GenericPayee linked_primary = new GenericPayee();
    {
        payanyone.setName(name);
        payanyone.setNickname(nickname);
        payanyone.setCode(bsb);
        payanyone.setPayeeType(PayeeType.PAY_ANYONE);
        payanyone.setReference(accountNo);

        linked_primary.setName(name);
        linked_primary.setNickname(nickname);
        linked_primary.setCode(bsb);
        linked_primary.setPayeeType(PayeeType.LINKED);
        linked_primary.setReference(accountNo);
        linked_primary.setPrimary(true);

        bpay.setPayeeType(PayeeType.BPAY);
        bpay.setReference(crn);
        bpay.setCode(billerCode);
        bpay.setName(name);
    }

    @Autowired
    private WebServiceProvider provider;

    /*@Test
    public void testLoadPortfolio() throws Exception {
        portfolioService.loadPortfolio(portfolioId);
    }

    @Test
    public void testLoadPayees() {
    	PortfolioRequest portfolioRequest = new PortfolioRequestModel();
        portfolioRequest.setPortfolioId(portfolioId);
        avaloqAddressBookIntegrationService.loadPayees(portfolioRequest);
    }


    @Test
    public void testUpdatePayees() throws Exception {
        String newNickname = "new nickname";
        testDeletePayees();
        linked_primary.setCode("036154");
        linked_primary.setReference("98237431");
        linked_primary.setPrimary(true);
        PortfolioRequest portfolioRequest = new PortfolioRequestModel();
        portfolioRequest.setPortfolioId(portfolioId);
        List<Payee> payees=avaloqAddressBookIntegrationService.loadPayees(portfolioRequest);
        Payee payee;
        if(payees.size()==0){
        testAddLinkedAccount(linked_primary);
            payee= linked_primary;
        }
        else
        payee=payees.get(0);
        ((GenericPayee)payee).setNickname(newNickname);
        portfolioRequest.setPortfolioId(portfolioId);
        portfolioRequest.setGenericPayee(payee);
        portfolioRequest.setServiceErrors(new ServiceErrors());
        avaloqAddressBookIntegrationService.updatePayee(portfolioRequest);
        for (Payee genericPayee : avaloqAddressBookIntegrationService.loadPayees(portfolioRequest)) {
            if (genericPayee.getNickname().equals(newNickname))
                return;
        }
        fail();
    }

    @Test
    public void testDeletePayees() throws Exception {
    	PortfolioRequest portfolioRequest = new PortfolioRequestModel();
        portfolioRequest.setPortfolioId(portfolioId);
        portfolioRequest.setServiceErrors(new ServiceErrors());
        List<Payee> payees = avaloqAddressBookIntegrationService.loadPayees(portfolioRequest);
        for (Payee genericPayee : payees) {
        	avaloqAddressBookIntegrationService.deletePayee(portfolioRequest);
        }
    }


    @Test
    public void testAddLinkedAccount() throws Exception {
        testAddLinkedAccount(linked_primary);
    }

    public void testAddLinkedAccount(GenericPayee genericPayee) throws Exception {
    	PortfolioRequest portfolioRequest = new PortfolioRequestModel();
        portfolioRequest.setPortfolioId(portfolioId);
        portfolioRequest.setGenericPayee(genericPayee);
        ServiceErrors serviceErrors =new ServiceErrors();
        portfolioRequest.setServiceErrors(serviceErrors);
        testDeletePayees();
        avaloqAddressBookIntegrationService.addPayee(portfolioRequest);
        assertThat(serviceErrors.isEmpty(), is(true));
    }

    @Test
    public void testAddPayanyonePayee() throws Exception {
    	PortfolioRequest portfolioRequest = new PortfolioRequestModel();
        portfolioRequest.setPortfolioId(portfolioId);
        portfolioRequest.setGenericPayee(payanyone);
        ServiceErrors serviceErrors =new ServiceErrors();
        portfolioRequest.setServiceErrors(serviceErrors);
        testDeletePayees();
        avaloqAddressBookIntegrationService.addPayee(portfolioRequest);
        assertThat(serviceErrors.isEmpty(), is(true));
    }

    @Test
    public void testAddBPay() throws Exception {
    	PortfolioRequest portfolioRequest = new PortfolioRequestModel();
        portfolioRequest.setPortfolioId(portfolioId);
        portfolioRequest.setGenericPayee(bpay);
        ServiceErrors serviceErrors =new ServiceErrors();
        portfolioRequest.setServiceErrors(serviceErrors);
        testDeletePayees();
        avaloqAddressBookIntegrationService.addPayee(portfolioRequest);
        assertThat(serviceErrors.isEmpty(), is(true));
    }*/
/*
    @Test
    public void testUpdatePaymentLimit() throws Exception{
        ServiceErrors serviceErrors=new ServiceErrors();
        PortfolioInterface portfolioInterface=portfolioService.loadPortfolio(portfolioId);
//        avaloqService.updatePaymentLimit(portfolioId, new BigDecimal("25000"), PayeeType.PAY_ANYONE, serviceErrors);
//        avaloqService.updatePaymentLimit(portfolioId, new BigDecimal("10000"), PayeeType.LINKED, serviceErrors);
        PaymentRequest paymentRequest = new PaymentRequestModel();
        paymentRequest.setPortfolioId(portfolioId);
        paymentRequest.setLimit( new BigDecimal("25000"));
        paymentRequest.setPayeeType(PayeeType.BPAY);
        paymentRequest.setServiceErrors(serviceErrors);
        avaloqCashPortfolioIntegrationService.updatePaymentLimit(paymentRequest);
        portfolioInterface=portfolioService.loadPortfolio(portfolioId);
        DailyLimitModel dailyLimitModel=paymentLimitService.checkPaymentLimit(portfolioId,"11000",PayeeType.PAY_ANYONE);
        assertThat(serviceErrors.isEmpty(), is(true));
    }

    @Test
    public void testEPIData() throws Exception{
    	PortfolioRequest portfolioRequest = new PortfolioRequestModel();
        portfolioRequest.setPortfolioId(portfolioId);
        portfolioRequest.setFromDate("1 Jan 2013");
        portfolioRequest.setToDate("31 Mar 2013");
        ServiceErrors serviceErrors =new ServiceErrors();
        portfolioRequest.setServiceErrors(serviceErrors);
        portfolioRequest.setBglDatadownloadType("without tax");
        String epiData=avaloqCashPortfolioIntegrationService.loadEpiData(portfolioRequest);
        assertThat(epiData,containsString("EPIDataResponse"));
        assertThat(serviceErrors.isEmpty(),is(true));
    }*/
}
