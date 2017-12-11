package com.bt.nextgen.serviceops.repository;


import com.bt.nextgen.config.BaseSecureIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Created by l069679 on 14/02/2017.
 */

public class GcmAuditRepositoryImplTest extends BaseSecureIntegrationTest {

    @Autowired
    GcmAuditRepository gcmAuditRepository;

    @Test
    public void testLogAuditEntry() {
        String userId ="CS052634";
        String reqType = "gesb-retrieveCustomerDataV10";
        String reqMsg = "{status: 'success',data: {}}";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("silo", "WPAC");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        gcmAuditRepository.logAuditEntry(userId,reqType,reqMsg);
    }
}
