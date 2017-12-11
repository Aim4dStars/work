package com.bt.nextgen.service.avaloq;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

/**
 * Tests {@link DataLoader}.
 *
 * @author Albert Hirawan
 */
@RunWith(MockitoJUnitRunner.class)
public class DataLoaderTest {
    // common names
    private static final String NAME_APL = "APL";
    private static final String NAME_OE_HIERARCHY = "OE hierarchy";

    @Mock
    private DataInitialization dataInitialisation;

    @InjectMocks
    private DataLoader dataLoader;

    @Before
    public void init() {
        dataLoader.init(); // simulate PostConstruct
    }

    @Test
    public void getStaticDataLoaders() {
        // the returned list is expected to be sorted by name
        final List<StaticDataLoader> staticDataLoaders = dataLoader.getStaticDataLoaders();
        StaticDataLoader staticDataLoader;
        int index;
        index = 0;
        staticDataLoader = staticDataLoaders.get(index);
        assertThat(index + ". name", staticDataLoader.getName(), equalTo(NAME_APL));
        assertThat(index + ". description", staticDataLoader.getDescription(), equalTo("Approved Product List"));
        staticDataLoader.getLoaderTask().load();
        verify(dataInitialisation).loadApl();
        reset(dataInitialisation); // reset to avoid false check on already verified method call
        index++;
        staticDataLoader = staticDataLoaders.get(index);
        assertThat(index + ". name", staticDataLoader.getName(), equalTo("AssetDetails"));
        staticDataLoader.getLoaderTask().load();
        verify(dataInitialisation).loadGeneralAssets();
        reset(dataInitialisation); // reset to avoid false check on already verified method call
        index++;
        staticDataLoader = staticDataLoaders.get(index);
        assertThat(index + ". name", staticDataLoader.getName(), equalTo("Bank Date"));
        staticDataLoader.getLoaderTask().load();
        verify(dataInitialisation).loadBankDate();
        reset(dataInitialisation); // reset to avoid false check on already verified method call
        index++;
        staticDataLoader = staticDataLoaders.get(index);
        assertThat(index + ". name", staticDataLoader.getName(), equalTo("Broker Product AAL"));
        staticDataLoader.getLoaderTask().load();
        verify(dataInitialisation).loadBrokerProductAssets();
        reset(dataInitialisation); // reset to avoid false check on already verified method call
        index++;
        staticDataLoader = staticDataLoaders.get(index);
        assertThat(index + ". name", staticDataLoader.getName(), equalTo("Index AAL"));
        staticDataLoader.getLoaderTask().load();
        verify(dataInitialisation).loadAalIndexes();
        reset(dataInitialisation); // reset to avoid false check on already verified method call
        index++;
        staticDataLoader = staticDataLoaders.get(index);
        assertThat(index + ". name", staticDataLoader.getName(), equalTo("Index assets"));
        staticDataLoader.getLoaderTask().load();
        verify(dataInitialisation).loadIndexAssets();
        reset(dataInitialisation); // reset to avoid false check on already verified method call
        index++;
        staticDataLoader = staticDataLoaders.get(index);
        assertThat(index + ". name", staticDataLoader.getName(), equalTo(NAME_OE_HIERARCHY));
        staticDataLoader.getLoaderTask().load();
        verify(dataInitialisation).loadBrokers();
        reset(dataInitialisation); // reset to avoid false check on already verified method call
        index++;
        staticDataLoader = staticDataLoaders.get(index);
        assertThat(index + ". name", staticDataLoader.getName(), equalTo("Static Codes"));
        staticDataLoader.getLoaderTask().load();
        verify(dataInitialisation).loadAllStaticCodes();
        reset(dataInitialisation); // reset to avoid false check on already verified method call
        index++;
        staticDataLoader = staticDataLoaders.get(index);
        assertThat(index + ". name", staticDataLoader.getName(), equalTo("TD Asset rates"));
        staticDataLoader.getLoaderTask().load();
        verify(dataInitialisation).loadTermDepositAssetRates();
        reset(dataInitialisation); // reset to avoid false check on already verified method call
        index++;
        staticDataLoader = staticDataLoaders.get(index);
        assertThat(index + ". name", staticDataLoader.getName(), equalTo("TD Product rates"));
        staticDataLoader.getLoaderTask().load();
        verify(dataInitialisation).loadTermDepositProductRates();
        reset(dataInitialisation); // reset to avoid false check on already verified method call
        index++;
        staticDataLoader = staticDataLoaders.get(index);
        assertThat(index + ". name", staticDataLoader.getName(), equalTo("Transaction fees"));
        staticDataLoader.getLoaderTask().load();
        verify(dataInitialisation).loadTransactionFees();
        reset(dataInitialisation); // reset to avoid false check on already verified method call
    }

    @Test
    public void getStaticDataLoader() {
        StaticDataLoader staticDataLoader;
        String name;
        name = NAME_OE_HIERARCHY;
        staticDataLoader = dataLoader.getStaticDataLoader(name);
        assertThat(staticDataLoader.getName(), equalTo(name));
        name = NAME_APL;
        staticDataLoader = dataLoader.getStaticDataLoader(name);
        assertThat(staticDataLoader.getName(), equalTo(name));
    }
}
