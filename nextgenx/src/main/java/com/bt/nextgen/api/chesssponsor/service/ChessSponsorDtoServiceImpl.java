package com.bt.nextgen.api.chesssponsor.service;

import com.bt.nextgen.api.chesssponsor.model.ChessSponsorDataDto;
import com.bt.nextgen.api.chesssponsor.model.ChessSponsorDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.chessparameter.ChessSponsor;
import com.bt.nextgen.service.integration.chessparameter.ChessSponsorIntegrationService;
import com.bt.nextgen.service.integration.chessparameter.ChessSponsorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by L078480 on 23/06/2017.
 */
@Service
public class ChessSponsorDtoServiceImpl implements ChessSponsorDtoService {

    @Autowired
    private ChessSponsorIntegrationService chessSponsorIntegrationService;

    @Override
    public ChessSponsorDto getChessSponsorData(ServiceErrors serviceErrors) {
        return toChessSponsorDto(chessSponsorIntegrationService.getChessSponsorData(serviceErrors));
    }

    private ChessSponsorDto toChessSponsorDto(ChessSponsorService chessSponsorService) {
        ChessSponsorDto chessSponsorDto = new ChessSponsorDto();
        List<ChessSponsorDataDto> chessSponsorDataDtos = new ArrayList<>();
        if (null != chessSponsorService && !chessSponsorService.getChessSponsor().isEmpty()) {
            for (ChessSponsor chessSponsor : chessSponsorService.getChessSponsor()) {
                ChessSponsorDataDto chessSponsorDataDto = new ChessSponsorDataDto();
                chessSponsorDataDto.setSponsorName(chessSponsor.getSponsorName().concat(" ").concat("-").concat(" ").concat(chessSponsor.getSponsorPid()));
                chessSponsorDataDto.setSponsorPid(chessSponsor.getSponsorPid());
                chessSponsorDataDtos.add(chessSponsorDataDto);

            }
        }
        chessSponsorDto.setChessSponsorDataDtoList(chessSponsorDataDtos);
        return chessSponsorDto;
    }

}
