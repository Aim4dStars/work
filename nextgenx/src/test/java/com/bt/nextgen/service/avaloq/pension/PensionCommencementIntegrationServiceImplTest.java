package com.bt.nextgen.service.avaloq.pension;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Field;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;


/**
 * Tests {@link PensionCommencementIntegrationServiceImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PensionCommencementIntegrationServiceImplTest {
    @Mock
    private AvaloqExecute avaloqExecute;

    @Mock
    private ServiceErrors serviceErrors;

    @InjectMocks
    private PensionCommencementIntegrationServiceImpl service;


    @Test(expected = IllegalArgumentException.class)
    public void isPensionCommencementPendingWithNullAccountNumber() {
        service.isPensionCommencementPending(null, serviceErrors);
    }


    @Test
    public void isPensionCommencementPending() throws Exception {
        isPensionCommencementPending("null pension commencement status", null, false);
        isPensionCommencementPending("no docId in pension commencement status", makePensionCommencementStatus(null), false);
        isPensionCommencementPending("non-null docId pension commencement status", makePensionCommencementStatus(999L), true);
    }


    public void isPensionCommencementPending(String infoStr, PensionCommencementStatusImpl response, boolean expected) {
        final boolean result;

        when(avaloqExecute.executeReportRequestToDomain(any(AvaloqReportRequest.class), eq(PensionCommencementStatusImpl.class),
                any(ServiceErrors.class))).thenReturn(response);

        result = service.isPensionCommencementPending("123", serviceErrors);
        assertThat(infoStr, result, Matchers.equalTo(expected));
    }


    private PensionCommencementStatusImpl makePensionCommencementStatus(Long docId) throws NoSuchFieldException, IllegalAccessException {
        final PensionCommencementStatusImpl retval = new PensionCommencementStatusImpl();
        final Field field = retval.getClass().getDeclaredField("docId");

        field.setAccessible(true);
        field.set(retval, docId);

        return retval;
    }
}
