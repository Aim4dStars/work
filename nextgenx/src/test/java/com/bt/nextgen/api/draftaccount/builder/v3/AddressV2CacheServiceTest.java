package com.bt.nextgen.api.draftaccount.builder.v3;

import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.gesb.locationmanagement.PostalAddress;
import com.bt.nextgen.service.gesb.locationmanagement.v1.LocationManagementIntegrationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by F058391 on 1/11/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddressV2CacheServiceTest {

    public static final String monikerId1 = "0GOAUEHwXgBwAAAAAIAgEAAAAA2C2PkBAhAQYQCCAAAAAAAAAAAP..ZAAAAAD.....AAAAAAAAAAAAAAAAADYwIG1hcnRpbiBwbGFjZQA-";
    public static final String monikerId2 = "0xOAUEHwXgBwAAAAAIAgEAAAAA2C0.EBAhAIIAAAAAAAAAAAD..2QAAAAA.....wAAAAAAAAAAAAAAAAA2MCBtYXJ0aW4gcGxhY2UA";
    @Mock
    private LocationManagementIntegrationService addressService;

    @InjectMocks
    AddressV2CacheService addressV2CacheService;

    @Before
    public void setUp(){
        when(addressService.retrievePostalAddress(anyString(), any(ServiceErrors.class))).thenReturn(getAddressResponse());
    }

    @Test
    public void getAddressShouldReturnAddressFromMap_whenSameAddressIsRequestedTwice (){
        PostalAddress postalAddress1 = addressV2CacheService.getAddress(monikerId1, new ServiceErrorsImpl());
        PostalAddress postalAddress2 = addressV2CacheService.getAddress(monikerId1, new ServiceErrorsImpl());
        verify(addressService, times(1)).retrievePostalAddress(anyString(), any(ServiceErrors.class));
        assertNotNull(postalAddress1);
        assertNotNull(postalAddress2);
    }

    @Test
    public void getAddressShouldReturnAddressFromServiceForTheFirstTime(){
        PostalAddress postalAddress =  addressV2CacheService.getAddress(monikerId2, new ServiceErrorsImpl());
        verify(addressService, times(1)).retrievePostalAddress(anyString(), any(ServiceErrors.class));
        assertNotNull(postalAddress);
    }

    private PostalAddress getAddressResponse() {
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setStreetName("Pitt");
        return postalAddress;
    }
}