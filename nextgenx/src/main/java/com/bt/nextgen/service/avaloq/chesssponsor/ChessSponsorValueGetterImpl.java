package com.bt.nextgen.service.avaloq.chesssponsor;

/**
 * Created by l078480 on 21/06/2017.
 */
import com.bt.nextgen.core.cache.ValueGetter;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.integration.chessparameter.ChessSponsorService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ChessSponsorValueGetterImpl implements ValueGetter {

    private static final Logger logger = LoggerFactory.getLogger(com.bt.nextgen.service.avaloq.chesssponsor.ChessSponsorValueGetterImpl.class);

    @Autowired
    private AvaloqExecute avaloqExecute;

    private AvaloqReportRequest reportRequest;

    @Override
    public Object getValue(Object key) {
        logger.info("Inside getValue method");

        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        reportRequest = new AvaloqReportRequest(Template.CHESS_PARAMETER.getName()).asApplicationUser();

        ChessSponsorService chessresponse = avaloqExecute.executeReportRequestToDomain(reportRequest,
                ChessSponsorServiceImpl.class,
                serviceErrors);
        return chessresponse;
    }
}
