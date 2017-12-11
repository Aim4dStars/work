package com.bt.nextgen.service.integration.chessparameter;

import java.io.Serializable;
import java.util.List;


/**
 * Created by l078480 on 16/06/2017.
 */
public interface ChessSponsorService extends Serializable {

    List<ChessSponsor> getChessSponsor();

    void setChessSponsor(List<ChessSponsor> chessSponsor);
}
