package com.bt.nextgen.api.ChessData.service;


import com.bt.nextgen.api.chesssponsor.controller.ChessSponsorController;
import com.bt.nextgen.api.chesssponsor.service.ChessSponsorDtoService;
import com.bt.nextgen.api.chesssponsor.model.ChessSponsorDataDto;
import com.bt.nextgen.api.chesssponsor.model.ChessSponsorDto;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.service.ServiceErrors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by l078480 on 29/06/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class ChessDataContorllerTest {


    @InjectMocks
    private ChessSponsorController chessSponsorController;
    @Mock
    private ChessSponsorDtoService chessSponsorDtoService;

    private   ChessSponsorDto chessSponsorDto;

    @Before
    public void setUp() throws Exception
    {
        chessSponsorDto =new ChessSponsorDto();
        List<ChessSponsorDataDto> chessSponsorDataDtoList=new ArrayList<>();
        ChessSponsorDataDto chessSponsor=new ChessSponsorDataDto();
        chessSponsor.setSponsorPid("04126");
        chessSponsor.setSponsorName("Wilson HTM Ltd");
        chessSponsorDataDtoList.add(chessSponsor);
        ChessSponsorDataDto chessSponsorSecond=new ChessSponsorDataDto();
        chessSponsorSecond.setSponsorPid("02338");
        chessSponsorSecond.setSponsorName("Ord Minnett Limited");
        chessSponsorDataDtoList.add(chessSponsorSecond);
        chessSponsorDto.setChessSponsorDataDtoList(chessSponsorDataDtoList);


    }

    @Test
    public final void testGeChessData() throws Exception
    {
        Mockito.when(chessSponsorDtoService.getChessSponsorData(Mockito.any(ServiceErrors.class))).thenReturn(chessSponsorDto);
        ApiResponse apiResponse = chessSponsorController.getChessSponsorData();
        assertThat(apiResponse, is(notNullValue()));
    }
}
