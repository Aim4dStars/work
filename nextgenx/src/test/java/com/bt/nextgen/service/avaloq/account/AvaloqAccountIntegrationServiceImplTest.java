package com.bt.nextgen.service.avaloq.account;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bt.nextgen.service.avaloq.pension.PensionCommencementStatusIntegrationService;
import com.btfin.abs.trxservice.bp.v1_0.BpRsp;
import com.btfin.panorama.core.validation.Validator;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.bt.nextgen.clients.util.JaxbUtil;
import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.AvaloqTransactionService;
import com.bt.nextgen.service.ServiceErrors;

import com.bt.nextgen.service.avaloq.pension.PensionCommencementIntegrationService;
import com.bt.nextgen.service.avaloq.account.AvaloqAccountIntegrationCacheHelper;
import static com.bt.nextgen.service.avaloq.account.AvaloqAccountIntegrationCacheHelper.Invoker;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.btfin.panorama.service.integration.account.InitialInvestmentAsset;
import com.bt.nextgen.service.integration.account.InitialInvestmentRequest;
import com.bt.nextgen.service.integration.account.PensionAccountDetail;
import com.btfin.panorama.service.integration.account.SubAccount;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.bt.nextgen.service.integration.account.SubscriptionRequest;
import com.bt.nextgen.service.integration.account.UpdateSubscriptionResponse;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.account.WrapAccountDetailResponse;
import com.bt.nextgen.service.integration.account.direct.InitialInvestmentAssetImpl;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.request.AvaloqOperation;
import com.bt.nextgen.service.request.AvaloqRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class AvaloqAccountIntegrationServiceImplTest {
    private static final String ACCOUNT_NUMBER = "233476";


    @InjectMocks
    private AvaloqAccountIntegrationServiceImpl avaloqAccountIntegrationService;

    @Mock
    private AvaloqReportService avaloqService;

    @Mock
    private AvaloqCacheManagedAccountIntegrationService avaloqCacheAccountIntegrationService;

    @Mock
    private DistributionUpdateConverter distributionUpdateConverter;

    @Mock
    private AvaloqTransactionService avaloqTransactionService;

    @Mock
    private Validator validator;

    @Mock
    private AvaloqAccountIntegrationCacheHelper avaloqAccountIntegrationCacheHelper;

    @Mock
    private PensionCommencementStatusIntegrationService pensionCommencementIntegrationService;

    private AccountKey accountKey;

    private ServiceErrors serviceErrors;

    private SubscriptionRequest subscriptionRequest;

    private InitialInvestmentRequest initialInvestmentRequest;


    @Before
    public void setUp() {
        accountKey = AccountKey.valueOf("74674");
        Map<AccountKey, List<SubAccount>> subAccounts = new HashMap<>();
        SubAccount subAccount = Mockito.mock(SubAccount.class);
        when(subAccount.getSubAccountType()).thenReturn(ContainerType.DIRECT);
        when(subAccount.getSubAccountKey()).thenReturn(SubAccountKey.valueOf("1234"));
        subAccounts.put(accountKey, Collections.singletonList(subAccount));
        when(avaloqCacheAccountIntegrationService.loadSubAccounts(any(ServiceErrors.class))).thenReturn(subAccounts);
        serviceErrors = new ServiceErrorsImpl();
        subscriptionRequest = createSubscriptionRequest("184146", "DIRE.BTPI.ACTIVE");
        initialInvestmentRequest = createInitialInvestmentRequest("184146", "assetId", new BigDecimal(2000));
    }

    @Test
    public void updateDistributionOption() throws Exception {
        AssetKey assetKey = AssetKey.valueOf("54799");
        avaloqAccountIntegrationService
                .updateDistributionOption(accountKey, assetKey, DistributionMethod.REINVEST, serviceErrors);
        assertThat(serviceErrors.hasErrors(), is(false));

        // Testing for Negative Response
        accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("999888");
        avaloqAccountIntegrationService
                .updateDistributionOption(accountKey, assetKey, DistributionMethod.REINVEST, serviceErrors);
        assertThat(serviceErrors.hasErrors(), is(true));
    }

    @Test
    public void addSubscriptionSuccess() {
        BpRsp subscriptionResponse = JaxbUtil.unmarshall("/webservices/response/Account_add_subscription.xml", BpRsp.class);

        when(avaloqTransactionService.executeTransactionRequest(any(Object.class), any(AvaloqOperation.class),
                any(ServiceErrors.class))).thenReturn(subscriptionResponse);

        UpdateSubscriptionResponse response = avaloqAccountIntegrationService.addSubscription(subscriptionRequest,
                serviceErrors);
        assertThat("accountKey", response.getAccountKey(), notNullValue());
        assertThat("accountId", response.getAccountKey().getId(), equalTo("184146"));
        assertThat("subscription size", response.getSubscriptions().size(), equalTo(2));
    }

    @Test
    public void addSubscriptionFailure() {
        when(avaloqTransactionService.executeTransactionRequest(any(Object.class), any(AvaloqOperation.class),
                any(ServiceErrors.class))).thenReturn(null);

        UpdateSubscriptionResponse response = avaloqAccountIntegrationService.addSubscription(subscriptionRequest,
                serviceErrors);
        assertThat("response", response, nullValue());
    }

    @Test
    public void addInitialInvestmentSuccess() {
        BpRsp investmentResponse = JaxbUtil.unmarshall("/webservices/response/Account_add_init_invst_resp.xml", BpRsp.class);

        when(avaloqTransactionService.executeTransactionRequest(any(Object.class), any(AvaloqOperation.class),
                any(ServiceErrors.class))).thenReturn(investmentResponse);

        UpdateSubscriptionResponse response = avaloqAccountIntegrationService.addInitialInvestment(
                initialInvestmentRequest, serviceErrors);
        assertThat("accountKey", response.getAccountKey(), notNullValue());
        assertThat("accountId", response.getAccountKey().getId(), equalTo("233476"));
        assertThat("initialInvestment size", response.getInitialInvestmentAsset().size(), equalTo(1));
        assertThat("initialInvestment assetId", response.getInitialInvestmentAsset().get(0).getInvestmentAssetId(),
                equalTo("110523"));
        assertThat("initialInvestment amount", response.getInitialInvestmentAsset().get(0).getInitialInvestmentAmount(),
                equalTo(new BigDecimal(1000)));
    }

    @Test
    public void addInitialInvestmentFailure() {
        when(avaloqTransactionService.executeTransactionRequest(any(Object.class), any(AvaloqOperation.class),
                any(ServiceErrors.class))).thenReturn(null);

        UpdateSubscriptionResponse response = avaloqAccountIntegrationService.addInitialInvestment(
                initialInvestmentRequest, serviceErrors);
        assertThat("response", response, nullValue());
    }

    @Test
    public void deleteInitialInvestmentSuccess() {
        BpRsp subscriptionResponse = JaxbUtil.unmarshall("/webservices/response/Account_del_init_invst_resp.xml", BpRsp.class);

        when(avaloqTransactionService.executeTransactionRequest(any(Object.class), any(AvaloqOperation.class),
                any(ServiceErrors.class))).thenReturn(subscriptionResponse);

        UpdateSubscriptionResponse response = avaloqAccountIntegrationService.deleteInitialInvestment(
                initialInvestmentRequest, serviceErrors);
        assertThat("accountKey", response.getAccountKey(), notNullValue());
        assertThat("accountId", response.getAccountKey().getId(), equalTo(ACCOUNT_NUMBER));
        assertThat("initialInvestment size", response.getInitialInvestmentAsset().size(), equalTo(0));
    }

    @Test
    public void deleteInitialInvestmentFailure() {
        when(avaloqTransactionService.executeTransactionRequest(any(Object.class), any(AvaloqOperation.class),
                any(ServiceErrors.class))).thenReturn(null);

        UpdateSubscriptionResponse response = avaloqAccountIntegrationService.deleteInitialInvestment(
                initialInvestmentRequest, serviceErrors);
        assertThat("response", response, nullValue());
    }

    @Test
    public void loadWrapAccountDetail() {
        final AccountKey accountKey = makeAccountKey(ACCOUNT_NUMBER);
        final WrapAccountDetailImpl accountDetail = makeWrapAccountDetail(ACCOUNT_NUMBER);
        final WrapAccountDetail result;

        reset(avaloqAccountIntegrationCacheHelper, avaloqService, pensionCommencementIntegrationService);

        when(avaloqAccountIntegrationCacheHelper.loadWrapAccountDetail(eq(accountKey),
                any(Invoker.class)))
                .thenAnswer(new Answer<WrapAccountDetail>() {
                    @Override
                    public WrapAccountDetail answer(InvocationOnMock invocation) throws Throwable {
                        return ((Invoker<WrapAccountDetail>) invocation.getArguments()[1]).invoke();
                    }
                });
        when(avaloqService.executeReportRequestToDomain(any(AvaloqRequest.class), eq(WrapAccountDetailResponseImpl.class),
                any(ServiceErrors.class))).thenReturn(makeWrapAccountDetailResponse(accountDetail));

        result = avaloqAccountIntegrationService.loadWrapAccountDetail(accountKey, serviceErrors);
        verify(avaloqAccountIntegrationCacheHelper).loadWrapAccountDetail(eq(accountKey), any(Invoker.class));
        verify(avaloqService).executeReportRequestToDomain(any(AvaloqRequest.class),
                eq(WrapAccountDetailResponseImpl.class), any(ServiceErrors.class));

        assertThat("accountDetail", result, notNullValue());
        assertThat("accountDetails is not a pensionDetail", result instanceof PensionAccountDetail, equalTo(false));
    }

    @Test
    public void loadPensionWrapAccountDetail() {
        loadPensionWrapAccountDetail("pending commencement is not in progress", false);
        loadPensionWrapAccountDetail("pending commencement is in progress", true);
    }

    @Test
    public void test_loadWrapAccountByAccountDetails() {
        final WrapAccountDetailImpl accountDetail = makeWrapAccountDetail(ACCOUNT_NUMBER);
        when(avaloqService.executeReportRequestToDomain(any(AvaloqRequest.class), eq(WrapAccountDetailResponseImpl.class),
            any(ServiceErrors.class))).thenReturn(makeWrapAccountDetailResponse(accountDetail));

        WrapAccountDetailResponse response = avaloqAccountIntegrationService.loadWrapAccountDetailByAccountDetails("test", serviceErrors);
        verify(avaloqService).executeReportRequestToDomain(any(AvaloqRequest.class), eq(WrapAccountDetailResponseImpl.class), any(ServiceErrors.class));
        assertThat(response, notNullValue());
    }

    private void loadPensionWrapAccountDetail(String infoStr, boolean commencementPending) {
        final AccountKey accountKey = makeAccountKey(ACCOUNT_NUMBER);
        final PensionAccountDetailImpl accountDetail = makePensionAccountDetail(ACCOUNT_NUMBER, commencementPending);
        final WrapAccountDetail result;
        final PensionAccountDetail pensionResult;

        reset(avaloqAccountIntegrationCacheHelper, avaloqService, pensionCommencementIntegrationService);

        when(avaloqAccountIntegrationCacheHelper.loadWrapAccountDetail(eq(accountKey),
                any(Invoker.class)))
                .thenAnswer(new Answer<WrapAccountDetail>() {
                    @Override
                    public WrapAccountDetail answer(InvocationOnMock invocation) throws Throwable {
                        return ((Invoker<WrapAccountDetail>) invocation.getArguments()[1]).invoke();
                    }
                });
        when(avaloqService.executeReportRequestToDomain(any(AvaloqRequest.class), eq(WrapAccountDetailResponseImpl.class),
                any(ServiceErrors.class))).thenReturn(makeWrapAccountDetailResponse(accountDetail));
        when(pensionCommencementIntegrationService.isPensionCommencementPending(eq(ACCOUNT_NUMBER), eq(serviceErrors)))
                .thenReturn(commencementPending);

        result = avaloqAccountIntegrationService.loadWrapAccountDetail(accountKey, serviceErrors);
        verify(avaloqAccountIntegrationCacheHelper).loadWrapAccountDetail(eq(accountKey), any(Invoker.class));
        verify(avaloqService).executeReportRequestToDomain(any(AvaloqRequest.class),
                eq(WrapAccountDetailResponseImpl.class), any(ServiceErrors.class));
        verify(pensionCommencementIntegrationService).isPensionCommencementPending(eq(ACCOUNT_NUMBER), eq(serviceErrors));

        assertThat(infoStr + " - accountDetail", result, notNullValue());
        assertThat(infoStr + " - accountDetails is a pensionDetail", result instanceof PensionAccountDetail, equalTo(true));

        pensionResult = (PensionAccountDetail) accountDetail;
        assertThat(infoStr + " - commencementPending", pensionResult.isCommencementPending(), equalTo(commencementPending));
    }


    private AccountKey makeAccountKey(String accountId) {
        return AccountKey.valueOf(accountId);
    }

    private WrapAccountDetailResponseImpl makeWrapAccountDetailResponse(WrapAccountDetail accountDetail) {
        final WrapAccountDetailResponseImpl retval = new WrapAccountDetailResponseImpl();

        retval.setWrapAccountDetails(Arrays.asList(accountDetail));

        return retval;
    }

    private WrapAccountDetailImpl makeWrapAccountDetail(String accountNumber) {
        final WrapAccountDetailImpl accountDetail = new WrapAccountDetailImpl();

        initAccountDetail(accountDetail, accountNumber);

        return accountDetail;
    }

    private PensionAccountDetailImpl makePensionAccountDetail(String accountNumber, boolean commencementPending) {
        final PensionAccountDetailImpl accountDetail = new PensionAccountDetailImpl();

        initAccountDetail(accountDetail, accountNumber);

        return accountDetail;
    }

    private void initAccountDetail(WrapAccountDetailImpl accountDetail, String accountNumber) {
        accountDetail.setOwners(new ArrayList<Client>());
        accountDetail.setAccountNumber(accountNumber);
    }

    private SubscriptionRequest createSubscriptionRequest(String accountId, String productShortName) {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestImpl();
        subscriptionRequest.setAccountKey(AccountKey.valueOf(accountId));
        subscriptionRequest.setModificationIdentifier(BigDecimal.ONE);
        subscriptionRequest.setProductShortName(productShortName);
        return subscriptionRequest;
    }

    private InitialInvestmentRequest createInitialInvestmentRequest(String accountId, String assetId, BigDecimal amount) {
        List<InitialInvestmentAsset> initialInvestmentAssets = new ArrayList<>();
        InitialInvestmentAssetImpl initialInvestment = new InitialInvestmentAssetImpl();
        initialInvestment.setInitialInvestmentAssetId(assetId);
        initialInvestment.setInitialInvestmentAmount(amount);
        initialInvestmentAssets.add(initialInvestment);
        return new InitialInvestmentRequestImpl(AccountKey.valueOf(accountId), initialInvestmentAssets, BigDecimal.ONE);
    }
}
