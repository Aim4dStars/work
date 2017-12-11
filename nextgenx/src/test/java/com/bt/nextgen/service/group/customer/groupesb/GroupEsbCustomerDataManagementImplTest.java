package com.bt.nextgen.service.group.customer.groupesb;

import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.CustomerDataManagementIntegrationService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by F058391 on 18/01/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class GroupEsbCustomerDataManagementImplTest {

    @Mock
    private CustomerDataManagementIntegrationService groupEsbCustomerDataManagementV11Impl;

    @Mock
    private FeatureTogglesService togglesService;

    @InjectMocks
    GroupEsbCustomerDataManagementImpl groupEsbCustomerDataManagement;

    FeatureToggles featureToggles;

    @Before
    public void setUp() {
        featureToggles = mock(FeatureToggles.class);
        when(togglesService.findOne(any(FailFastErrorsImpl.class))).thenReturn(featureToggles);
    }

    @Test
    public void retrieveCustomerInformation_V11() {
        when(featureToggles.getFeatureToggle(eq("svc0258v11.enabled"))).thenReturn(true);
        groupEsbCustomerDataManagement.retrieveCustomerInformation(new CustomerManagementRequestImpl(), new ArrayList<String>(), new ServiceErrorsImpl());

        verify(groupEsbCustomerDataManagementV11Impl, times(1)).retrieveCustomerInformation(any(CustomerManagementRequest.class), anyListOf(String.class), any(ServiceErrors.class));
    }
}