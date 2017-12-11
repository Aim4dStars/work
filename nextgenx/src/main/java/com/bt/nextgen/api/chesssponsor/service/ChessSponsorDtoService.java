package com.bt.nextgen.api.chesssponsor.service;

import com.bt.nextgen.api.chesssponsor.model.ChessSponsorDto;
import com.bt.nextgen.service.ServiceErrors;

/**
 * Created by L078480 on 22/06/2017.
 */
public interface ChessSponsorDtoService {

    ChessSponsorDto getChessSponsorData(ServiceErrors serviceErrors);
}
