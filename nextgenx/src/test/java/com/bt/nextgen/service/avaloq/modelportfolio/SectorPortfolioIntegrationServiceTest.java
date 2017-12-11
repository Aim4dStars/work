package com.bt.nextgen.service.avaloq.modelportfolio;

import com.avaloq.abs.bb.fld_def.IdFld;
import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.modelportfolio.sectorportfolio.SectorPortfolio;
import com.bt.nextgen.service.avaloq.modelportfolio.sectorportfolio.SectorPortfolioIntegrationServiceImpl;
import com.bt.nextgen.service.avaloq.modelportfolio.sectorportfolio.SectorPortfolioKey;
import com.bt.nextgen.service.avaloq.modelportfolio.sectorportfolio.SectorPortfolioResponse;
import com.bt.nextgen.service.avaloq.modelportfolio.sectorportfolio.SectorPortfolioResponseImpl;
import com.bt.nextgen.service.avaloq.modelportfolio.sectorportfolio.SectorPortfolioTemplate;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.request.AvaloqReportRequestImpl;
import com.bt.nextgen.service.request.AvaloqRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SectorPortfolioIntegrationServiceTest {

    @InjectMocks
    private SectorPortfolioIntegrationServiceImpl sectorPortfolioIntegrationService;

    @Mock
    private AvaloqReportService avaloqService;

    private ServiceErrors serviceErrors;

    @Test
    public void whenSectorPortfoliosLoadRequested_thenCorrectParametersArePassedToTheService() throws Exception {

        final SectorPortfolioResponse mockResponse = Mockito.mock(SectorPortfolioResponseImpl.class);

        serviceErrors = new ServiceErrorsImpl();

        when(avaloqService.executeReportRequestToDomain(any(AvaloqRequest.class), any(Class.class), any(ServiceErrors.class)))
                .thenAnswer(new Answer<SectorPortfolioResponse>() {

                    @Override
                    public SectorPortfolioResponse answer(InvocationOnMock invocation) throws Throwable {

                        AvaloqReportRequestImpl req = (AvaloqReportRequestImpl) invocation.getArguments()[0];

                        assertEquals(SectorPortfolioTemplate.SECTOR_PORTFOLIOS_FOR_IM, req.getTemplate());
                        assertEquals("key",
                                ((IdFld) req.getRequestObject().getTask().getParamList().getParam().get(0).getVal()).getVal());

                        return mockResponse;
                    }
                });

        List<SectorPortfolio> responseList = sectorPortfolioIntegrationService.loadSectorPortfoliosForManager(
                BrokerKey.valueOf("key"), serviceErrors);

        assertEquals(mockResponse.getSectorPortfolios(), responseList);
    }

    @Test
    public void whenSectorPortfoliosListRequested_thenCorrectParametersArePassedToTheService() throws Exception {

        final SectorPortfolioResponse mockResponse = Mockito.mock(SectorPortfolioResponseImpl.class);

        serviceErrors = new ServiceErrorsImpl();

        when(avaloqService.executeReportRequestToDomain(any(AvaloqRequest.class), any(Class.class), any(ServiceErrors.class)))
                .thenAnswer(new Answer<SectorPortfolioResponse>() {

                    @Override
                    public SectorPortfolioResponse answer(InvocationOnMock invocation) throws Throwable {

                        AvaloqReportRequestImpl req = (AvaloqReportRequestImpl) invocation.getArguments()[0];

                        assertEquals(SectorPortfolioTemplate.SECTOR_PORTFOLIOS, req.getTemplate());
                        assertEquals("1010101", ((IdFld) req.getRequestObject().getTask().getParamList().getParam().get(0)
                                .getValList().getVal().get(0)).getVal());
                        assertEquals("2020202", ((IdFld) req.getRequestObject().getTask().getParamList().getParam().get(0)
                                .getValList().getVal().get(1)).getVal());

                        return mockResponse;
                    }
                });

        List<SectorPortfolioKey> keyList = Arrays.asList(SectorPortfolioKey.valueOf("1010101"),
                SectorPortfolioKey.valueOf("2020202"));
        List<SectorPortfolio> responseList = sectorPortfolioIntegrationService.loadSectorPortfolios(keyList, serviceErrors);

        assertEquals(mockResponse.getSectorPortfolios(), responseList);
    }
}
