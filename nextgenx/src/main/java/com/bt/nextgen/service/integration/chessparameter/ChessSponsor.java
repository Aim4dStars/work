package com.bt.nextgen.service.integration.chessparameter;

import java.io.Serializable;

/**
 * Created by l078480 on 21/06/2017.
 */
public interface ChessSponsor extends Serializable {

    String getSponsorName();

    String getSponsorPid();

    void setSponsorName(String sponsorName);

    void setSponsorPid(String sponsorPid);
}
