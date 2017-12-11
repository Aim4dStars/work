package com.bt.nextgen.api.movemoney.v2.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.movemoney.v2.model.BillerKey;
import com.bt.nextgen.api.movemoney.v2.model.BpayBillerDto;
import com.bt.nextgen.payments.domain.CRNType;
import com.bt.nextgen.payments.repository.BpayBiller;
import com.bt.nextgen.payments.repository.BpayBillerCodeRepository;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;

@RunWith(MockitoJUnitRunner.class)
public class BPAYBillerDtoServiceTest {

    @InjectMocks
    private BPayBillerDtoServiceImpl bpayBillerService;

    @Mock
    private BpayBillerCodeRepository bpayBillerCodeRepository;

    private List<BpayBiller> listBpayBillers = new ArrayList<BpayBiller>();

    @Before
    public void setUp() {

        BpayBiller bpayBiller1 = new BpayBiller();
        bpayBiller1.setCrnType(CRNType.CRN);
        bpayBiller1.setBillerCode("0000001008");
        bpayBiller1.setBillerName("MOLONG LTD");

        BpayBiller bpayBiller2 = new BpayBiller();
        bpayBiller2.setCrnType(CRNType.CRN);
        bpayBiller2.setBillerCode("0000001024");
        bpayBiller2.setBillerName("PARKES SVC");

        BpayBiller bpayBiller3 = new BpayBiller();
        bpayBiller3.setCrnType(CRNType.CRN);
        bpayBiller3.setBillerCode("0000001040");
        bpayBiller3.setBillerName("MOLONG PTY");

        listBpayBillers.add(bpayBiller1);
        listBpayBillers.add(bpayBiller2);
        listBpayBillers.add(bpayBiller3);
    }

    @Test
    public void testSearchBillerSuccess() {
        BpayBiller bpayBiller1 = new BpayBiller();
        bpayBiller1.setCrnType(CRNType.CRN);
        bpayBiller1.setBillerCode("0000001008");
        bpayBiller1.setBillerName("MOLONG LTD             ");
        Mockito.when(bpayBillerCodeRepository.load(Mockito.anyString())).thenReturn(bpayBiller1);
        BpayBillerDto response = bpayBillerService.validate(new BpayBillerDto(new BillerKey("0000001008")),
                new FailFastErrorsImpl());
        Assert.assertEquals(response.getBillerCode(), "0000001008");
        Assert.assertEquals(response.getBillerName(), "MOLONG LTD");
    }

    @Test
    public void testSearchBillerFail() {
        Mockito.when(bpayBillerCodeRepository.load(Mockito.anyString())).thenReturn(null);
        BpayBillerDto response = bpayBillerService.validate(new BpayBillerDto(new BillerKey("0000001008")),
                new FailFastErrorsImpl());
        Assert.assertEquals(response.getKey().getKey(), "0000001008");
        Assert.assertNull(response.getBillerCode());
        Assert.assertNull(response.getBillerName());
    }

    @Test
    public void testFindAllBillers() {
        Mockito.when(bpayBillerCodeRepository.loadAllBillers()).thenReturn(listBpayBillers);
        List<BpayBillerDto> response = bpayBillerService.findAll(new FailFastErrorsImpl());
        Assert.assertEquals(response.size(), 3);
        Assert.assertEquals(response.get(1).getBillerCode(), "0000001024");
        Assert.assertEquals(response.get(1).getBillerName(), "PARKES SVC");
    }
}
