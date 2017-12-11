package com.bt.nextgen.api.inspecietransfer.v2.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.service.AssetDtoConverter;
import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto;
import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferKey;
import com.bt.nextgen.api.inspecietransfer.v2.model.SettlementRecordDto;
import com.bt.nextgen.api.inspecietransfer.v2.model.SettlementRecordDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v2.model.SponsorDetailsDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v2.model.TaxParcelDto;
import com.bt.nextgen.api.inspecietransfer.v2.validation.InspecieTransferDtoErrorMapper;
import com.bt.nextgen.api.inspecietransfer.v2.validation.InspecieTransferDtoErrorMapperImpl;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.transfer.SponsorDetailsImpl;
import com.bt.nextgen.service.avaloq.transfer.TaxParcelImpl;
import com.bt.nextgen.service.avaloq.transfer.TransferDetailsImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.transaction.TransactionResponse;
import com.bt.nextgen.service.integration.transaction.TransactionValidation;
import com.bt.nextgen.service.integration.transfer.InspecieAsset;
import com.bt.nextgen.service.integration.transfer.InspecieTransferIntegrationService;
import com.bt.nextgen.service.integration.transfer.TaxParcel;
import com.bt.nextgen.service.integration.transfer.TransferDetails;
import com.bt.nextgen.service.integration.transfer.TransferType;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.JobKey;

@RunWith(MockitoJUnitRunner.class)
public class InspecieTransferDtoServiceTest {

    @InjectMocks
    private InspecieTransferDtoServiceImpl transferDtoService;

    @Mock
    private InspecieTransferIntegrationService transferIntegrationService;

    @Mock
    private TransferAssetHelper assetHelper;

    @Mock
    private TransactionResponse txnRsp;

    @Mock
    private InspecieTransferDtoErrorMapper inspecieErrorMapper;

    @Mock
    private BrokerIntegrationService brokerService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private AssetDtoConverter assetDtoConverter;

    @Spy
    private final InspecieTransferDtoErrorMapper inspecieTransferErrorMapper = new InspecieTransferDtoErrorMapperImpl();

    private List<DomainApiErrorDto> apiErrors;

    private AssetDto assetDto;
    private Map<String, AssetDto> assetDtos;

    private Collection<Broker> brokers;

    private InspecieTransferDtoImpl transferDto;

    private TransferDetailsImpl transferDetails;

    private List<ValidationError> validationErrors;

    @Before
    public void setup() throws Exception {

        apiErrors = new ArrayList<>();
        apiErrors.add(new DomainApiErrorDto("errorId", "domain", "reason", "message", DomainApiErrorDto.ErrorType.WARNING));
        apiErrors.add(new DomainApiErrorDto("errorId2", "domain2", "reason2", "message2", DomainApiErrorDto.ErrorType.WARNING));

        validationErrors = new ArrayList<>();

        InspecieAsset asset = new InspecieAsset("111", BigDecimal.ONE);
        List<InspecieAsset> transferAssets = new ArrayList<>();
        transferAssets.add(asset);
        transferDetails = new TransferDetailsImpl("121", TransferType.LS_BROKER_SPONSORED, Boolean.FALSE, transferAssets);
        SponsorDetailsImpl sponsorDetails = new SponsorDetailsImpl();
        sponsorDetails.setInvestmentId("111");
        sponsorDetails.setSponsorId("222");
        transferDetails.setSponsorDetails(sponsorDetails);
        transferDetails.setDestContainerId(EncodedString.toPlainText("46804E8B5F179DA38D92E506C2A825BD771E85B9A85D17C6"));
        transferDetails.setAccountKey(AccountKey.valueOf("123434"));
        List<TransactionValidation> warnings = new ArrayList<TransactionValidation>();
        transferDetails.setWarnings(warnings);
        transferDetails.setValidationErrors(validationErrors);

        List<TaxParcel> taxParcel = new ArrayList<TaxParcel>();
        TaxParcel tarParcel1 = new TaxParcelImpl("assetId", new DateTime(), new DateTime(), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), BigDecimal.valueOf(10), BigDecimal.valueOf(10));
        taxParcel.add(tarParcel1);

        transferDetails.setTaxParcels(taxParcel);

        SettlementRecordDtoImpl settlementRecord = new SettlementRecordDtoImpl("111", "BHP", BigDecimal.ONE);
        List<SettlementRecordDto> assets = new ArrayList<>();
        assets.add(settlementRecord);

        transferDto = new InspecieTransferDtoImpl(TransferType.LS_BROKER_SPONSORED.getDisplayName(), new SponsorDetailsDtoImpl(),
                assets, "46804E8B5F179DA38D92E506C2A825BD771E85B9A85D17C6", new InspecieTransferKey(), Boolean.FALSE,
                new ArrayList<DomainApiErrorDto>());
        List<TaxParcelDto> taxParcels = new ArrayList<>();
        TaxParcelDto taxParcelDto = new TaxParcelDto("1231", new DateTime(), null, BigDecimal.TEN, BigDecimal.ONE,
                BigDecimal.ONE, BigDecimal.ONE);
        taxParcels.add(taxParcelDto);

        transferDto.setTaxParcels(taxParcels);

        AssetImpl assetImpl = new AssetImpl();
        assetImpl.setAssetId("111");
        assetImpl.setAssetCode("BHP");

        Mockito.when(assetHelper.toTransferAssetsDto(Mockito.anyList())).thenReturn(assets);

        BrokerUser brokerUser = Mockito.mock(BrokerUser.class);
        Mockito.when(brokerUser.getBankReferenceKey()).thenReturn(UserKey.valueOf("testUser"));
        Mockito.when(brokerUser.getJob()).thenReturn(JobKey.valueOf("testJob"));
        Mockito.when(brokerUser.getFirstName()).thenReturn("Bob");
        Mockito.when(brokerUser.getLastName()).thenReturn("Gilby");
        Mockito.when(brokerUser.getClientKey()).thenReturn(ClientKey.valueOf("testClient"));
        Mockito.when(brokerUser.isRegisteredOnline()).thenReturn(false);
        Mockito.when(brokerUser.getAge()).thenReturn(0);
        Mockito.when(brokerUser.isRegistrationOnline()).thenReturn(false);

        Mockito.when(inspecieErrorMapper.map(Mockito.anyList())).thenReturn(apiErrors);
        Mockito.when(brokerService.getBrokerUser(Mockito.any(UserKey.class), Mockito.any(ServiceErrors.class))).thenReturn(
                brokerUser);

        brokers = new ArrayList<Broker>();
        Broker broker = Mockito.mock(Broker.class);
        Mockito.when(broker.getKey()).thenReturn(BrokerKey.valueOf("testUser"));
        Mockito.when(broker.isPayableParty()).thenReturn(false);
        brokers.add(broker);

        Mockito.when(userProfileService.getUserId()).thenReturn("201601388");
        UserProfile userInfo = Mockito.mock(UserProfile.class);
        Mockito.when(userInfo.getClientKey()).thenReturn(ClientKey.valueOf("testClient"));

        Mockito.when(userProfileService.getActiveProfile()).thenReturn(userInfo);

        Mockito.when(brokerService.getBrokersForUser(Mockito.any(UserKey.class), Mockito.any(ServiceErrors.class))).thenReturn(
                brokers);
        Mockito.when(brokerService.getBroker(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class))).thenReturn(broker);

        WrapAccountDetail accountDetail = Mockito.mock(WrapAccountDetail.class);
        Mockito.when(accountDetail.getAdviserKey()).thenReturn(BrokerKey.valueOf("brokerKey"));
        Mockito.when(accountDetail.isOpen()).thenReturn(false);
        Mockito.when(accountDetail.getAdminFeeRate()).thenReturn(new BigDecimal("9.98"));
        Mockito.when(accountDetail.isHasMinCash()).thenReturn(false);
    }

    @Test
    public void testValidate() {
        Mockito.when(
                transferIntegrationService.validateTransfer(Mockito.any(TransferDetails.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(transferDetails);
        Mockito.when(inspecieErrorMapper.map(Mockito.anyList())).thenReturn(apiErrors);
        List<ValidationError> errors = new ArrayList<>();
        Mockito.when(txnRsp.getValidationErrors()).thenReturn(errors);

        InspecieTransferDto resultDto = transferDtoService.validate(transferDto, new ServiceErrorsImpl());
        Assert.assertNotNull(resultDto);
        Assert.assertEquals(resultDto.getSponsorDetails().getHin(), transferDetails.getSponsorDetails().getInvestmentId());
        Assert.assertEquals(resultDto.getTransferType(), transferDetails.getTransferType().getDisplayName());
        Assert.assertEquals(resultDto.getSettlementRecords().size(), transferDetails.getTransferAssets().size());

    }

    @Test
    public void testSubmit() {
        Mockito.when(
                transferIntegrationService.submitTransfer(Mockito.any(TransferDetails.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(transferDetails);
        List<TaxParcel> taxParcels = new ArrayList<>();
        TaxParcelImpl taxParcel = new TaxParcelImpl("assetId", new DateTime(), null, BigDecimal.TEN, BigDecimal.ONE,
                BigDecimal.ONE, BigDecimal.ONE);
        taxParcels.add(taxParcel);
        transferDetails.setTaxParcels(taxParcels);
        Mockito.when(inspecieErrorMapper.map(Mockito.anyList())).thenReturn(apiErrors);
        List<ValidationError> errors = new ArrayList<>();
        Mockito.when(txnRsp.getValidationErrors()).thenReturn(errors);

        InspecieTransferDto resultDto = transferDtoService.submit(transferDto, new ServiceErrorsImpl());
        Assert.assertNotNull(resultDto);
        Assert.assertFalse(resultDto.getTaxParcels().isEmpty());
        Assert.assertEquals(resultDto.getTaxParcels().size(), transferDetails.getTaxParcels().size());
        Assert.assertEquals(resultDto.getTransferType(), transferDetails.getTransferType().getDisplayName());
        Assert.assertEquals(resultDto.getSettlementRecords().size(), transferDetails.getTransferAssets().size());
        Assert.assertEquals(resultDto.getSponsorDetails().getHin(), transferDetails.getSponsorDetails().getInvestmentId());

    }

    @Test
    public void testLoad() {
        Mockito.when(
                transferIntegrationService.loadTransferDetails(Mockito.anyString(), Mockito.any(AccountKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(transferDetails);
        Mockito.when(inspecieErrorMapper.map(Mockito.anyList())).thenReturn(apiErrors);
        List<ValidationError> errors = new ArrayList<>();
        Mockito.when(txnRsp.getValidationErrors()).thenReturn(errors);

        InspecieTransferDto resultDto = transferDtoService.find(new InspecieTransferKey(
                "46804E8B5F179DA38D92E506C2A825BD771E85B9A85D17C6", "123"), new ServiceErrorsImpl());
        Assert.assertNotNull(resultDto);
        Assert.assertEquals(resultDto.getSponsorDetails().getHin(), transferDetails.getSponsorDetails().getInvestmentId());
        Assert.assertEquals(resultDto.getTransferType(), transferDetails.getTransferType().getDisplayName());
        Assert.assertEquals(resultDto.getSettlementRecords().size(), transferDetails.getTransferAssets().size());
    }
}