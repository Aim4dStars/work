package com.bt.nextgen.service.integration.financialdocument;

import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.user.UserKey;
import org.joda.time.DateTime;

public interface FinancialDocument extends BaseCmisDocument<FinancialDocumentKey> {
    AccountKey getAccountKey();

    BrokerKey getDealerGroupKey();

    UserKey getCustomerKey();

    FinancialDocumentType getDocumentType();

    DateTime getGenerationDate();

    String getExtensionType();

    String getGcmId();
}
