package com.bt.nextgen.core.jms.cacheinvalidation.executor;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.core.jms.cacheinvalidation.ExecutorUtils;
import com.bt.nextgen.core.jms.cacheinvalidation.InvalidationNotification;
import com.bt.nextgen.core.jms.cacheinvalidation.InvalidationNotificationImpl;
import com.bt.nextgen.core.jms.cacheinvalidation.command.BrokerInitializationCommand;
import com.bt.nextgen.core.jms.cacheinvalidation.command.Command;
import com.bt.nextgen.core.jms.cacheinvalidation.command.CommandBuilder;
import com.bt.nextgen.core.jms.cacheinvalidation.command.TermDepositAssetRateInitializationCommand;
import com.bt.nextgen.core.jms.cacheinvalidation.command.TermDepositProductRateInitializationCommand;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import org.apache.commons.lang.StringUtils;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.junit.Assert.assertNotNull;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by L054821 on 3/03/2015.
 */
public class ExecutorUtilsTest extends BaseSecureIntegrationTest {

    @Autowired
    @Qualifier("commandBuilderFactory")
    private CommandBuilder commandBuilder;

    InvalidationNotification investorInvalidationNotification;

    @Test
    public void test_resolveCommand_With_Param_Partial_Inv_Disabled() throws Exception{
        String templateName = "BTFG$UI_OE_STRUCT.ALL#PERSON#HIRA";

        Command command = ExecutorUtils.resolveCommand(commandBuilder, createNotification(templateName));
        assert(command instanceof BrokerInitializationCommand);
    }

    @Test
    public void test_resolveCommand_Without_Param() throws Exception{
        String templateName = "BTFG$UI_OE_STRUCT.ALL#PERSON#HIRA";

        InvalidationNotificationImpl notification = createNotification(templateName);
        notification.setParamName("");
        Command command = ExecutorUtils.resolveCommand(commandBuilder, notification);

        assertNotNull(command);
        assert(command instanceof BrokerInitializationCommand);
    }

    @Test
    public void test_resolveCommand_for_TDAssetRates() throws Exception {
        InvalidationNotification invalidationNotification = createInvalidationMessage("TermDepositAssetRate_Invalidation.xml");
        Command assetRateCommand = ExecutorUtils.resolveCommand(commandBuilder,invalidationNotification);
        assertNotNull(assetRateCommand);
        assertTrue(assetRateCommand instanceof TermDepositAssetRateInitializationCommand);
    }

    @Test
    public void test_resolveCommand_for_TDProductRates() throws Exception {
        InvalidationNotification invalidationNotification = createInvalidationMessage("TermDepositProductRate_Invalidation.xml");
        Command productRateCommand = ExecutorUtils.resolveCommand(commandBuilder,invalidationNotification);
        assertNotNull(productRateCommand);
        assertTrue(productRateCommand instanceof TermDepositProductRateInitializationCommand);
    }

    private  InvalidationNotificationImpl createNotification(String templateName){
        InvalidationNotificationImpl notification = new InvalidationNotificationImpl();
        notification.setParamName("oe_start_id");
        notification.setTemplateName(templateName);
        notification.setCacheName(templateName);

        return notification;
    }

    private InvalidationNotification createInvalidationMessage(String fileName) throws Exception {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("webservices/response/"+fileName);
        String content = FileCopyUtils.copyToString(new InputStreamReader(in));
        if(StringUtils.isNotBlank(content)){
            DefaultResponseExtractor defaultResponseExtractor = new DefaultResponseExtractor(InvalidationNotificationImpl.class);
            investorInvalidationNotification = (InvalidationNotification)defaultResponseExtractor.extractData(content);
            return investorInvalidationNotification;
        }
        return null;
    }

}