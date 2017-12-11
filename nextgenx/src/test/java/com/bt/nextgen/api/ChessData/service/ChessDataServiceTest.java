package com.bt.nextgen.api.ChessData.service;

import com.bt.nextgen.api.chesssponsor.service.ChessSponsorDtoServiceImpl;
import com.bt.nextgen.api.chesssponsor.model.ChessSponsorDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.chesssponsor.ChessSponsorImpl;
import com.bt.nextgen.service.avaloq.chesssponsor.ChessSponsorServiceImpl;
import com.bt.nextgen.service.integration.chessparameter.ChessSponsor;
import com.bt.nextgen.service.integration.chessparameter.ChessSponsorIntegrationService;
import com.bt.nextgen.service.integration.chessparameter.ChessSponsorService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

/**
 * Created by l078480 on 27/06/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class ChessDataServiceTest {


    @Mock
    ChessSponsorIntegrationService chessSponsorIntegrationService;

    @InjectMocks
    ChessSponsorDtoServiceImpl chessSponsorDtoService;

    private List<ChessSponsor> chessSponsorList;
    private ChessSponsorService chessSponsorService;


    @Before
    public void setUp() {
     chessSponsorList =new ArrayList<ChessSponsor>();
     chessSponsorService =new ChessSponsorServiceImpl();

     ChessSponsor chessSponsor=new ChessSponsorImpl();
        chessSponsor.setSponsorPid("04126");
        chessSponsor.setSponsorName("Wilson HTM Ltd");
        chessSponsorList.add(chessSponsor);
        ChessSponsor chessSponsorSecond=new ChessSponsorImpl();
        chessSponsorSecond.setSponsorPid("02338");
        chessSponsorSecond.setSponsorName("Ord Minnett Limited");
        chessSponsorList.add(chessSponsorSecond);
        chessSponsorService.setChessSponsor(chessSponsorList);
    }

    @Test
    public void testChessSponsorData() {
        Mockito.when(chessSponsorIntegrationService.getChessSponsorData( Mockito.any(ServiceErrors.class))).thenReturn(chessSponsorService);
        ChessSponsorDto chessSponsorDto=chessSponsorDtoService.getChessSponsorData(new FailFastErrorsImpl());
        assertThat(chessSponsorDto.getChessSponsorDataDtoList().size(), equalTo(2));
        assertEquals("Wilson HTM Ltd - 04126",chessSponsorDto.getChessSponsorDataDtoList().get(0).getSponsorName());

    }

}
