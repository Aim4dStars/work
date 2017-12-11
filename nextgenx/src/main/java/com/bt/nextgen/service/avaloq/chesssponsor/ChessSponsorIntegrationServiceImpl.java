package com.bt.nextgen.service.avaloq.chesssponsor;

import com.bt.nextgen.core.cache.CacheType;
import com.bt.nextgen.core.cache.GenericCache;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.Constants;
import com.bt.nextgen.service.integration.chessparameter.ChessSponsorIntegrationService;
import com.bt.nextgen.service.integration.chessparameter.ChessSponsorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by l078480 on 21/06/2017.
 */
@Service
public class ChessSponsorIntegrationServiceImpl implements ChessSponsorIntegrationService {

    @Autowired
    private GenericCache cache;

    @Autowired

    private ChessSponsorValueGetterImpl chessSponsorValueGetter;

    @Override
    public ChessSponsorService getChessSponsorData(ServiceErrors serviceErrors){
        ChessSponsorService chessSponsorService = (ChessSponsorService)cache.get(CacheType.CHESS_SPONSOR, Constants.BANKDATE, chessSponsorValueGetter);

        return chessSponsorService;
    }
}
