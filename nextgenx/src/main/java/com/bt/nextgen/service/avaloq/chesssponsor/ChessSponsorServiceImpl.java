package com.bt.nextgen.service.avaloq.chesssponsor;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.integration.chessparameter.ChessSponsor;
import com.bt.nextgen.service.integration.chessparameter.ChessSponsorService;

import java.util.List;

/**
 * Created by l078480 on 21/06/2017.
 */
@ServiceBean(xpath = "/", type = ServiceBeanType.CONCRETE)
public class ChessSponsorServiceImpl extends AvaloqBaseResponseImpl implements ChessSponsorService {

    @ServiceElementList(xpath = "//data/bp_list/bp", type = ChessSponsorImpl.class)
    private List<ChessSponsor> chessSponsor;

    @Override
    public void setChessSponsor(List<ChessSponsor> chessSponsor) {
        this.chessSponsor = chessSponsor;
    }

    @Override
    public List<ChessSponsor> getChessSponsor() {
        return this.chessSponsor;
    }

}
