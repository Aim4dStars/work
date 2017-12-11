package com.bt.nextgen.service.avaloq.modelportfolio;

import com.avaloq.abs.screen_rep.hira.btfg$ui_book_list_invst_mgr_cont_doc_evt_pos_shdw_mp.Cont;
import com.avaloq.abs.screen_rep.hira.btfg$ui_book_list_invst_mgr_cont_doc_evt_pos_shdw_mp.ContHead;
import com.avaloq.abs.screen_rep.hira.btfg$ui_book_list_invst_mgr_cont_doc_evt_pos_shdw_mp.Doc;
import com.avaloq.abs.screen_rep.hira.btfg$ui_book_list_invst_mgr_cont_doc_evt_pos_shdw_mp.DocHead;
import com.avaloq.abs.screen_rep.hira.btfg$ui_book_list_invst_mgr_cont_doc_evt_pos_shdw_mp.Evt;
import com.avaloq.abs.screen_rep.hira.btfg$ui_book_list_invst_mgr_cont_doc_evt_pos_shdw_mp.EvtHead;
import com.avaloq.abs.screen_rep.hira.btfg$ui_book_list_invst_mgr_cont_doc_evt_pos_shdw_mp.Pos;
import com.avaloq.abs.screen_rep.hira.btfg$ui_book_list_invst_mgr_cont_doc_evt_pos_shdw_mp.PosHead;
import com.avaloq.abs.screen_rep.hira.btfg$ui_pos_list_asset_x_wgt.Asset;
import com.avaloq.abs.screen_rep.hira.btfg$ui_pos_list_asset_x_wgt.AssetClass;
import com.avaloq.abs.screen_rep.hira.btfg$ui_pos_list_asset_x_wgt.AssetClassHead;
import com.avaloq.abs.screen_rep.hira.btfg$ui_pos_list_asset_x_wgt.AssetHead;
import com.avaloq.abs.screen_rep.hira.btfg$ui_pos_list_asset_x_wgt.IpsHead;
import com.bt.nextgen.core.mapping.AbstractMappingConverter;
import com.btfin.panorama.core.mapping.Mapper;
import com.bt.nextgen.core.mapping.MappingUtil;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AvaloqUtils;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.modelportfolio.CashForecast;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolio;
import com.bt.nextgen.service.integration.modelportfolio.ShadowPortfolio;
import com.bt.nextgen.service.integration.modelportfolio.ShadowPortfolioAsset;
import com.bt.nextgen.service.integration.modelportfolio.ShadowPortfolioAssetSummary;
import com.bt.nextgen.service.integration.modelportfolio.ShadowTransaction;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@SuppressWarnings("squid:UnusedProtectedMethod")
class ModelPortfolioConverter extends AbstractMappingConverter
{
	private static final Logger logger = LoggerFactory.getLogger(ModelPortfolioConverter.class);

	@Autowired
	private StaticIntegrationService staticService;

    protected Map<IpsKey, ModelPortfolio> toModelHeader(
		com.avaloq.abs.screen_rep.hira.btfg$ui_ips_list_ips_im_rep_hdr.Rep report, ServiceErrors serviceErrors)
	{
        Map<IpsKey, ModelPortfolio> modelPortfolioMap = new HashMap<>();
		Mapper mapper = getMapper();
		if (!MappingUtil.isEmpty(report, serviceErrors))
		{
			for (com.avaloq.abs.screen_rep.hira.btfg$ui_ips_list_ips_im_rep_hdr.InvstMgr mgr : report.getData()
				.getInvstMgrList()
				.getInvstMgr())
			{
				for (com.avaloq.abs.screen_rep.hira.btfg$ui_ips_list_ips_im_rep_hdr.Ips ips : mgr.getIpsList().getIps())
				{
					for (com.avaloq.abs.screen_rep.hira.btfg$ui_ips_list_ips_im_rep_hdr.IpsHead ipsHead : ips.getIpsHeadList()
						.getIpsHead())
					{
						ModelPortfolioImpl modelPortfolio = mapper.map(ipsHead, ModelPortfolioImpl.class, serviceErrors);
						modelPortfolio.setStatus(staticService.loadCode(CodeCategory.IPS_STATUS,
								AvaloqGatewayUtil.asString(ipsHead.getStatusId()),
							serviceErrors).getName());
						modelPortfolioMap.put(modelPortfolio.getModelKey(), modelPortfolio);
					}
				}
			}
		}
		return modelPortfolioMap;
	}

    protected Map<IpsKey, CashForecast> toCashForecast(
		com.avaloq.abs.screen_rep.hira.btfg$ui_pos_list_ips_shdw_mp_cash.Rep report, ServiceErrors serviceErrors)
	{
        Map<IpsKey, CashForecast> cashForecastMap = new HashMap<>();
		Mapper mapper = getMapper();

		if (!MappingUtil.isEmpty(report, serviceErrors))
		{
			for (com.avaloq.abs.screen_rep.hira.btfg$ui_pos_list_ips_shdw_mp_cash.InvstMgr mgr : report.getData()
				.getInvstMgrList()
				.getInvstMgr())
			{
				for (com.avaloq.abs.screen_rep.hira.btfg$ui_pos_list_ips_shdw_mp_cash.Ips ips : mgr.getIpsList().getIps())
				{
					CashForecastImpl cashForecast = null;
					for (com.avaloq.abs.screen_rep.hira.btfg$ui_pos_list_ips_shdw_mp_cash.Pos pos : ips.getPosList().getPos())
					{
						for (com.avaloq.abs.screen_rep.hira.btfg$ui_pos_list_ips_shdw_mp_cash.PosHead posHead : pos.getPosHeadList()
							.getPosHead())
						{
							cashForecast = mapper.map(posHead, CashForecastImpl.class, serviceErrors);
						}
					}

					for (com.avaloq.abs.screen_rep.hira.btfg$ui_pos_list_ips_shdw_mp_cash.IpsHead ipsHead : ips.getIpsHeadList()
						.getIpsHead())
					{
                        cashForecastMap.put(IpsKey.valueOf(AvaloqGatewayUtil.asString(ipsHead.getIpsId())), cashForecast);
					}
				}
			}
		}

		return cashForecastMap;
	}

    protected Map<IpsKey, ShadowPortfolio> toShadowPortfolioModel(
		com.avaloq.abs.screen_rep.hira.btfg$ui_pos_list_asset_x_wgt.Rep report, ServiceErrors serviceErrors)
	{
        Map<IpsKey, ShadowPortfolio> shadowPortfolioMap = new HashMap<>();
		Mapper mapper = getMapper();

		if (!MappingUtil.isEmpty(report, serviceErrors))
		{
			DateTime asAtDate = AvaloqGatewayUtil.asDateTime(report.getMetadata().getAsAtDate());

			for (com.avaloq.abs.screen_rep.hira.btfg$ui_pos_list_asset_x_wgt.InvstMgr mgr : report.getData()
				.getInvstMgrList()
				.getInvstMgr())
			{
				for (com.avaloq.abs.screen_rep.hira.btfg$ui_pos_list_asset_x_wgt.Ips ips : mgr.getIpsList().getIps())
				{
					ShadowPortfolioImpl shadowPortfolio = new ShadowPortfolioImpl();
					shadowPortfolio.setAsAtDate(asAtDate);
					List <ShadowPortfolioAssetSummary> assetSummaries = new ArrayList <>();
					shadowPortfolio.setAssetSummaries(assetSummaries);

					for (AssetClass assetClass : ips.getAssetClassList().getAssetClass())
					{
						ShadowPortfolioAssetSummaryImpl assetSummary = new ShadowPortfolioAssetSummaryImpl();
						List <ShadowPortfolioAsset> assets = new ArrayList <>();
						for (AssetClassHead assetClassHead : assetClass.getAssetClassHeadList().getAssetClassHead())
						{
							assetSummary.setTotal(mapper.map(assetClassHead, ShadowPortfolioDetailImpl.class, serviceErrors));
							assetSummary.setAssetClass(staticService.loadCode(CodeCategory.ASSET_CLASS,
									AvaloqGatewayUtil.asString(assetClassHead.getAssetClassId()),
								serviceErrors).getName());
						}

						for (Asset asset : assetClass.getAssetList().getAsset())
						{
							for (AssetHead assetHead : asset.getAssetHeadList().getAssetHead())
							{
								ShadowPortfolioAssetImpl shadowAsset = new ShadowPortfolioAssetImpl();
								shadowAsset.setAssetId(AvaloqGatewayUtil.asString(assetHead.getAssetId()));
								shadowAsset.setShadowDetail(mapper.map(assetHead, ShadowPortfolioDetailImpl.class, serviceErrors));
								assets.add(shadowAsset);
							}
						}

						assetSummary.setAssets(assets);
						assetSummaries.add(assetSummary);
					}

					for (IpsHead ipsHead : ips.getIpsHeadList().getIpsHead())
					{
						shadowPortfolio.setTotal(mapper.map(ipsHead, ShadowPortfolioDetailImpl.class, serviceErrors));
                        shadowPortfolioMap.put(IpsKey.valueOf(AvaloqGatewayUtil.asString(ipsHead.getIpsId())),
							shadowPortfolio);
					}
				}
			}
		}

		return shadowPortfolioMap;
	}

    protected Map<IpsKey, List<ShadowTransaction>> toShadowTransactionModel(
		com.avaloq.abs.screen_rep.hira.btfg$ui_book_list_invst_mgr_cont_doc_evt_pos_shdw_mp.Rep report,
		ServiceErrors serviceErrors)
	{
        Map<IpsKey, List<ShadowTransaction>> shadowTransactionMap = new HashMap<>();
		Mapper mapper = getMapper();

		if (!MappingUtil.isEmpty(report, serviceErrors))
		{
			for (com.avaloq.abs.screen_rep.hira.btfg$ui_book_list_invst_mgr_cont_doc_evt_pos_shdw_mp.InvstMgr mgr : report.getData()
				.getInvstMgrList()
				.getInvstMgr())
			{
				for (Cont cont : mgr.getContList().getCont())
				{
					List <ShadowTransaction> transactions = new ArrayList <>();
					for (Doc doc : cont.getDocList().getDoc())
					{
						String transactionId = null;
						for (DocHead docHead : doc.getDocHeadList().getDocHead())
						{
							transactionId = AvaloqGatewayUtil.asString(docHead.getDocId());
						}

						for (Evt evt : doc.getEvtList().getEvt())
						{
							ShadowTransactionImpl transaction = null;
							for (Pos pos : evt.getPosList().getPos())
							{
								for (PosHead posHead : pos.getPosHeadList().getPosHead())
								{
									transaction = mapper.map(posHead, ShadowTransactionImpl.class, serviceErrors);
									transaction.setTransactionId(transactionId);

									for (EvtHead evtHead : evt.getEvtHeadList().getEvtHead())
									{
										transaction.setTransactionType(staticService.loadCode(CodeCategory.ORDER_TYPE,
												AvaloqGatewayUtil.asString(evtHead.getOrderTypeId()),
											serviceErrors).getName());
										transaction.setStatus(AvaloqGatewayUtil.asString(evtHead.getStatus()));
										transaction.setTradeDate(AvaloqGatewayUtil.asDateTime(evtHead.getTrxDate()));
										transaction.setValueDate(AvaloqGatewayUtil.asDateTime(evtHead.getValDate()));
										transaction.setPerformanceDate(AvaloqGatewayUtil.asDateTime(evtHead.getPerfDate()));
										transaction.setDescription(AvaloqGatewayUtil.asString(evtHead.getExtlBookText()));
									}

									transactions.add(transaction);
								}
							}
						}
					}

					for (ContHead contHead : cont.getContHeadList().getContHead())
					{
                        shadowTransactionMap.put(IpsKey.valueOf(AvaloqGatewayUtil.asString(contHead.getIpsId())),
							transactions);
					}
				}
			}
		}

		return shadowTransactionMap;
	}
}
