package com.bt.nextgen.service.integration.financialdocument;

import org.joda.time.DateTime;

import java.math.BigInteger;

public interface BaseCmisDocument<K> {

    K getDocumentKey();

    String getDocumentName();

    String getDocumentTitleCode();

    BigInteger getSize();

    DateTime getPeriodEndDate();

    DateTime getPeriodStartDate();
}
