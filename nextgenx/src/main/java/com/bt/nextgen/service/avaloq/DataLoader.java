package com.bt.nextgen.service.avaloq;

import com.bt.nextgen.core.cache.CacheType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Loader for static data.
 * 
 * @author Albert Hirawan
 */
@Component
public class DataLoader {
    /** Loaders for static data. */
    private Map<String, StaticDataLoader> staticDataLoaders;

    /** Initialiser for static data. */
    @Autowired
    private DataInitialization dataInitialisation;

    @PostConstruct
    public void init() {
        initStaticDataLoaders();
    }

    /**
     * Initialie Static data loaders.
     */
    private void initStaticDataLoaders() {
        final List<StaticDataLoader> loaders = new ArrayList<>();
        loaders.add(new StaticDataLoader(
                "Static Codes",
                "Static Codes reference data.<br/>If JMS is enabled, reloading this will trigger loading of other static data that depends on Static Code",
                CacheType.STATIC_CODE_CACHE, new StaticDataLoader.LoaderTask() {
                    @Override
                    public void load() {
                        dataInitialisation.loadAllStaticCodes();
                    }
                }));
		loaders.add(new StaticDataLoader("OE hierarchy", "Contains broker references",
                CacheType.JOB_USER_BROKER_CACHE,
				new StaticDataLoader.LoaderTask() {
					@Override
					public void load() {
					    dataInitialisation.loadBrokers();
					}
				}));
        loaders.add(new StaticDataLoader("AssetDetails", "General Asset List", CacheType.ASSET_DETAILS,
                new StaticDataLoader.LoaderTask() {
                    @Override
                    public void load() {
                        dataInitialisation.loadGeneralAssets();
                    }
                }));
        loaders.add(new StaticDataLoader("APL", "Approved Product List", CacheType.ADVISER_PRODUCT_LIST_CACHE,
                new StaticDataLoader.LoaderTask() {
                    @Override
                    public void load() {
                        dataInitialisation.loadApl();
                    }
                }));
        loaders.add(new StaticDataLoader("TD Asset rates", "Term deposit rates", CacheType.TERM_DEPOSIT_ASSET_RATE_CACHE,
                new StaticDataLoader.LoaderTask() {
                    @Override
                    public void load() {
                        dataInitialisation.loadTermDepositAssetRates();
                    }
                }));
        loaders.add(new StaticDataLoader("TD Product rates", "Term deposit rates", CacheType.TERM_DEPOSIT_PRODUCT_RATE_CACHE,
                new StaticDataLoader.LoaderTask() {
                    @Override
                    public void load() {
                        dataInitialisation.loadTermDepositProductRates();
                    }
                }));
        loaders.add(new StaticDataLoader("Bank Date", "", CacheType.BANK_DATE, new StaticDataLoader.LoaderTask() {
            @Override
            public void load() {
                dataInitialisation.loadBankDate();
            }
        }));
        loaders.add(new StaticDataLoader("Transaction fees",
                "Transaction fees that are applied to orders when calculating the consideration.", CacheType.TRANSACTION_FEES,
                new StaticDataLoader.LoaderTask() {
                    @Override
                    public void load() {
                        dataInitialisation.loadTransactionFees();
                    }
                }));
        loaders.add(new StaticDataLoader("Broker Product AAL", "The aal list for a broker product.",
                CacheType.AVAILABLE_ASSET_LIST_BROKER_CACHE, new StaticDataLoader.LoaderTask() {
                    @Override
                    public void load() {
                        dataInitialisation.loadBrokerProductAssets();
                    }
                }));
       loaders.add(new StaticDataLoader("Index AAL", "The indexes to include in each aal.",
                CacheType.AVAILABLE_ASSET_LIST_INDEX_CACHE, new StaticDataLoader.LoaderTask() {
                    @Override
                    public void load() {
                        dataInitialisation.loadAalIndexes();
                    }
                }));
        loaders.add(new StaticDataLoader("Index assets", "The list of assets in each index", CacheType.INDEX_ASSET_CACHE,
                new StaticDataLoader.LoaderTask() {
                    @Override
                    public void load() {
                        dataInitialisation.loadIndexAssets();
                    }
                }));
        // put loaders in map in sorted order
        Collections.sort(loaders);
        staticDataLoaders = new LinkedHashMap<>();
        for (StaticDataLoader loader : loaders) {
            staticDataLoaders.put(loader.getName(), loader);
        }
    }

    /**
     * Get list of static data loaders.
     * 
     * @return List of static data orders (sorted by name).
     */
    public List<StaticDataLoader> getStaticDataLoaders() {
        final List<StaticDataLoader> retval = new ArrayList<>();

        retval.addAll(staticDataLoaders.values());

        return retval;
    }

    /**
     * Get static data loader instance for a specified name.
     * 
     * @param name
     *            Name of static data.
     * 
     * @return Static data loader instance for a specified name.
     */
    public StaticDataLoader getStaticDataLoader(String name) {
        return staticDataLoaders.get(name);
    }
}
