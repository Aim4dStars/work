package com.bt.nextgen.core.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.bt.nextgen.config.BaseSecureIntegrationTest;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class WestpacProductsVersionRepositoryTest extends BaseSecureIntegrationTest {

    @Autowired
    WestpacProductsRepositoryImpl westpacProductsRepository;

    @Test
    public void testLoadWestpacProduct_success() throws Exception {
        WestpacProduct result = westpacProductsRepository.load("13d46777ec304eadb673f30ed0487f99");
        assertThat(result.getCanonicalProductCode(), is("13d46777ec304eadb673f30ed0487f99"));
        assertThat(result.getName(), is("Westpac Choice"));
        assertThat(result.getCategory(), is("Consumer Transaction Accounts"));
        assertThat(result.getCategoryProductSystem(), is("CHOICE"));
        assertThat(result.getFundsTransferFrom(), is("Y"));
    }

    @Test
    public void testLoadWestpacProduct_noResult() throws Exception {
        WestpacProduct result = westpacProductsRepository.load("123456789");
        assertNull(result);
    }

    @Test
    public void testLoadWestpacProduct_noCpc() throws Exception {
        WestpacProduct result = westpacProductsRepository.load(null);
        assertNull(result);
    }
}
