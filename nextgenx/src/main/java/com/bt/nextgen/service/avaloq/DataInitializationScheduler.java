package com.bt.nextgen.service.avaloq;

import com.bt.nextgen.service.avaloq.asset.AssetEnumTemplate;
import com.bt.nextgen.service.avaloq.broker.BrokerEnumTemplate;
import com.bt.nextgen.service.avaloq.licenseadviserfee.CacheManagedAvaloqLicenseAdviserFeeIntegrationService;
import com.bt.nextgen.service.avaloq.product.AplEnumTemplate;
import com.bt.nextgen.service.avaloq.transactionfee.CacheManagedAvaloqTransactionFeeIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Created by L054821 on 10/12/2014. This is a scheduler class which will schedule the reloading of data initialization services.
 */
@EnableScheduling
@Configuration
public class DataInitializationScheduler extends AbstractAvaloqIntegrationService {

    private static Logger logger = LoggerFactory.getLogger(DataInitializationScheduler.class);

    @Autowired
    DataInitialization dataInitialization;

    @Autowired
    private CacheManagedAvaloqTransactionFeeIntegrationService transactionFeeService;

    @Autowired
    private CacheManagedAvaloqLicenseAdviserFeeIntegrationService licenseAdviserFee;

    @Scheduled(cron = "${static.code.reload.cron}")
    public void reloadAllStaticCodes() {
        logger.info("Static codes reloading started.");
        dataInitialization.loadAllStaticCodes();
        logger.info("Static codes reloading completed.");
    }

    @Scheduled(cron = "${broker.static.reload.cron}")
    public void reloadAllBrokers() {
        if (!AvaloqRequestBuilderUtil.isTemplateJmsEnabled(BrokerEnumTemplate.BROKER_HIERARCHY)) {
            logger.info("Broker data cleared from cache and reloading started.");
            dataInitialization.loadChunkedBrokers();
            logger.info("Broker data cleared from cache and reloading completed.");
        }
    }

    @Scheduled(cron = "${asset.static.reload.cron}")
    public void reloadGeneralAssets() {
        if (!AvaloqRequestBuilderUtil.isTemplateJmsEnabled(AssetEnumTemplate.ASSETS)) {
            logger.info("Asset List cleared from cache and reloading started.");
            dataInitialization.loadAssets();
            logger.info("Asset List cleared from cache and reloading completed.");
        }
    }

    @Scheduled(cron = "${apl.static.reload.cron}")
    public void reloadAPL() {
        if (!AvaloqRequestBuilderUtil.isTemplateJmsEnabled(AplEnumTemplate.ADVISOR_PRODUCTS)) {
            logger.info("APL cleared from cache and reloading started.");
            dataInitialization.loadApl();
            logger.info("APL cleared from cache and reloading completed.");
        }
    }

    @Scheduled(cron = "${bankDate.static.reload.cron}")
    public void reloadAvaloqBankDate() {
        logger.info("Avaloq Bank Date cleared from cache and reloading started.");
        dataInitialization.loadBankDate();
        logger.info("Avaloq Bank Date cleared from cache and reloading completed.");
    }

    @Scheduled(cron = "${td.rates.reload.cron}")
    public void reloadAllTermDepositRates() {
        logger.info("Term Deposit Rates reloading started.");
        dataInitialization.loadTermDepositAssetRates();
        dataInitialization.loadTermDepositProductRates();
        logger.info("Term Deposit Rates reloading completed.");
    }

    @Scheduled(cron = "${transactionfees.reload.cron}")
    public void reloadTransactionFees() {
        logger.info("Transaction fees reloading started.");
        transactionFeeService.clearCache();
        dataInitialization.loadTransactionFees();
        logger.info("Transaction fees reloading completed.");
    }

    @Scheduled(cron = "${licensefees.reload.cron}")
    public void reloadLicenseFees() {
        logger.info("License fees reloading started.");
        licenseAdviserFee.clearCache();
        dataInitialization.loadLicenseAdviserFees();
        logger.info("License fees reloading completed.");
    }

    @Scheduled(cron = "${chessdata.reload.cron}")
    public void reloadChessData(){
        logger.info("Avaloq Chess Data cleared from cache and reloading started.");
        dataInitialization.loadChessParameters();
        logger.info("Avaloq Chess Data cleared from cache and reloading completed.");
    }
}
