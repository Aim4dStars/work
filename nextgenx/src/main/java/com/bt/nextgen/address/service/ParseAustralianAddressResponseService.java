package com.bt.nextgen.address.service;

import com.bt.nextgen.core.web.model.Address;
import ns.btfin_com.party.partyservice.partyreply.v2_1.ParseAustralianAddressResponseMsgType;

public interface ParseAustralianAddressResponseService {

    Address getAddressFromParseAustralianAddressResponse(ParseAustralianAddressResponseMsgType response);

    boolean validateSuburbAndPostCode(ParseAustralianAddressResponseMsgType addressResponseMsgType, String suburb, String postcode);
}
