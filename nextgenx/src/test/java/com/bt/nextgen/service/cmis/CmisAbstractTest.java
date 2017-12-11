package com.bt.nextgen.service.cmis;

import com.bt.nextgen.clients.util.JaxbUtil;
import org.junit.BeforeClass;
import org.oasis_open.docs.ns.cmis.core._200908.CmisObjectType;
import org.oasis_open.docs.ns.cmis.core._200908.CmisProperty;
import org.oasis_open.docs.ns.cmis.messaging._200908.QueryResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by L062329 on 21/07/2015.
 */
public class CmisAbstractTest {

    protected static Map<String, CmisProperty> map = new HashMap<>();
    protected static QueryResponse response;

    @BeforeClass
    public static void setup() {
        response = JaxbUtil.unmarshall("/webservices/response/CMISQuerySingleValidAccount_UT.xml",
                QueryResponse.class);
        for (CmisObjectType cmisObject : response.getObjects().getObjects()) {
            CmisDocumentImpl cmisDocument = new CmisDocumentImpl();
            for (CmisProperty property : cmisObject.getProperties().getProperty()) {
                map.put(property.getPropertyDefinitionId(), property);
            }
        }
    }
}
