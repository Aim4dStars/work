package com.bt.nextgen.core.jms.cacheinvalidation;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.core.jms.data.Chunk;
import com.bt.nextgen.core.jms.data.ChunkImpl;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Deepshikha Singh on 25/02/2015.
 */
public class CacheInvalidationStrategyIntegrationTest extends BaseSecureIntegrationTest {

    @Autowired
    CacheInvalidationStrategy cacheInvalidationStrategy;

    @Test
    public void test_resolveStrategy_static_codes() throws Exception{
        String templateName = "BTFG$UI_CODE_LIST.ALL#UI";

        TemplateBasedInvalidationStrategy strategy = cacheInvalidationStrategy.resolveStrategy(templateName);

        assertNotNull(strategy);
        assert(strategy instanceof StaticCodesTemplateInvalidationExecutor);
    }

    @Test
    public void test_resolveStrategy_broker_hierarchy() throws Exception{
        String templateName = "BTFG$UI_OE_STRUCT.ALL#PERSON#HIRA";

        TemplateBasedInvalidationStrategy strategy = cacheInvalidationStrategy.resolveStrategy(templateName);

        assertNotNull(strategy);
        assert(strategy instanceof BrokerTemplateInvalidationExecutor);

    }

    @Test(expected = NoSuchBeanDefinitionException.class)
    public void test_resolveStrategy_exception() throws Exception{
        String templateName = "NO#MATCHING#BEAN#EXIST";

        TemplateBasedInvalidationStrategy strategy = cacheInvalidationStrategy.resolveStrategy(templateName);

        assertNotNull(strategy);
        assert(strategy instanceof BrokerTemplateInvalidationExecutor);

    }

    @Test
    public void test_cacheInvalidationStrategy() throws Exception{
        String templateName = "BTFG$UI_OE_STRUCT.ALL#PERSON#HIRA";
        Chunk chunk = new ChunkImpl();
        chunk.add(createMessageForTemplateName(templateName));

        cacheInvalidationStrategy.execute(chunk);
    }

    @Test
    public void test_resolveStrategy_unread_notifications() throws Exception{
        String templateName = "BTFG$UI_NTFCN_LIST.USER#UNREAD_CNT";

        TemplateBasedInvalidationStrategy strategy = cacheInvalidationStrategy.resolveStrategy(templateName);

        assertNotNull(strategy);
        assert(strategy instanceof NotificationUnReadCountInvalidationExecutor);

    }

    @Test
    public void test_resolveStratergy_term_deposit_asset_rates(){

        String templateName = "BTFG$UI_FIDD_RATE_ASSET.IRC_LIST";
        TemplateBasedInvalidationStrategy templateBasedInvalidationStrategy = cacheInvalidationStrategy.resolveStrategy(templateName);
        assertNotNull(templateBasedInvalidationStrategy);
        assertTrue(templateBasedInvalidationStrategy instanceof TermDepositAssetRateTemplateInvalidationExecutor);
    }

    @Test
    public void test_resolveStratergy_term_deposit_product_rates(){

        String templateName = "BTFG$UI_FIDD_RATE_PROD.IRC_LIST";
        TemplateBasedInvalidationStrategy templateBasedInvalidationStrategy = cacheInvalidationStrategy.resolveStrategy(templateName);
        assertNotNull(templateBasedInvalidationStrategy);
        assertTrue(templateBasedInvalidationStrategy instanceof TermDepositProductRateTemplateInvalidationExecutor);
    }

    public Message createMessageForTemplateName(String templateName) throws Exception {
        Message message = mock(Message.class);
        when(message.getStringProperty(anyString())).thenReturn(templateName);
        return message;
    }
}