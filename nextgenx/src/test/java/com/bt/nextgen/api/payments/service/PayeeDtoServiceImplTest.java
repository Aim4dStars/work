package com.bt.nextgen.api.payments.service;

import com.bt.nextgen.addressbook.web.model.GenericPayee;
import com.bt.nextgen.api.account.v1.model.AccountKey;
import com.bt.nextgen.api.account.v1.model.PayeeDto;
import com.bt.nextgen.api.account.v1.model.PaymentDto;
import com.bt.nextgen.api.account.v1.service.PayeeDtoServiceImpl;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.security.profile.InvestorProfileService;
import com.bt.nextgen.payments.domain.PayeeType;
import com.bt.nextgen.payments.repository.BpayBiller;
import com.bt.nextgen.payments.repository.BpayBillerCodeRepository;
import com.bt.nextgen.payments.repository.Bsb;
import com.bt.nextgen.payments.repository.BsbCodeRepository;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.account.BankAccountImpl;
import com.bt.nextgen.service.avaloq.account.BillerImpl;
import com.bt.nextgen.service.avaloq.account.UpdateAccountDetailResponseImpl;
import com.bt.nextgen.service.avaloq.payeedetails.PayAnyOneImpl;
import com.bt.nextgen.service.avaloq.payeedetails.PayeeDetailsImpl;
import com.bt.nextgen.service.group.customer.CustomerDataManagementIntegrationService;
import com.bt.nextgen.service.group.customer.groupesb.CustomerData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerDataImpl;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequest;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.account.*;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetails;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetailsIntegrationService;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.util.SamlUtil;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.profile.Profile;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.account.BankAccount;
import com.btfin.panorama.service.integration.account.Biller;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.*;

/**
 * @deprecated Use V2
 */
@Deprecated
@RunWith(MockitoJUnitRunner.class)
public class PayeeDtoServiceImplTest {
    @InjectMocks
    private PayeeDtoServiceImpl payeeDtoService;

    @Mock
    BpayBillerCodeRepository bpayBillerCodeRepository;

    @Mock
    PayeeDetailsIntegrationService payeeDetailsIntegrationService;

    @Mock
    private AccountIntegrationService avaloqAccountIntegrationServiceImpl;

    @Mock
    private CustomerDataManagementIntegrationService customerDataManagementIntegrationService;

    @Mock
    private InvestorProfileService profileService;

    @Mock
    Profile userProfile;

    @Mock
    CmsService cmsService;

    @Mock
    private BsbCodeRepository bsbCodeRepository;

    PaymentDto paymentDtoKeyedObj;

    List<Biller> billerList = null;

    PayeeDetails payeeDetails;

    UpdateAccountDetailResponse updateAccountDetailResponse = null;

    PayeeDto payeeDto;

    BpayBiller bpayBiller = null;

    private SamlToken token = null;

    @Before
    public void setup() throws Exception {
        payeeDetails = Mockito.mock(PayeeDetails.class);
        Mockito.when(payeeDetails.getModifierSeqNumber()).thenReturn(new BigDecimal(123));
        payeeDto = new PayeeDto();
        updateAccountDetailResponse = new UpdateAccountDetailResponseImpl();

        paymentDtoKeyedObj = new PaymentDto(new AccountKey("123456"));

        payeeDto.setAccountName("Denis Beecham");
        payeeDto.setAccountId("123456");
        payeeDto.setNickname("Nickname1");
        payeeDto.setCode("234567");
        payeeDto.setCrn("345678");
        payeeDto.setSaveToList("Yes");
        payeeDto.setLimit("1000");

        paymentDtoKeyedObj.setPrimary(true);
        paymentDtoKeyedObj.setToPayteeDto(payeeDto);
        paymentDtoKeyedObj.setKey(new AccountKey("123456"));

        bpayBiller = new BpayBiller();
        bpayBiller.setBillerName("Denis Beecham");

        BillerImpl biller = new BillerImpl();
        biller.setBillerCode("012006");
        biller.setCRN("123456789");
        biller.setName("Denis Beecham");
        biller.setNickName("Nickname1");
        billerList = new ArrayList<Biller>();
        billerList.add(biller);

        List<PayAnyOne> payanyoneList = new ArrayList<PayAnyOne>();
        PayAnyOneImpl payanyone = new PayAnyOneImpl();
        payanyone.setName("Denis Beecham");
        payanyone.setBsb("012006");
        payanyone.setAccountNumber("120061455");
        payanyone.setNickName("Nickname1");
        payanyoneList.add(payanyone);

        PayeeDetailsImpl payeeDetailsImpl = new PayeeDetailsImpl();
        payeeDetailsImpl.setBpayBillerPayeeList(billerList);
        payeeDetailsImpl.setPayanyonePayeeList(payanyoneList);
        Mockito.when(payeeDetailsIntegrationService.loadPayeeDetails(Mockito.any(WrapAccountIdentifier.class),
                Mockito.any(ServiceErrorsImpl.class))).thenReturn(payeeDetailsImpl);

        final List<DomainApiErrorDto> domainErrors = Arrays.asList(new DomainApiErrorDto());

        Bsb bsb = new Bsb();
        bsb.setBsbCode("012006");
        Mockito.when(bsbCodeRepository.load(Mockito.anyString())).thenReturn(bsb);

        /*
         * portfolioInterface = mock(PortfolioInterface.class); cashAccountModel = new CashAccountModel();
         * cashAccountModel.setIdpsAccountName("Test Account"); cashAccountModel.setBsb("012020");
         * cashAccountModel.setCashAccountNumber("123654789"); cashAccountModel.setMaccId("123");
         * 
         * AccountKey accountkey = new AccountKey("36846");
         * 
         * payeeModelList = new ArrayList <PayeeModel>(); PayeeModel payeeModel = new PayeeModel(); //payeeModel.setId("1");
         * payeeModel.setCode("0120"); payeeModel.setName("Test"); payeeModel.setPayeeType(PayeeType.LINKED);
         * payeeModel.setReference("123456789"); payeeModelList.add(payeeModel);
         * 
         * payeeList = new ArrayList <Payee>(); GenericPayee payee = new GenericPayee(); payee.setName("Test");
         * payee.setCode("0120"); payee.setPayeeType(PayeeType.LINKED); payee.setReference(payeeModel.getReference());
         * 
         * payeeList.add(payee);
         * 
         * portfolioRequest = new PortfolioRequestModel(); portfolioRequest.setPortfolioId("36846");
         * 
         * paymentDtoObjAccount = new PaymentDto(); paymentDtoObjAccount.setAmount(new BigDecimal(120));
         * paymentDtoObjAccount.setDescription("Add Test Payee"); paymentDtoObjAccount.setDeviceNumber("0098734562");
         * paymentDtoObjAccount.setKey(accountkey); PayeeDto payeeDto = new PayeeDto(); payeeDto.setAccountName("Denis Beecham");
         * payeeDto.setCode("001234568976"); payeeDto.setPayeeType(PayeeType.LINKED.name()); payeeDto.setNickname("nickname2");
         * payeeDto.setSaveToList("save"); paymentDtoObjAccount.setToPayteeDto(payeeDto);
         * 
         * paymentDtoObjBiller = new PaymentDto(); paymentDtoObjBiller.setAmount(new BigDecimal(120));
         * paymentDtoObjBiller.setDescription("Add Test Biller"); paymentDtoObjBiller.setDeviceNumber("0098734562");
         * paymentDtoObjBiller.setKey(accountkey); PayeeDto payeeDtoBiller = new PayeeDto(); payeeDtoBiller.setAccountName(
         * "Denis Beecham"); payeeDtoBiller.setCode("001234568976"); payeeDtoBiller.setFixedCRN(true);
         * payeeDtoBiller.setPayeeType(PayeeType.BPAY.name()); payeeDtoBiller.setNickname("nickname2");
         * payeeDtoBiller.setSaveToList("save"); paymentDtoObjBiller.setToPayteeDto(payeeDtoBiller);
         * 
         * bpayBiller = new BpayBiller(); bpayBiller.setCrnType(CRNType.CRN); bpayBiller.setBillerCode("0000001008");
         * bpayBiller.setBillerName("MOLONG LTD");
         * 
         * Mockito.when(portfolioInterface.getCashAccount()).thenReturn(cashAccountModel);
         * Mockito.when(portfolioService.loadPortfolio(anyString())).thenReturn(portfolioInterface);
         * 
         * Mockito.when(avaloqAddressBookIntegrationService.loadPayees(portfolioRequest, new ServiceErrorsImpl()))
         * .thenReturn(payeeList);
         * 
         * Mockito.when(bpayBillerCodeRepository.load(Mockito.anyString())).thenReturn(bpayBiller);
         * 
         * request = new MockHttpServletRequest();
         * 
         * response = new MockHttpServletResponse(); session = new MockHttpSession(); request.setSession(session);
         */

    }

    @Test
    public void testAddPayment() {
        /*
         * Payee payee = toGenericPayee(paymentDtoObjAccount);
         * 
         * when(avaloqAddressBookIntegrationService.addPayee(any(PortfolioRequest.class),
         * any(ServiceErrorsImpl.class))).thenReturn(payee);
         * 
         * PaymentDto paymentDto = payeeDtoService.addPayee(paymentDtoObjAccount, new ServiceErrorsImpl());
         * 
         * assertNotNull(paymentDto); assertNotNull(paymentDto.getToPayteeDto());
         * assertEquals(paymentDto.getToPayteeDto().getPayeeType(), PayeeType.LINKED.name());
         */
    }

    @Test
    public void testAddBiller() {

        /*
         * Payee payee = toGenericPayee(paymentDtoObjBiller);
         * 
         * when(avaloqAddressBookIntegrationService.addPayee(any(PortfolioRequest.class),
         * any(ServiceErrorsImpl.class))).thenReturn(payee);
         * 
         * PaymentDto paymentDto = payeeDtoService.addPayee(paymentDtoObjBiller, new ServiceErrorsImpl());
         * 
         * assertNotNull(paymentDto); assertNotNull(paymentDto.getToPayteeDto());
         * assertEquals(paymentDto.getToPayteeDto().getPayeeType(), PayeeType.BPAY.name());
         */
    }

    private GenericPayee toGenericPayee(PaymentDto paymentDtoKeyedObj) {
        /*
         * GenericPayee payee = new GenericPayee();
         * payee.setPayeeType(PayeeType.valueOf(paymentDtoKeyedObj.getToPayteeDto().getPayeeType()));
         * payee.setCode(paymentDtoKeyedObj.getToPayteeDto().getCode());
         * payee.setReference(paymentDtoKeyedObj.getToPayteeDto().getCrn());
         * payee.setName(paymentDtoKeyedObj.getToPayteeDto().getAccountName());
         * payee.setNickname(paymentDtoKeyedObj.getToPayteeDto().getNickname()); payee.setPrimary(paymentDtoKeyedObj.isPrimary());
         * 
         * return payee;
         */
        return null;
    }

    /*
     * @Test public void testAddPayee() {
     */
    /*
     * AccountKey accountkey = new AccountKey("36846"); paymentDtoObjBiller = new PaymentDto(); paymentDtoObjBiller.setAmount(new
     * BigDecimal(120)); paymentDtoObjBiller.setDescription("Add Test Biller"); paymentDtoObjBiller.setDeviceNumber("0098734562");
     * paymentDtoObjBiller.setKey(accountkey); PayeeDto payeeDtoBiller = new PayeeDto(); payeeDtoBiller.setAccountName(
     * "Denis Beecham"); payeeDtoBiller.setCode("001234568976"); payeeDtoBiller.setFixedCRN(true);
     * payeeDtoBiller.setPayeeType(PayeeType.BPAY.name()); payeeDtoBiller.setNickname("nickname2");
     * payeeDtoBiller.setSaveToList("save"); paymentDtoObjBiller.setToPayteeDto(payeeDtoBiller);
     * 
     * //when(avaloqAddressBookIntegrationService.addPayee(any(PortfolioRequest.class),
     * any(ServiceErrorsImpl.class))).thenReturn(payee);
     * 
     * //UpdateAccountDetailResponse response = avaloqAccountIntegrationServiceImpl.addNewBillerDetail(biller, serviceErrors);
     * 
     * PaymentDto paymentDto = payeeDtoService.addPayee(paymentDtoObjBiller, new ServiceErrorsImpl());
     */

    // }

    /*
     * @Test public void testDeletePayee() {
     * 
     * PaymentDto paymentDto;
     * 
     * Mockito.when(payeeDetailsIntegrationService.loadPayeeDetails(Mockito.any(WrapAccountIdentifier.class),
     * Mockito.any(ServiceErrorsImpl.class))).thenReturn(payeeDetails);
     * 
     * Mockito.when(bpayBillerCodeRepository.load(paymentDtoKeyedObj.getToPayteeDto().getCode())).thenReturn(bpayBiller);
     * bpayBiller.setBillerName("Denis Beecham");
     * 
     * Mockito.when(avaloqAccountIntegrationServiceImpl.deleteExistingBillerDetail(Mockito.any(BillerRequest.class),
     * Mockito.any(ServiceErrorsImpl.class))).thenReturn(updateAccountDetailResponse);
     * updateAccountDetailResponse.setUpdatedFlag(true);
     * 
     * paymentDto = payeeDtoService.deletePayee(paymentDtoKeyedObj, new ServiceErrorsImpl());
     * 
     * assertNotNull(paymentDto); assertEquals("Denis Beecham", paymentDto.getToPayteeDto().getAccountName());
     * assertEquals("123456", paymentDto.getToPayteeDto().getAccountId()); assertEquals("Nickname1",
     * paymentDto.getToPayteeDto().getNickname()); assertEquals("234567", paymentDto.getToPayteeDto().getCode());
     * assertEquals("345678", paymentDto.getToPayteeDto().getCrn()); assertEquals(false, paymentDto.getToPayteeDto().isPrimary());
     * assertEquals("BPAY", paymentDto.getToPayteeDto().getPayeeType());
     * 
     * }
     */

    @Test
    public void testDeletePayee() {

        PaymentDto paymentDto;

        if (null != paymentDtoKeyedObj.getToPayteeDto().getPayeeType()
                && PayeeType.valueOf(paymentDtoKeyedObj.getToPayteeDto().getPayeeType()).equals(PayeeType.BPAY)) {

            payeeDto.setPayeeType(PayeeType.BPAY.name());

            Mockito.when(bpayBillerCodeRepository.load("any")).thenReturn(bpayBiller);
            bpayBiller.setBillerName("Denis Beecham");

            Mockito.when(avaloqAccountIntegrationServiceImpl.deleteExistingBillerDetail(Mockito.any(BillerRequest.class),
                    Mockito.any(ServiceErrorsImpl.class))).thenReturn(updateAccountDetailResponse);
            updateAccountDetailResponse.setUpdatedFlag(true);

            paymentDto = payeeDtoService.deletePayee(paymentDtoKeyedObj, new ServiceErrorsImpl());

            assertNotNull(paymentDto);
            assertEquals("Denis Beecham", paymentDto.getToPayteeDto().getAccountName());
            assertEquals("123456", paymentDto.getToPayteeDto().getAccountId());
            assertEquals("Nickname1", paymentDto.getToPayteeDto().getNickname());
            assertEquals("234567", paymentDto.getToPayteeDto().getCode());
            assertEquals("345678", paymentDto.getToPayteeDto().getCrn());
            assertEquals(false, paymentDto.getToPayteeDto().isPrimary());
            assertEquals("BPAY", paymentDto.getToPayteeDto().getPayeeType());

        } else if (null != paymentDtoKeyedObj.getToPayteeDto().getPayeeType()
                && PayeeType.valueOf(paymentDtoKeyedObj.getToPayteeDto().getPayeeType()).equals(PayeeType.PAY_ANYONE)) {

            payeeDto.setPayeeType(PayeeType.PAY_ANYONE.name());

            Mockito.when(avaloqAccountIntegrationServiceImpl.deleteExistingPayeeDetail(Mockito.any(PayeeRequest.class),
                    Mockito.any(ServiceErrorsImpl.class))).thenReturn(updateAccountDetailResponse);
            updateAccountDetailResponse.setUpdatedFlag(true);

            paymentDto = payeeDtoService.deletePayee(paymentDtoKeyedObj, new ServiceErrorsImpl());

            assertNotNull(paymentDto);
            assertEquals("Denis Beecham", paymentDto.getToPayteeDto().getAccountName());
            assertEquals("123456", paymentDto.getToPayteeDto().getAccountId());
            assertEquals("Nickname1", paymentDto.getToPayteeDto().getNickname());
            assertEquals("234567", paymentDto.getToPayteeDto().getCode());
            assertEquals("345678", paymentDto.getToPayteeDto().getCrn());
            assertEquals(false, paymentDto.getToPayteeDto().isPrimary());
            assertEquals("PAY_ANYONE", paymentDto.getToPayteeDto().getPayeeType());
        } else if (null != paymentDtoKeyedObj.getToPayteeDto().getPayeeType()
                && PayeeType.valueOf(paymentDtoKeyedObj.getToPayteeDto().getPayeeType()).equals(PayeeType.LINKED)) {
            payeeDto.setPayeeType(PayeeType.LINKED.name());

            Mockito.when(avaloqAccountIntegrationServiceImpl.deleteLinkedAccount(Mockito.any(DeleteLinkedAccRequest.class),
                    Mockito.any(ServiceErrorsImpl.class))).thenReturn(updateAccountDetailResponse);
            updateAccountDetailResponse.setUpdatedFlag(true);

            paymentDto = payeeDtoService.deletePayee(paymentDtoKeyedObj, new ServiceErrorsImpl());

            assertNotNull(paymentDto);
            assertEquals("Denis Beecham", paymentDto.getToPayteeDto().getAccountName());
            assertEquals("123456", paymentDto.getToPayteeDto().getAccountId());
            assertEquals("Nickname1", paymentDto.getToPayteeDto().getNickname());
            assertEquals("234567", paymentDto.getToPayteeDto().getCode());
            assertEquals("345678", paymentDto.getToPayteeDto().getCrn());
            assertEquals(false, paymentDto.getToPayteeDto().isPrimary());
            assertEquals("LINKED", paymentDto.getToPayteeDto().getPayeeType());
        }

    }

    @Test
    public void testUpdatePayee() {
        PaymentDto paymentDto;

        if (null != paymentDtoKeyedObj.getToPayteeDto().getPayeeType()
                && PayeeType.valueOf(paymentDtoKeyedObj.getToPayteeDto().getPayeeType()).equals(PayeeType.BPAY)) {

            payeeDto.setPayeeType(PayeeType.BPAY.name());

            Mockito.when(bpayBillerCodeRepository.load("any")).thenReturn(bpayBiller);
            bpayBiller.setBillerName("Denis Beecham");

            Mockito.when(avaloqAccountIntegrationServiceImpl.updateExistingBillerDetail(Mockito.any(BillerRequest.class),
                    Mockito.any(ServiceErrorsImpl.class))).thenReturn(updateAccountDetailResponse);
            updateAccountDetailResponse.setUpdatedFlag(true);

            paymentDto = payeeDtoService.updatePayee(paymentDtoKeyedObj, new ServiceErrorsImpl());

            assertNotNull(paymentDto);
            assertEquals("Denis Beecham", paymentDto.getToPayteeDto().getAccountName());
            assertEquals("123456", paymentDto.getToPayteeDto().getAccountId());
            assertEquals("Nickname1", paymentDto.getToPayteeDto().getNickname());
            assertEquals("234567", paymentDto.getToPayteeDto().getCode());
            assertEquals("345678", paymentDto.getToPayteeDto().getCrn());
            assertEquals(false, paymentDto.getToPayteeDto().isPrimary());
            assertEquals("BPAY", paymentDto.getToPayteeDto().getPayeeType());

        } else if (null != paymentDtoKeyedObj.getToPayteeDto().getPayeeType()
                && PayeeType.valueOf(paymentDtoKeyedObj.getToPayteeDto().getPayeeType()).equals(PayeeType.PAY_ANYONE)) {

            payeeDto.setPayeeType(PayeeType.PAY_ANYONE.name());

            Mockito.when(avaloqAccountIntegrationServiceImpl.updateExistingPayeeDetail(Mockito.any(PayeeRequest.class),
                    Mockito.any(ServiceErrorsImpl.class))).thenReturn(updateAccountDetailResponse);
            updateAccountDetailResponse.setUpdatedFlag(true);

            paymentDto = payeeDtoService.updatePayee(paymentDtoKeyedObj, new ServiceErrorsImpl());

            assertNotNull(paymentDto);
            assertEquals("Denis Beecham", paymentDto.getToPayteeDto().getAccountName());
            assertEquals("123456", paymentDto.getToPayteeDto().getAccountId());
            assertEquals("Nickname1", paymentDto.getToPayteeDto().getNickname());
            assertEquals("234567", paymentDto.getToPayteeDto().getCode());
            assertEquals("345678", paymentDto.getToPayteeDto().getCrn());
            assertEquals(false, paymentDto.getToPayteeDto().isPrimary());
            assertEquals("PAY_ANYONE", paymentDto.getToPayteeDto().getPayeeType());
        } else if (null != paymentDtoKeyedObj.getToPayteeDto().getPayeeType()
                && PayeeType.valueOf(paymentDtoKeyedObj.getToPayteeDto().getPayeeType()).equals(PayeeType.LINKED)) {
            payeeDto.setPayeeType(PayeeType.LINKED.name());

            Mockito.when(avaloqAccountIntegrationServiceImpl.updateLinkedAccount(Mockito.any(LinkedAccRequest.class),
                    Mockito.any(ServiceErrorsImpl.class))).thenReturn(updateAccountDetailResponse);
            updateAccountDetailResponse.setUpdatedFlag(true);

            paymentDto = payeeDtoService.updatePayee(paymentDtoKeyedObj, new ServiceErrorsImpl());

            assertNotNull(paymentDto);
            assertEquals("Denis Beecham", paymentDto.getToPayteeDto().getAccountName());
            assertEquals("123456", paymentDto.getToPayteeDto().getAccountId());
            assertEquals("Nickname1", paymentDto.getToPayteeDto().getNickname());
            assertEquals("234567", paymentDto.getToPayteeDto().getCode());
            assertEquals("345678", paymentDto.getToPayteeDto().getCrn());
            assertEquals(false, paymentDto.getToPayteeDto().isPrimary());
            assertEquals("LINKED", paymentDto.getToPayteeDto().getPayeeType());
        }

    }

    @Test
    public void testAddPayee() {
        PaymentDto paymentDto;

        if (null != paymentDtoKeyedObj.getToPayteeDto().getPayeeType()
                && PayeeType.valueOf(paymentDtoKeyedObj.getToPayteeDto().getPayeeType()).equals(PayeeType.BPAY)) {

            PayeeDetailsImpl payeeDetailsImpl = new PayeeDetailsImpl();

            Mockito.when(payeeDetailsIntegrationService.loadPayeeDetails(Mockito.any(WrapAccountIdentifier.class),
                    Mockito.any(ServiceErrorsImpl.class))).thenReturn(payeeDetailsImpl);

            payeeDetailsImpl.setBpayBillerPayeeList(billerList);

            payeeDto.setPayeeType(PayeeType.BPAY.name());

            Mockito.when(bpayBillerCodeRepository.load("any")).thenReturn(bpayBiller);
            bpayBiller.setBillerName("Denis Beecham");

            Mockito.when(avaloqAccountIntegrationServiceImpl.addNewBillerDetail(Mockito.any(BillerRequest.class),
                    Mockito.any(ServiceErrorsImpl.class))).thenReturn(updateAccountDetailResponse);
            updateAccountDetailResponse.setUpdatedFlag(true);

            paymentDto = payeeDtoService.addPayee(paymentDtoKeyedObj, new ServiceErrorsImpl());

            assertNotNull(paymentDto);
            assertEquals("Denis Beecham", paymentDto.getToPayteeDto().getAccountName());
            assertEquals("123456", paymentDto.getToPayteeDto().getAccountId());
            assertEquals("Nickname1", paymentDto.getToPayteeDto().getNickname());
            assertEquals("234567", paymentDto.getToPayteeDto().getCode());
            assertEquals("345678", paymentDto.getToPayteeDto().getCrn());
            assertEquals(false, paymentDto.getToPayteeDto().isPrimary());
            assertEquals("BPAY", paymentDto.getToPayteeDto().getPayeeType());

        } else if (null != paymentDtoKeyedObj.getToPayteeDto().getPayeeType()
                && PayeeType.valueOf(paymentDtoKeyedObj.getToPayteeDto().getPayeeType()).equals(PayeeType.PAY_ANYONE)) {

            payeeDto.setPayeeType(PayeeType.PAY_ANYONE.name());

            Mockito.when(avaloqAccountIntegrationServiceImpl.addNewRegPayeeDetail(Mockito.any(PayeeRequest.class),
                    Mockito.any(ServiceErrorsImpl.class))).thenReturn(updateAccountDetailResponse);
            updateAccountDetailResponse.setUpdatedFlag(true);

            paymentDto = payeeDtoService.addPayee(paymentDtoKeyedObj, new ServiceErrorsImpl());

            assertNotNull(paymentDto);
            assertEquals("Denis Beecham", paymentDto.getToPayteeDto().getAccountName());
            assertEquals("123456", paymentDto.getToPayteeDto().getAccountId());
            assertEquals("Nickname1", paymentDto.getToPayteeDto().getNickname());
            assertEquals("234567", paymentDto.getToPayteeDto().getCode());
            assertEquals("345678", paymentDto.getToPayteeDto().getCrn());
            assertEquals(false, paymentDto.getToPayteeDto().isPrimary());
            assertEquals("PAY_ANYONE", paymentDto.getToPayteeDto().getPayeeType());
        } else if (null != paymentDtoKeyedObj.getToPayteeDto().getPayeeType()
                && PayeeType.valueOf(paymentDtoKeyedObj.getToPayteeDto().getPayeeType()).equals(PayeeType.LINKED)) {
            payeeDto.setPayeeType(PayeeType.LINKED.name());

            Mockito.when(avaloqAccountIntegrationServiceImpl.addLinkedAccount(Mockito.any(LinkedAccRequest.class),
                    Mockito.any(ServiceErrorsImpl.class))).thenReturn(updateAccountDetailResponse);
            updateAccountDetailResponse.setUpdatedFlag(true);

            paymentDto = payeeDtoService.addPayee(paymentDtoKeyedObj, new ServiceErrorsImpl());

            assertNotNull(paymentDto);
            assertEquals("Denis Beecham", paymentDto.getToPayteeDto().getAccountName());
            assertEquals("123456", paymentDto.getToPayteeDto().getAccountId());
            assertEquals("Nickname1", paymentDto.getToPayteeDto().getNickname());
            assertEquals("234567", paymentDto.getToPayteeDto().getCode());
            assertEquals("345678", paymentDto.getToPayteeDto().getCrn());
            assertEquals(false, paymentDto.getToPayteeDto().isPrimary());
            assertEquals("LINKED", paymentDto.getToPayteeDto().getPayeeType());
        }
    }

    @Test
    public void shouldReturnLinkedAccRequestTest() {
        LinkedAccRequest request = payeeDtoService.makeLinkedAccountRequest(paymentDtoKeyedObj, payeeDetails);
        assertNotNull(request);
        assertEquals(request.getAccountKey().getId(), paymentDtoKeyedObj.getKey().getAccountId());
        assertEquals(request.getModificationIdentifier(), payeeDetails.getModifierSeqNumber());
        assertNotNull(request.getLinkedAccount());
        assertEquals(request.getLinkedAccount().getAccountNumber(), paymentDtoKeyedObj.getToPayteeDto().getAccountId());
        assertEquals(request.getLinkedAccount().getBsb(), paymentDtoKeyedObj.getToPayteeDto().getCode());
        assertEquals(request.getLinkedAccount().getName(), paymentDtoKeyedObj.getToPayteeDto().getAccountName());
        assertEquals(request.getLinkedAccount().getNickName(), paymentDtoKeyedObj.getToPayteeDto().getNickname());
        assertEquals(request.getLinkedAccount().getLimit(), paymentDtoKeyedObj.getLimit());
        assertFalse(request.getLinkedAccount().isPrimary());
        assertEquals(request.getLinkedAccount().getCurrency(), CurrencyType.AustralianDollar);
    }

    @Test
    public void shouldReturnPayeeRequestTest() {
        PayeeRequest payeeRequest = payeeDtoService.makePayeeRequest(paymentDtoKeyedObj, payeeDetails);
        assertNotNull(payeeRequest);
        assertEquals(payeeRequest.getAccountKey().getId(), paymentDtoKeyedObj.getKey().getAccountId());
        assertEquals(payeeRequest.getModificationIdentifier(), payeeDetails.getModifierSeqNumber());
        assertNotNull(payeeRequest.getBankAccount());
        assertEquals(payeeRequest.getBankAccount().getAccountNumber(), paymentDtoKeyedObj.getToPayteeDto().getAccountId());
        assertEquals(payeeRequest.getBankAccount().getName(), paymentDtoKeyedObj.getToPayteeDto().getAccountName());
        assertEquals(payeeRequest.getBankAccount().getNickName(), paymentDtoKeyedObj.getToPayteeDto().getNickname());
        assertEquals(payeeRequest.getBankAccount().getBsb(), paymentDtoKeyedObj.getToPayteeDto().getCode());
    }

    @Test
    public void shouldReturnBillerRequestTest() {
        BpayBiller bpayBiller = new BpayBiller();
        bpayBiller.setBillerName("Test123");
        Mockito.when(bpayBillerCodeRepository.load(paymentDtoKeyedObj.getToPayteeDto().getCode())).thenReturn(bpayBiller);
        BillerRequest biller = payeeDtoService.makeBillerRequest(paymentDtoKeyedObj, payeeDetails);
        assertNotNull(biller);
        assertEquals(biller.getAccountKey().getId(), paymentDtoKeyedObj.getKey().getAccountId());
        assertEquals(biller.getModificationIdentifier(), payeeDetails.getModifierSeqNumber());
        assertNotNull(biller.getBillerDetail());
        assertEquals(biller.getBillerDetail().getName(), paymentDtoKeyedObj.getToPayteeDto().getAccountName());
        assertEquals(biller.getBillerDetail().getNickName(), paymentDtoKeyedObj.getToPayteeDto().getNickname());
        assertEquals(biller.getBillerDetail().getBillerCode(), paymentDtoKeyedObj.getToPayteeDto().getCode());
        assertEquals(biller.getBillerDetail().getCRN(), paymentDtoKeyedObj.getToPayteeDto().getCrn());
    }

    @Test
    public void shouldReturnDeleteLinkedAccRequestTest() {
        DeleteLinkedAccRequest request = payeeDtoService.makeDeleteLinkedAccountRequest(paymentDtoKeyedObj, payeeDetails);
        assertNotNull(request);
        assertEquals(request.getAccountKey().getId(), paymentDtoKeyedObj.getKey().getAccountId());
        assertEquals(request.getModificationIdentifier(), payeeDetails.getModifierSeqNumber());
        assertNotNull(request.getBankAccount());
        assertEquals(request.getBankAccount().getAccountNumber(), paymentDtoKeyedObj.getToPayteeDto().getAccountId());
        assertEquals(request.getBankAccount().getBsb(), paymentDtoKeyedObj.getToPayteeDto().getCode());
        assertEquals(request.getBankAccount().getName(), paymentDtoKeyedObj.getToPayteeDto().getAccountName());
        assertEquals(request.getBankAccount().getNickName(), paymentDtoKeyedObj.getToPayteeDto().getNickname());
    }

    @Test
    public void testMakeBillerRequest_whenDeleteOp_thenBpayBillerCodeRepoNotCalled() {
        PayeeDto payeeDto = new PayeeDto();
        payeeDto.setCode("234567");
        payeeDto.setCrn("345678");
        PaymentDto paymentDtoKeyedObj = new PaymentDto();
        paymentDtoKeyedObj.setToPayteeDto(payeeDto);
        paymentDtoKeyedObj.setKey(new AccountKey("123456"));
        paymentDtoKeyedObj.setOpType("DELETE");

        BillerRequest billerReq = payeeDtoService.makeBillerRequest(paymentDtoKeyedObj, payeeDetails);
        verify(bpayBillerCodeRepository, Mockito.times(0)).load(Mockito.anyString());

        assertNotNull(billerReq);
        assertEquals(payeeDto.getCode(), billerReq.getBillerDetail().getBillerCode());
        assertEquals(payeeDto.getCrn(), billerReq.getBillerDetail().getCRN());
    }

    @Test
    public void testMakeBillerRequest_whenNotDeleteOp_thenBpayBillerCodeRepoCalled() {
        PayeeDto payeeDto = new PayeeDto();
        payeeDto.setCode("234567");
        payeeDto.setCrn("345678");
        PaymentDto paymentDtoKeyedObj = new PaymentDto();
        paymentDtoKeyedObj.setToPayteeDto(payeeDto);
        paymentDtoKeyedObj.setKey(new AccountKey("123456"));

        BpayBiller bpayBiller = new BpayBiller();
        bpayBiller.setBillerName("Denis Beecham");

        Mockito.when(bpayBillerCodeRepository.load(Mockito.anyString())).thenReturn(bpayBiller);

        BillerRequest billerReq = payeeDtoService.makeBillerRequest(paymentDtoKeyedObj, payeeDetails);
        verify(bpayBillerCodeRepository, atLeastOnce()).load(Mockito.anyString());

        assertNotNull(billerReq);
        assertEquals(payeeDto.getCode(), billerReq.getBillerDetail().getBillerCode());
        assertEquals(payeeDto.getCrn(), billerReq.getBillerDetail().getCRN());
        assertEquals(bpayBiller.getBillerName(), billerReq.getBillerDetail().getName());
    }

    @Test(expected = BadRequestException.class)
    public void testValidateAddPayee_LinkedAndNotAssocAcc() {
        mockAccountAndProfile();
        String accountNumber ="NOT_ASSOCIATED_NUMBER";
        String bsb = "NOT_ASSOCIATED_BSB";
        payeeDtoService.validate(getPaymentDto(PayeeType.LINKED, accountNumber, bsb), new ServiceErrorsImpl());
    }

    @Test
    public void testValidateAddPayee_LinkedAndAssocAcc() {
        mockAccountAndProfile();
        String accountNumber ="ASSOCIATED_NUMBER";
        String bsb = "ASSOCIATED_BSB";
        PaymentDto paymentDto = payeeDtoService.validate(getPaymentDto(PayeeType.LINKED, accountNumber, bsb), new ServiceErrorsImpl());
        assertNotNull(paymentDto);
    }

    @Test
    public void testValidateAddPayee_NotLinkedAndAssocAcc() {
        mockAccountAndProfile();
        String accountNumber ="ASSOCIATED_NUMBER";
        String bsb = "ASSOCIATED_BSB";
        PaymentDto paymentDto = payeeDtoService.validate(getPaymentDto(PayeeType.PAY_ANYONE, accountNumber, bsb), new ServiceErrorsImpl());
        assertNotNull(paymentDto);
    }

    @Test
    public void testValidateAddPayee_NotLinkedAndNotAssocAcc() {
        mockAccountAndProfile();
        String accountNumber ="NOT_ASSOCIATED_NUMBER";
        String bsb = "NOT_ASSOCIATED_BSB";
        PaymentDto paymentDto = payeeDtoService.validate(getPaymentDto(PayeeType.PAY_ANYONE, accountNumber, bsb), new ServiceErrorsImpl());
        assertNotNull(paymentDto);
    }

    @Test
    public void testValidateAddPayee_NotLinkedAndNotAssocAccNum() {
        mockAccountAndProfile();
        String accountNumber ="NOT_ASSOCIATED_NUMBER";
        String bsb = "ASSOCIATED_BSB";
        PaymentDto paymentDto = payeeDtoService.validate(getPaymentDto(PayeeType.PAY_ANYONE, accountNumber, bsb), new ServiceErrorsImpl());
        assertNull(paymentDto.getErrors());
    }

    @Test
    public void testValidateAddPayee_NotLinkedAndNotAssocAccBsb() {
        mockAccountAndProfile();
        String accountNumber ="ASSOCIATED_NUMBER";
        String bsb = "NOT_ASSOCIATED_BSB";
        PaymentDto paymentDto = payeeDtoService.validate(getPaymentDto(PayeeType.PAY_ANYONE, accountNumber, bsb), new ServiceErrorsImpl());
        assertNull(paymentDto.getErrors());
    }

    private void mockAccountAndProfile() {
        Map<com.bt.nextgen.service.integration.account.AccountKey, WrapAccount> accountMap = new HashMap<>();
        WrapAccount account = mock(WrapAccount.class);
        when(account.getApprovers()).thenReturn(Collections.singletonList(ClientKey.valueOf("clientKey")));
        when(account.getAccountKey()).thenReturn(com.bt.nextgen.service.integration.account.AccountKey.valueOf("123456789"));
        when(account.getAccountStatus()).thenReturn(AccountStatus.ACTIVE);
        when(account.getAccountNumber()).thenReturn("123456789");
        when(account.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        accountMap.put(account.getAccountKey(), account);

        String samlString = SamlUtil.loadSaml();
        token = new SamlToken(samlString);
        when(profileService.getEffectiveProfile()).thenReturn(userProfile);
        when(userProfile.getToken()).thenReturn(token);
        when(profileService.isInvestor()).thenReturn(true);

        when(avaloqAccountIntegrationServiceImpl.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(accountMap);
        when(customerDataManagementIntegrationService.retrieveCustomerInformation(any(CustomerManagementRequest.class), anyList(), any(ServiceErrors.class))).thenReturn(getCustomerData());
    }


    private PaymentDto getPaymentDto(PayeeType payeeType, String accountNumber, String bsb) {
        PaymentDto paymentDto = new PaymentDto();
        AccountKey key = new AccountKey("123456789");
        paymentDto.setKey(key);

        PayeeDto fromDto = new PayeeDto();
        fromDto.setCode("262-786");
        fromDto.setAccountName("Adrian Demo Smith");
        fromDto.setPayeeType(payeeType.name());

        PayeeDto toDto = new PayeeDto();
        toDto.setCode(bsb);
        toDto.setAccountId(accountNumber);
        toDto.setAccountName("Denis Beecham");
        toDto.setNickname("Nickname1");
        toDto.setCrn("124456789");
        toDto.setAccountKey(EncodedString.fromPlainText("120061455").toString());
        toDto.setSaveToList("save");
        toDto.setLimit("1000");
        toDto.setPayeeType(payeeType.toString());

        paymentDto.setFromPayDto(fromDto);
        paymentDto.setToPayteeDto(toDto);
        paymentDto.setAmount(new BigDecimal(12));
        paymentDto.setDescription("Test Payment");
        paymentDto.setTransactionDate("");

        return paymentDto;
    }

    private CustomerData getCustomerData() {
        List<BankAccount>bankAccountList = new ArrayList<>();
        BankAccountImpl bankAccount1 = new BankAccountImpl();
        BankAccountImpl bankAccount2 = new BankAccountImpl();
        bankAccount1.setBsb("ASSOCIATED_BSB");
        bankAccount1.setAccountNumber("ASSOCIATED_NUMBER");
        bankAccount1.setName("Westpac Account 1");
        bankAccountList.add(bankAccount1);
        CustomerData response = new CustomerDataImpl();
        response.setBankAccounts(bankAccountList);

        return response;
    }
}
