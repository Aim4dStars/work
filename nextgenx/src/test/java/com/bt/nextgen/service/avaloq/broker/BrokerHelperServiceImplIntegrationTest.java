package com.bt.nextgen.service.avaloq.broker;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.group.customer.BankingCustomerIdentifier;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.bt.nextgen.service.integration.userprofile.JobProfileIdentifier;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.btfin.panorama.service.integration.broker.BrokerType.ADVISER;
import static junit.framework.Assert.assertFalse;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;

public class BrokerHelperServiceImplIntegrationTest extends BaseSecureIntegrationTest
{
    private static final Logger log = LoggerFactory.getLogger(BrokerHelperServiceImplIntegrationTest.class);

    @Autowired
    private BrokerHelperService brokerHelperService;


    private class Timer {
        private String infoStr;
        private long startTime;

        public Timer(String infoStr) {
            this.infoStr = infoStr;
        }

        public void start() {
            startTime = System.nanoTime();
        }

        public void end() {
            long endTime = System.nanoTime();
            long elapsedTime = endTime - startTime;

            log.info("{}: Elapsed time = {} ms ({} us)", infoStr, elapsedTime / 1000000, elapsedTime / 1000);
        }
    }

    @Test
    @SecureTestContext(authorities =
            {
                    "ROLE_ADVISER"
            }, username = "serviceops", customerId = "M034010", profileId ="561" , jobRole="SERVICE_AND_OPERATION", jobId = "89436")
    public void testgetAdviserForInvestor() throws Exception
    {
        final String testName = "getAdviserForInvestor";
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Timer timer;

        /************* userBrokerHolderService ************/
        timer = new Timer("BrokerHelperService - " + testName );
        timer.start();
        BankingCustomerIdentifier gcmId = new BankingCustomerIdentifier() {
            @Override
            public String getBankReferenceId() {
                return "201649533";
            }

            @Override
            public UserKey getBankReferenceKey() {
                return UserKey.valueOf("201649533");
            }

            @Override
            public CISKey getCISKey() {
                return null;
            }
        };

        Broker broker = brokerHelperService.getAdviserForInvestor(gcmId,serviceErrors);
        assertThat(broker, notNullValue());
        timer.end();


    }

    @Test
    @SecureTestContext(authorities =
            {
                    "ROLE_ADVISER"
            }, username = "adviser", customerId = "M034010", profileId ="561" , jobRole="SERVICE_AND_OPERATION", jobId = "89436")
    public void testgetDealerGroupForIntermediary() throws Exception
    {
        final String testName = "getAdviserForInvestor";
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Timer timer;

        /************* userBrokerHolderService ************/
        timer = new Timer("BrokerHelperService - " + testName );
        timer.start();
        BankingCustomerIdentifier gcmId = new BankingCustomerIdentifier() {
            @Override
            public String getBankReferenceId() {
                return "201601509";
            }

            @Override
            public UserKey getBankReferenceKey() {
                return UserKey.valueOf("201601509");
            }

            @Override
            public CISKey getCISKey() {
                return null;
            }
        };

        List<Broker> broker = brokerHelperService.getDealerGroupForIntermediary(gcmId, serviceErrors);
        assertThat(broker, notNullValue());
        timer.end();


    }


    @Test
    @SecureTestContext( profileId ="677",jobRole = "ADVISER", customerId = "201601408",jobId = "83489")
    public void testGetDealerGroupInvestor()
    {
        WrapAccount wrapAccount= new WrapAccountImpl(){
            public BrokerKey getAdviserPositionId()
            {
                return BrokerKey.valueOf("1234");
            }
        };
        ServiceErrors serviceErrors= new ServiceErrorsImpl();
        wrapAccount.setAccountKey(AccountKey.valueOf("11234234"));
        Broker dealerGroupTest= brokerHelperService.getDealerGroupForInvestor(wrapAccount,serviceErrors);
        assertThat(serviceErrors, notNullValue());
        assertThat(serviceErrors.getErrorList().iterator().next().getReason(), is("Adviser is Null.It is a fatal Error"));

    }

    @Test
    @SecureTestContext(username="nullcheck", profileId ="677",jobRole = "ADVISER", customerId = "201601408",jobId = "83489")
    public void testGetDealerGroupAdviser()
    {

        JobProfileIdentifier jobProfileIdentifier= new BrokerUserImpl(UserKey.valueOf("201601509"), JobKey.valueOf("85062"));
        ServiceErrors serviceErrors= new ServiceErrorsImpl();
        Broker dealerGroupTest= brokerHelperService.getDealerGroupForIntermediary(jobProfileIdentifier, serviceErrors);
        assertThat(serviceErrors, notNullValue());
        assertThat(serviceErrors.getErrorList().iterator().next().getReason(), is("Dealer Key Found Null. It is a fatal error."));
    }

    @Test
    @SecureTestContext(username="btdirect", profileId ="677",jobRole = "ADVISER", customerId = "201601408",jobId = "83489")
    public void testIsDirectInvestor()
    {

        WrapAccount wrapAccount= new WrapAccountImpl(){
            public BrokerKey getAdviserPositionId()
            {
                return BrokerKey.valueOf("1234");
            }
        };
        ServiceErrors serviceErrors= new ServiceErrorsImpl();
        wrapAccount.setAccountKey(AccountKey.valueOf("11234234"));
        boolean directInvestor = brokerHelperService.isDirectInvestor(wrapAccount,serviceErrors);
        assertFalse(directInvestor);
    }


    @Test
    @SecureTestContext(authorities = {"ROLE_ADVISER"}, username = "serviceops", customerId = "M034010", profileId ="561"
            , jobRole="SERVICE_AND_OPERATION", jobId = "89436")
    public void testAdviserListForInvestors() {

        BankingCustomerIdentifier customerIdentifier = new BankingCustomerIdentifier() {
            @Override
            public String getBankReferenceId() {
                return "201602190";
            }
            @Override
            public UserKey getBankReferenceKey() {
                return UserKey.valueOf("201602190");
            }
            @Override
            public CISKey getCISKey() {
                return null;
            }
        };
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<Broker> res = brokerHelperService.getAdviserListForInvestor(customerIdentifier, serviceErrors);
        assertNotNull(res);
        assertThat(res.size(),is(4));
    }

    @Test
    @SecureTestContext
    public void testBrokerLoading() {

        List<BrokerKey> list = new ArrayList<>();
        list.add(BrokerKey.valueOf("80091"));
        list.add(BrokerKey.valueOf("79964"));
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Map<BrokerKey, Broker> response = brokerHelperService.loadBrokersByIdList(list, serviceErrors);

        assertThat(serviceErrors.hasErrors(), equalTo(false));
        assertNotNull(response);
        assertThat(response.keySet().size(), is(2));

        Broker firstBroker = response.get(BrokerKey.valueOf("80091"));
        assertThat(firstBroker, notNullValue());
        assertThat(firstBroker.getBrokerType(), equalTo(ADVISER));
        assertThat(firstBroker.getBankReferenceId(),equalTo("101117206"));
        assertThat(firstBroker.getBrokerStartDate().toLocalDate().toString(),equalTo("2014-06-04"));
        assertThat(firstBroker.getExternalBrokerKey().getId(),equalTo("AVSR_POS.2404"));
        assertThat(firstBroker.getParentEBIKey().getId(),equalTo("GRANTTHORN"));

        Broker secondBroker = response.get(BrokerKey.valueOf("79964"));
        assertThat(secondBroker, notNullValue());
        assertThat(secondBroker.getBrokerType(), equalTo(ADVISER));
    }

}
