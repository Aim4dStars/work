package com.bt.nextgen.api.draftaccount.builder.v3;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.gesb.locationmanagement.PostalAddress;
import com.bt.nextgen.service.gesb.locationmanagement.v1.LocationManagementIntegrationService;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by F058391 on 1/11/2016.
 */

// This class used to cache the address retrieved from GESB.
// In case an application contains multiple address with same moniker id, we do not want to make multiple GESB calls.
@Service
public class AddressV2CacheService {

    private Logger logger = getLogger(AddressV2CacheService.class);

    private final ThreadLocal<HashedMap> localAdressCache = new ThreadLocal<HashedMap>() {
        protected HashedMap initialValue() {
            return new HashedMap();
        }
    };

    @Autowired
    private LocationManagementIntegrationService addressService;

    public PostalAddress getAddress(String addressIdentifier, ServiceErrors serviceErrors) {
        logger.info("AddressV2 identifier", addressIdentifier);
        if (localAdressCache.get().get(addressIdentifier) == null) {
            logger.info("Fetching address from GESB", addressIdentifier);
            PostalAddress addressResponse = addressService.retrievePostalAddress(addressIdentifier, serviceErrors);
            localAdressCache.get().put(addressIdentifier, addressResponse);
        }
        return (PostalAddress) localAdressCache.get().get(addressIdentifier);
    }

    public void clearMap() {
        localAdressCache.remove();
    }
}
