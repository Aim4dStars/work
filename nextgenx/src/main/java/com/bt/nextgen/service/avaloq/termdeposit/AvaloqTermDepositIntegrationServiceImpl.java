package com.bt.nextgen.service.avaloq.termdeposit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.lambdaj.Lambda;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.exception.ServiceException;
import com.bt.nextgen.core.domain.key.StringIdKey;
import com.bt.nextgen.service.avaloq.*;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.btfin.panorama.core.validation.Validator;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.gateway.AvaloqException;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.integration.termdeposit.TermDeposit;
import com.bt.nextgen.service.integration.termdeposit.TermDepositAction;
import com.bt.nextgen.service.integration.termdeposit.TermDepositIntegrationService;
import com.bt.nextgen.service.integration.termdeposit.TermDepositTrx;
import com.bt.nextgen.service.integration.termdeposit.TermDepositTrxImpl;
import com.bt.nextgen.service.integration.termdeposit.TermDepositTrxRequest;
import com.btfin.abs.trxservice.fidd.v1_0.Data;
import com.btfin.abs.trxservice.fidd.v1_0.FiddReq;
import com.btfin.abs.trxservice.fidd.v1_0.FiddRsp;

import static com.bt.nextgen.service.AvaloqGatewayUtil.asBigDecimal;
import static com.bt.nextgen.service.AvaloqGatewayUtil.asDateTime;


@Service
public class AvaloqTermDepositIntegrationServiceImpl extends AbstractUserCachedAvaloqIntegrationService implements
	TermDepositIntegrationService
{
	private static final Logger logger = LoggerFactory.getLogger(AvaloqTermDepositIntegrationServiceImpl.class);

	@Autowired
	private AvaloqExecute avaloqExecute;

	@Autowired
	private TermDepositConverter termDepositConverter;

	@Autowired
	private Validator validator;

	@Autowired
	private AvaloqGatewayHelperService webserviceClient;

    @Autowired
    private CmsService cmsService;

	/**
	 * Method to perform termdeposit transaction requests.
	 * @param action :  VALIDATE_ADD_TERM_DEPOSIT, BREAK_TERM_DEPOSIT, UPDATE_TERM_DEPOSIT, ADD_TERM_DEPOSIT
	 * @param request : VALIDATE_ADD_TERM_DEPOSIT(Amount, Asset, CurrencyCode, Portfolio),  BREAK_TERM_DEPOSIT(Asset, Portfolio), 
	 * 					UPDATE_TERM_DEPOSIT(Asset, Portfolio, RenewMode), ADD_TERM_DEPOSIT(Amount, Asset, CurrencyCode, Portfolio)
	 * @param serviceErrors
	 * @return
	 */
	@Override
	public boolean termDeposit(final TermDepositAction action, final TermDepositTrxRequest request,
		final ServiceErrors serviceErrors)
	{
		logger.info("AvaloqTermDepositIntegrationServiceImpl.termDeposit(): Term deposit Action:{}, Amount:{}, AccountId:{}, CurrencyCode:{}, Portfolio:{}, RenewMode:{}",
			action.name(),
			request.getAmount(),
			request.getAsset(),
			request.getCurrencyCode(),
			request.getPortfolio(),
			request.getRenewMode());
		try
		{
			new IntegrationOperation("performTermDepositActions", serviceErrors)
			{
				@Override
				public void performOperation()
				{
					FiddReq fiddReq = AvaloqUtils.makeFiddRequest(action, request);
					webserviceClient.sendToWebService(fiddReq, AvaloqOperation.FIDD_REQ, serviceErrors);
				}
			}.run();
		}
		catch (AvaloqException ex)
		{
			return false;
		}
		return true;
	}

	@Override
	public List <TermDeposit> loadTermDeposit(final BrokerKey brokerKey, final ServiceErrors serviceErrors)
	{
		final List <TermDeposit> termDepositList = new ArrayList <TermDeposit>();

		new IntegrationOperation("loadTermDeposit", serviceErrors)
		{
			@Override
			public void performOperation()
			{
				com.avaloq.abs.screen_rep.hira.btfg$ui_pos_list_pos_fidd.Rep report = avaloqExecute.executeReportRequest(new AvaloqReportRequest(Template.ADVISER_TERM_DEPOSIT.getName()).forOeIds(Collections.singletonList(brokerKey.getId())));
				termDepositList.addAll(termDepositConverter.toModel(report, serviceErrors));
				validator.validate(termDepositList, serviceErrors);
			}
		}.run();

		return termDepositList;
	}

	/**
	 * Method to fetch adviser term deposits based on OE ids.
	 *
	 * @param brokerKeys    : Adviser oe ids to pass in the service.
	 * @param serviceErrors
	 */
	@Override
	public List <TermDeposit> loadTermDeposit(final List <BrokerKey> brokerKeys, final ServiceErrors serviceErrors)
	{
		final List <TermDeposit> termDepositList = new ArrayList <TermDeposit>();

		new IntegrationOperation("loadTermDeposit", serviceErrors)
		{
			@Override
			public void performOperation()
			{
				List <String> oeIds = Lambda.collect(brokerKeys, Lambda.on(StringIdKey.class).getId());
				com.avaloq.abs.screen_rep.hira.btfg$ui_pos_list_pos_fidd.Rep report = avaloqExecute.executeReportRequest(new AvaloqReportRequest(Template.ADVISER_TERM_DEPOSIT.getName()).forOeIds(oeIds));
				termDepositList.addAll(termDepositConverter.toModel(report, serviceErrors));
				validator.validate(termDepositList, serviceErrors);
			}
		}.run();

		return termDepositList;
	}

	/**
	 * Method to validate breaking of a term deposit.
	 * @param request : Asset, Portfolio
	 * @param serviceErrors
	 * @return TermDepositTrx(Withdrawal Interest Rate, Withdrawal Prinicpal, Percent Term Elapsed, isErrorOccurred).
	 */
	@Override
	public TermDepositTrx validateBreakTermDeposit(final TermDepositTrxRequest request, final ServiceErrors serviceErrors)
	{
		logger.info("AvaloqTermDepositIntegrationServiceImpl.validateBreakTermDeposit(): AccountId:{}, Portfolio:{}",
			request.getAsset(),
			request.getPortfolio());
		final TermDepositTrx termDepositTrx = new TermDepositTrxImpl();

        return new IntegrationSingleOperation <TermDepositTrx>("validateBreakTermDeposit", serviceErrors)
        {
            @Override
            public TermDepositTrx performOperation()
            {
                FiddReq fiddReq = AvaloqUtils.makeFiddRequest(TermDepositAction.VALIDATE_BREAK_TERM_DEPOSIT, request);
                FiddRsp fiddRsp = webserviceClient.sendToWebService(fiddReq, AvaloqOperation.FIDD_REQ, serviceErrors);
                Data data = fiddRsp.getData();
                if (data != null) {
                    setTermDepositResponse(data, termDepositTrx);
                }
                return termDepositTrx;
            }
        }.run();
	}

    /**
     * Method to validate breaking of a term deposit.
     * @param request : Asset, Portfolio
     * @param serviceErrors
     * @return TermDepositTrx(Withdrawal Interest Rate, Withdrawal Prinicpal, Percent Term Elapsed, isErrorOccurred).
     */
    @Override
    public TermDepositTrx submitBreakTermDeposit(final TermDepositTrxRequest request, final ServiceErrors serviceErrors)
    {
        logger.info("AvaloqTermDepositIntegrationServiceImpl.submitBreakTermDeposit(): AccountId:{}, Portfolio:{}",
                request.getAsset(),
                request.getPortfolio());
        final TermDepositTrx termDepositTrx = new TermDepositTrxImpl();

        return new IntegrationSingleOperation <TermDepositTrx>("submitBreakTermDeposit", serviceErrors)
            {
                @Override
                public TermDepositTrx performOperation()
                {
                    FiddReq fiddReq = AvaloqUtils.makeFiddRequest(TermDepositAction.BREAK_TERM_DEPOSIT, request);
                    FiddRsp fiddRsp = webserviceClient.sendToWebService(fiddReq, AvaloqOperation.FIDD_REQ, serviceErrors);
                    Data data = fiddRsp.getData();
                    if (data != null)
                    {
                        setTermDepositResponse(data, termDepositTrx);
                    }
                    return termDepositTrx;
                }
            }.run();
    }

    private void setTermDepositResponse(Data data, TermDepositTrx termDepositTrx) {
        termDepositTrx.setCurrPrpl(asBigDecimal(data.getCurrPrpl())); //principal ?
        termDepositTrx.setWidrwPrpl(asBigDecimal(data.getWidrwPrpl())); //principal ?
        termDepositTrx.setQty(asBigDecimal(data.getQty())); //principal ?
        termDepositTrx.setOpenDate(null!=data.getOpenDate()?asDateTime(data.getOpenDate()).toString():null); // Open Date
        termDepositTrx.setDaysUntilMaturity(null!=data.getDaysToMaturity() ? data.getDaysToMaturity().getVal(): null); // Days until Maturity
        termDepositTrx.setNoticeEndDate(null!=data.getNoticeEndDate() ? asDateTime(data.getNoticeEndDate()).toString():null);//Notice End Date
        termDepositTrx.setPercentTermElapsed(asBigDecimal(data.getPercTermElapsed())); // % of term elapsed
        termDepositTrx.setMaturityDate(null != data.getMaturityDate() ? asDateTime(data.getMaturityDate()).toString():null);//Maturity Date
        termDepositTrx.setInterestPaid(asBigDecimal(data.getIntrPaid())); //Interest paid
        termDepositTrx.setInterestAccrued(asBigDecimal(data.getIntrAccr()));//Interest accrued since last payment
        termDepositTrx.setInterestRate(asBigDecimal(data.getIntrRate())); //Interest rate
        termDepositTrx.setWithdrawNet(asBigDecimal(data.getWidrwNet())); // Total principal interest paid at withdrawal
        termDepositTrx.setWithdrawInterestPaid(asBigDecimal(data.getWidrwIntrPaid())); //Withdraw interest paid, to be used to calculate Adjusted interest amount
        termDepositTrx.setWithdrawDate(null!=data.getWidrwDate()? asDateTime(data.getWidrwDate()).toString(): null);//withdraw date
        termDepositTrx.setAdjustedInterestRate(asBigDecimal(data.getAdjustWidrwIntrRate())); //Adjusted Interest rate
    }


    /******
     * This method will update the Term Deposit through Avaloq Service <b>FIDD_REQ</b> .
     * @param termDepositInterface
     * @param serviceErrors
     */
	//TODO change this method to use the new TermDeposit interface implementation - this is still using the one from Cash Beta5
    @Override
    @CacheEvict(key = "{#root.target.getActiveProfileCacheKey(),#termDepositInterface.getPortfolioId()}", value = "com.bt.nextgen.avaloq.service.Portfolio")
    public boolean updateTermDeposit(TermDepositInterface termDepositInterface, ServiceErrors serviceErrors)
    {
        try
        {
            FiddReq fiddReq =AvaloqUtils.makeUpdateFiddRequest(termDepositInterface.getPortfolioId(),
                    termDepositInterface.getTdAccountId(),
                    termDepositInterface.getRenewModeId());
            webserviceClient.sendToWebService(fiddReq, AvaloqOperation.FIDD_REQ, serviceErrors);
        }
        catch (AvaloqException ex)
        {
            logger.warn("Error during updating term deposit",ex);
            return false;
        }
        return true;

    }

}
