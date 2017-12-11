package com.bt.nextgen.api.modelportfolio.v2.service;

import com.bt.nextgen.api.modelportfolio.v2.model.sector.SectorPortfolioDto;
import com.bt.nextgen.api.modelportfolio.v2.service.sectorportfolio.SectorPortfolioDtoServiceImpl;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.bt.nextgen.service.avaloq.modelportfolio.sectorportfolio.SectorPortfolio;
import com.bt.nextgen.service.avaloq.modelportfolio.sectorportfolio.SectorPortfolioImpl;
import com.bt.nextgen.service.avaloq.modelportfolio.sectorportfolio.SectorPortfolioIntegrationService;
import com.bt.nextgen.service.avaloq.modelportfolio.sectorportfolio.SectorPortfolioKey;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.service.integration.broker.BrokerType;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class SectorPortfolioDtoServiceTest {

    @InjectMocks
    private final SectorPortfolioDtoServiceImpl sectorPortfolioDtoService = new SectorPortfolioDtoServiceImpl();

    @Mock
    SectorPortfolioIntegrationService sectorPortfolioIntegrationService;

    @Mock
    BrokerIntegrationService brokerService;

    @Mock
    UserProfileService userProfileService;

    SectorPortfolioKey emptySectorPortfolioKey;
    SectorPortfolioKey sectorPortfolioKey;
    List<SectorPortfolio> emptySectorPortfolioList;
    SectorPortfolioImpl sp1;
    SectorPortfolioImpl sp2;
    List<SectorPortfolio> sectorPortfolioList;

    @Before
    public void setup() throws Exception {

        sectorPortfolioList = new ArrayList<>();

        emptySectorPortfolioKey = null;
        sectorPortfolioKey = SectorPortfolioKey.valueOf("11111");

        sp1 = new SectorPortfolioImpl();
        sp1.setId("00001");
        sp1.setName("Domestic Equities");
        sp1.setCode("DEAL00001");
        sp1.setCategory("Active listed");
        sp1.setAssetClass("Australian Shares");
        sp1.setProductType("Investment");
        sp1.setIpsCount(BigInteger.valueOf(6));
        sp1.setStatus("Pending");
        sp1.setLastModifiedDate(new DateTime());
        sp1.setLastModifiedBy("Frank Wong (202303411)");

        sp2 = new SectorPortfolioImpl();
        sp1.setId("00002");
        sp1.setName("International Equities");
        sp1.setCode("DEAL00002");
        sp1.setCategory("Active managed");
        sp1.setAssetClass("International Shares");
        sp1.setProductType("Investment");
        sp1.setIpsCount(BigInteger.valueOf(3));
        sp1.setStatus("Open");
        sp1.setLastModifiedDate(new DateTime());
        sp1.setLastModifiedBy("Harris Kerr (201663867)");

        sectorPortfolioList.add(sp1);
        sectorPortfolioList.add(sp2);

        mockOtherServices();
    }

    @Test
    public void testFindAll() {

        Mockito.when(
                sectorPortfolioIntegrationService.loadSectorPortfoliosForManager(Mockito.any(BrokerKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(sectorPortfolioList);

        List<SectorPortfolioDto> dtos = sectorPortfolioDtoService.findAll(new ServiceErrorsImpl());

        testSectorPortfolioDtos(dtos);
    }

    @Test
    public void testSearch() {

        Mockito.when(
                sectorPortfolioIntegrationService.loadSectorPortfolios(Mockito.anyListOf(SectorPortfolioKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(sectorPortfolioList);

        List<SectorPortfolioDto> dtos = sectorPortfolioDtoService.search(new ArrayList<ApiSearchCriteria>(),
                new ServiceErrorsImpl());

        testSectorPortfolioDtos(dtos);
    }

    @Test
    public void testSearch_whenCriteriaProvided_thenCriteriaConvertToKeysCorrectly() {

        Mockito.when(
                sectorPortfolioIntegrationService.loadSectorPortfolios(Mockito.anyListOf(SectorPortfolioKey.class),
                        Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<List<SectorPortfolio>>() {

            @Override
            public List<SectorPortfolio> answer(InvocationOnMock invocation) throws Throwable {

                List<SectorPortfolioKey> keys = (List<SectorPortfolioKey>) invocation.getArguments()[0];
                assertEquals(2, keys.size());
                assertEquals(SectorPortfolioKey.valueOf("1010101"), keys.get(0));
                assertEquals(SectorPortfolioKey.valueOf("2020202"), keys.get(1));

                return new ArrayList<SectorPortfolio>();
            }
        });

        ApiSearchCriteria criteria = new ApiSearchCriteria("sectorPortfolioId", "1010101");
        ApiSearchCriteria criteria2 = new ApiSearchCriteria("sectorPortfolioId", "2020202");

        List<ApiSearchCriteria> searchCriteria = new ArrayList<>();
        searchCriteria.add(criteria);
        searchCriteria.add(criteria2);

        sectorPortfolioDtoService.search(searchCriteria, new ServiceErrorsImpl());
    }

    private void testSectorPortfolioDtos(List<SectorPortfolioDto> dtos) {

        assertNotNull(dtos);
        assertEquals(2, dtos.size());

        SectorPortfolioDto dto = dtos.get(0);

        assertEquals(sp1.getId(), dto.getKey().getSectorPortfolioId());
        assertEquals(sp1.getName(), dto.getName());
        assertEquals(sp1.getCode(), dto.getCode());
        assertEquals(sp1.getCategory(), dto.getCategory());
        assertEquals(sp1.getAssetClass(), dto.getAssetClass());
        assertEquals(sp1.getProductType(), dto.getProductType());
        assertEquals(sp1.getIpsCount(), dto.getIpsCount());
        assertEquals(sp1.getStatus(), dto.getStatus());
        assertEquals(sp1.getLastModifiedDate(), dto.getLastModifiedDate());
        assertEquals(sp1.getLastModifiedBy(), dto.getLastModifiedBy());
    }

    private void mockOtherServices() {

        Broker broker = new BrokerImpl(BrokerKey.valueOf("broker"), BrokerType.INVESTMENT_MANAGER);
        List<Broker> brokerList = Arrays.asList(broker);

        Mockito.when(brokerService.getBrokersForJob(Mockito.any(UserProfile.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(brokerList);

        Mockito.when(userProfileService.getActiveProfile()).thenReturn(Mockito.mock(UserProfile.class));
    }
}
