package com.bt.nextgen.service.avaloq.portfolio;

import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractUserCachedAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.portfolio.cashmovement.CashMovementsHolder;
import com.bt.nextgen.service.avaloq.portfolio.movement.ValuationMovementConverter;
import com.bt.nextgen.service.avaloq.portfolio.movement.ValuationMovementImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.AccountValuationHolder;
import com.bt.nextgen.service.avaloq.portfolio.valuation.CashAccruedIncomeHolder;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ValuationConverter;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.portfolio.PortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.ValuationMovement;
import com.bt.nextgen.service.integration.portfolio.cashmovements.CashMovement;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import com.bt.nextgen.service.request.AvaloqReportRequestImpl;
import com.bt.nextgen.service.request.AvaloqRequest;
import com.btfin.panorama.core.validation.Validator;
import oracle.sql.DATE;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service("avaloqPortfolioIntegrationService")
public class AvaloqPortfolioIntegrationServiceImpl extends AbstractUserCachedAvaloqIntegrationService
        implements PortfolioIntegrationService {

    @Autowired
    private AvaloqReportService avaloqService;

    @Autowired
    private Validator validator;

    @Autowired
    private ValuationMovementConverter valuationMovementConverter;

    @Autowired
    private ValuationConverter valuationConverter;

    @Override
    /**
     * {@inheritDoc}
     */
    public ValuationMovement loadValuationMovement(final AccountKey accountKey, final DateTime startDate, final DateTime endDate,
            final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<ValuationMovementImpl>("loadValuationMovement", serviceErrors) {
            @Override
            public ValuationMovementImpl performOperation() {

                AvaloqRequest avaloqRequest = new AvaloqReportRequestImpl(PortfolioTemplate.VALUATION_MOVEMENT)
                        .forParam(PortfolioParams.PARAM_ACCOUNT_ID, accountKey.getId())
                        .forParam(PortfolioParams.PARAM_NAME_START_DATE, startDate)
                        .forParam(PortfolioParams.PARAM_NAME_END_DATE, endDate);

                com.avaloq.abs.screen_rep.hira.btfg$ui_perf_cf_list_bp_portf_mvt.Rep report = avaloqService
                        .executeReportRequest(avaloqRequest, serviceErrors);

                ValuationMovementImpl valuationMovement = valuationMovementConverter.toModel(report, serviceErrors);
                if (valuationMovement != null) {
                    validator.validate(valuationMovement, serviceErrors);
                }

                return valuationMovement;
            }
        }.run();
    }

    @Override
    public Collection<CashMovement> loadCashMovement(final AccountKey accountKey, final DateTime effectiveDate, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<Collection<CashMovement>>("loadCashMovement", serviceErrors) {
            @Override
            public Collection<CashMovement> performOperation() {

                AvaloqRequest avaloqRequest = new AvaloqReportRequestImpl(PortfolioTemplate.CASH_MOVEMENT)
                        .forParam(PortfolioParams.PARAM_ACCOUNT_ID, accountKey.getId())
                        .forParam(PortfolioParams.PARAM_VAL_DATE_FROM, effectiveDate);

                CashMovementsHolder holder = avaloqService.executeReportRequestToDomain(avaloqRequest, CashMovementsHolder.class,
                        serviceErrors);

                return holder.getCashMovements();
            }
        }.run();
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public WrapAccountValuation loadWrapAccountValuation(final AccountKey accountKey, final DateTime effectiveDate,
            final ServiceErrors serviceErrors) {
        return loadWrapAccountValuation(accountKey, effectiveDate, false, serviceErrors);
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public WrapAccountValuation loadWrapAccountValuation(final AccountKey accountKey, final DateTime effectiveDate,
            final boolean includeExternal, final ServiceErrors serviceErrors) {
        return loadWrapAccountValuation(accountKey, effectiveDate, includeExternal, true, serviceErrors);
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public WrapAccountValuation loadWrapAccountValuation(final AccountKey accountKey, final DateTime effectiveDate,
            final boolean includeExternal, final boolean includeIncome, final ServiceErrors serviceErrors) {
        boolean historicIncome = Properties.getSafeBoolean("feature.historicIncomeAccrued") && includeIncome;
        if (new LocalDate().toDateTimeAtStartOfDay().compareTo(effectiveDate) == 0 || !historicIncome) {
            return loadWrapAccountValuationCurrentDate(accountKey, effectiveDate, includeExternal, serviceErrors);
        } else {
            return loadWrapAccountValuationHistoric(accountKey, effectiveDate, includeExternal, serviceErrors);
        }
    }


    private AccountValuationHolder loadAccountValuationHolder(final AccountKey accountKey, final DateTime effectiveDate,
            final boolean includeExternal, final ServiceErrors serviceErrors) {
        AvaloqRequest avaloqRequest = new AvaloqReportRequestImpl(PortfolioTemplate.ACCOUNT_VALUATION)
                .forParam(PortfolioParams.PARAM_INCLUDE_ACCOUNT_ID, accountKey.getId())
                .forParam(PortfolioParams.PARAM_NAME_EFFECTIVE_DATE, effectiveDate);
        return avaloqService.executeReportRequestToDomain(avaloqRequest, AccountValuationHolder.class, serviceErrors);
    }

    private CashAccruedIncomeHolder loadCashIncomeHolder(final AccountKey accountKey, final DateTime effectiveDate,
            final ServiceErrors serviceErrors) {
        AvaloqRequest avaloqRequest = new AvaloqReportRequestImpl(PortfolioTemplate.HOLDING_CASH_INCOME)
                .forParam(PortfolioParams.PARAM_ACCOUNT_ID1, accountKey.getId())
                .forParam(PortfolioParams.PARAM_EX_DATE_TO, effectiveDate)
                .forParam(PortfolioParams.PARAM_VAL_DATE_FROM, effectiveDate.plusDays(1));
        return avaloqService.executeReportRequestToDomain(avaloqRequest, CashAccruedIncomeHolder.class, serviceErrors);
    }


    private WrapAccountValuation loadWrapAccountValuationCurrentDate(final AccountKey accountKey, final DateTime effectiveDate,
            final boolean includeExternal, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<WrapAccountValuation>("loadAccountValuation", serviceErrors) {
            @Override
            public WrapAccountValuation performOperation() {
                AccountValuationHolder valuationReport = loadAccountValuationHolder(accountKey, effectiveDate, includeExternal,
                        serviceErrors);
                return valuationConverter.toModel(valuationReport, new CashAccruedIncomeHolder(), effectiveDate, includeExternal,
                        serviceErrors);
            }
        }.run();
    }

    private WrapAccountValuation loadWrapAccountValuationHistoric(final AccountKey accountKey, final DateTime effectiveDate,
            final boolean includeExternal, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<WrapAccountValuation>("loadAccountValuation", serviceErrors) {
            @Override
            public WrapAccountValuation performOperation() {
                AccountValuationHolder valuationReport = loadAccountValuationHolder(accountKey, effectiveDate, includeExternal,
                        serviceErrors);
                CashAccruedIncomeHolder cashIncome = loadCashIncomeHolder(accountKey, effectiveDate, serviceErrors);
                return valuationConverter.toModel(valuationReport, cashIncome, effectiveDate, includeExternal, serviceErrors);
            }
        }.run();
    }
}
