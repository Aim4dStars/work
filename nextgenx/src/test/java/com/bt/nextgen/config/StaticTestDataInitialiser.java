package com.bt.nextgen.config;

import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.avaloq.asset.CacheManagedAssetIntegrationServiceImpl;
import com.bt.nextgen.service.avaloq.asset.aal.AalIndexIntegrationService;
import com.bt.nextgen.service.avaloq.asset.aal.BrokerProductAssetIntegrationService;
import com.bt.nextgen.service.avaloq.asset.aal.IndexAssetIntegrationService;
import com.bt.nextgen.service.avaloq.broker.CacheManagedPaginatedUserBrokerIntegrationServiceImpl;
import com.bt.nextgen.service.avaloq.broker.CacheManagedUserBrokerIntegrationServiceImpl;
import com.bt.nextgen.service.avaloq.code.CacheManagedStaticCodeIntegrationServiceImpl;
import com.bt.nextgen.service.avaloq.licenseadviserfee.CacheManagedAvaloqLicenseAdviserFeeIntegrationService;
import com.bt.nextgen.service.avaloq.product.CacheAvaloqProductIntegrationServiceImpl;
import com.bt.nextgen.service.avaloq.transactionfee.CacheManagedAvaloqTransactionFeeIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to minimise the number of times static data caches are populated when running multiple tests (and hence speed them
 * up).
 *
 * @author Albert Hirawan
 */
public class StaticTestDataInitialiser {
    private static final Logger log = LoggerFactory.getLogger(StaticTestDataInitialiser.class);

    // current initialisers.
    private static CacheManagedStaticCodeIntegrationServiceImpl staticCodeInitialiser = null;
    private static CacheManagedUserBrokerIntegrationServiceImpl userBrokerInitialiser = null;
    private static CacheManagedPaginatedUserBrokerIntegrationServiceImpl cachePaginatedBrokerInitialiser = null;
    private static CacheAvaloqProductIntegrationServiceImpl cacheAvaloqProductInitialiser = null;
    private static CacheManagedAssetIntegrationServiceImpl cacheManagedAssetIntegrationService = null;
    private static CacheManagedAvaloqTransactionFeeIntegrationService cacheManagedAvaloqTransactionFeeIntegrationService = null;
    private static BrokerProductAssetIntegrationService brokerProductAssetService = null;
    private static IndexAssetIntegrationService indexAssetIntegrationService = null;
    private static AalIndexIntegrationService aalIndexIntegrationService = null;
    private static CacheManagedAvaloqLicenseAdviserFeeIntegrationService cacheManagedAvaloqLicenseAdviserFeeIntegrationService=null;


    /**
     * Populate static code cache when the initialiser changes (eg when Spring re-wire the reference.
     *
     * @param latestInitialiser
     *            Latest initialiser for static code.
     */
    public static void init(CacheManagedStaticCodeIntegrationServiceImpl latestInitialiser) {
        if (latestInitialiser != staticCodeInitialiser) {
            latestInitialiser.populateCache(new FailFastErrorsImpl());
            staticCodeInitialiser = latestInitialiser;

        } else {
            log.debug("Reusing previously initialised StaticCodes (initialiser reference did not change)");
        }
    }

    /**
     * Populate OE hierarchy (aka broker user) cache when the initialiser changes (eg when Spring re-wire the reference.
     *
     * @param latestInitialiser
     *            Latest initialiser for OE hierarchy (aka broker user).
     */
    public static void init(CacheManagedUserBrokerIntegrationServiceImpl latestInitialiser) {
        if (latestInitialiser != userBrokerInitialiser) {
            latestInitialiser.populatebrokerCache(new FailFastErrorsImpl());
            userBrokerInitialiser = latestInitialiser;
        } else {
            log.debug("Reusing previously initialised UserBrokers (initialiser reference did not change)");
        }
    }
    
    /**
     * Populate paginated broker cache when the initialiser changes (eg when Spring re-wire the reference.
     *
     * @param latestInitialiser
     *            Latest initialiser for paginated broker.
     */
    public static void init(CacheManagedPaginatedUserBrokerIntegrationServiceImpl latestInitialiser) {
        if (latestInitialiser != cachePaginatedBrokerInitialiser) {
            latestInitialiser.populatePaginatedBrokerCache(new FailFastErrorsImpl());
            cachePaginatedBrokerInitialiser = latestInitialiser;
        } else {
            log.debug("Reusing previously initialised PaginatedBrokers (initialiser reference did not change)");
        }
    }

    /**
     * Populate Available Asset cache when the initialiser changes (eg when Spring re-wire the reference.
     *
     * @param latestInitialiser
     *            Latest initialiser for OE hierarchy (aka broker user).
     */
    public static void init(BrokerProductAssetIntegrationService latestInitialiser) {
        if (latestInitialiser != brokerProductAssetService) {
            latestInitialiser.initCache(new FailFastErrorsImpl());
            brokerProductAssetService = latestInitialiser;
        } else {
            log.debug("Reusing previously initialised Available assets reference did not change)");
        }
    }

    /**
     * Populate Available Asset cache when the initialiser changes (eg when Spring re-wire the reference.
     *
     * @param latestInitialiser
     *            Latest initialiser for OE hierarchy (aka broker user).
     */
    public static void init(IndexAssetIntegrationService latestInitialiser) {
        if (latestInitialiser != indexAssetIntegrationService) {
            latestInitialiser.initCache(new FailFastErrorsImpl());
            indexAssetIntegrationService = latestInitialiser;
        } else {
            log.debug("Reusing previously initialised Available assets reference did not change)");
        }
    }

    /**
     * Populate Available Asset cache when the initialiser changes (eg when Spring re-wire the reference.
     *
     * @param latestInitialiser
     *            Latest initialiser for OE hierarchy (aka broker user).
     */
    public static void init(AalIndexIntegrationService latestInitialiser) {
        if (latestInitialiser != aalIndexIntegrationService) {
            latestInitialiser.initCache(new FailFastErrorsImpl());
            aalIndexIntegrationService = latestInitialiser;
        } else {
            log.debug("Reusing previously initialised Available assets reference did not change)");
        }
    }


    /**
     * Populate APL cache when the initialiser changes (eg when Spring re-wire the reference.
     *
     * @param latestInitialiser
     *            Latest initialiser APL.
     */
    public static void init(CacheAvaloqProductIntegrationServiceImpl latestInitialiser) {
        if (latestInitialiser != cacheAvaloqProductInitialiser) {
            latestInitialiser.initializeAplCache(new FailFastErrorsImpl());
            cacheAvaloqProductInitialiser = latestInitialiser;
        } else {
            log.debug("Reusing previously initialised APL (initialiser reference did not change)");
        }
    }

    /**
     * Populate Asset cache when the initialiser changes (eg when Spring re-wire the reference.
     *
     * @param latestInitialiser
     *            Latest initialiser for OE hierarchy (aka broker user).
     */
    public static void init(CacheManagedAssetIntegrationServiceImpl latestInitialiser) {
        if (latestInitialiser != cacheManagedAssetIntegrationService) {
            latestInitialiser.populateAssetCache(new FailFastErrorsImpl());
            cacheManagedAssetIntegrationService = latestInitialiser;
        } else {
            log.debug("Reusing previously initialised Available assets reference did not change)");
        }
    }

    /**
     * Populate transaction fees when the initialiser changes (eg when Spring re-wire the reference.
     *
     * @param latestInitialiser
     *            Latest initialiser for OE hierarchy (aka broker user).
     */
    public static void init(CacheManagedAvaloqTransactionFeeIntegrationService latestInitialiser) {
        if (latestInitialiser != cacheManagedAvaloqTransactionFeeIntegrationService) {
            latestInitialiser.initCache(new FailFastErrorsImpl());
            cacheManagedAvaloqTransactionFeeIntegrationService = latestInitialiser;
        } else {
            log.debug("Reusing previously initialised transaction fees reference did not change)");
        }
    }


    public static void init(CacheManagedAvaloqLicenseAdviserFeeIntegrationService latestInitialiser) {
        if (latestInitialiser != cacheManagedAvaloqLicenseAdviserFeeIntegrationService) {
            latestInitialiser.initCache(new FailFastErrorsImpl());
            cacheManagedAvaloqLicenseAdviserFeeIntegrationService = latestInitialiser;
        } else {
            log.debug("Reusing previously initialised transaction fees reference did not change)");
        }
    }
}
