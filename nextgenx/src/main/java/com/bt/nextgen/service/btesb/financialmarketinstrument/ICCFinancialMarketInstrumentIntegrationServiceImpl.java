package com.bt.nextgen.service.btesb.financialmarketinstrument;

import com.bt.nextgen.config.WebServiceProviderConfig;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.avaloq.asset.AssetPriceConverter;
import com.bt.nextgen.service.avaloq.asset.ManagedFundPriceImpl;
import com.bt.nextgen.service.avaloq.asset.SharePriceImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetPrice;
import com.bt.nextgen.service.integration.asset.AssetPriceSource;
import com.bt.nextgen.service.integration.asset.AssetPriceStatus;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.ManagedFundAsset;
import com.bt.nextgen.service.integration.asset.ShareAsset;
import com.bt.nextgen.service.integration.financialmarketinstrument.FinancialMarketInstrumentExchangeCode;
import com.bt.nextgen.service.integration.financialmarketinstrument.FinancialMarketInstrumentIntegrationService;
import ns.btfin_com.product.common.financialmarketinstrument.financialmarketinstrumentrequest.v1_0.FinancialMarketInstrumentType;
import ns.btfin_com.product.common.financialmarketinstrument.financialmarketinstrumentrequest.v1_0.RetrieveInstrumentPriceRequestMsgType;
import ns.btfin_com.product.common.financialmarketinstrument.financialmarketinstrumentresponse.v1_0.RetrieveInstrumentPriceResponseMsgType;
import ns.btfin_com.product.common.financialmarketinstrument.financialmarketinstrumentresponse.v1_0.StatusTypeCode;
import ns.btfin_com.product.common.financialmarketinstrument.v1_0.InstrumentCodeIssuerType;
import ns.btfin_com.product.common.financialmarketinstrument.v1_0.InstrumentIdentifierType;
import ns.btfin_com.product.common.financialmarketinstrument.v1_0.InstrumentIdentifiersType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.ws.soap.saaj.SaajSoapEnvelopeException;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Service
public class ICCFinancialMarketInstrumentIntegrationServiceImpl implements FinancialMarketInstrumentIntegrationService {
	private static final Logger logger = LoggerFactory.getLogger(ICCFinancialMarketInstrumentIntegrationServiceImpl.class);

	@Autowired
	private WebServiceProvider provider;

	@Autowired
	private AssetPriceConverter converter;

	@Resource(name = "userDetailsService")
	private BankingAuthorityService userSamlService;

	public AssetPrice loadAssetPrice(String userReferenceNumber, Asset asset, boolean useLivePrice, boolean useFallbackOnFailure) {
		return loadAssetPrices(userReferenceNumber, Arrays.asList(asset), useLivePrice, useFallbackOnFailure).get(0);
	}

	public List<AssetPrice> loadAssetPrices(String userReferenceNumber, Collection<Asset> assets, boolean useLivePrice,
											boolean useFallbackOnFailure) {
		RetrieveInstrumentPriceRequestMsgType request = new RetrieveInstrumentPriceRequestMsgType();
		request.setUserReferenceNumber(userReferenceNumber);
		request.setLivePriceIndicator(useLivePrice);

		for (Asset asset : assets) {
			FinancialMarketInstrumentType financialMarketInstrumentType = new FinancialMarketInstrumentType();

			if (AssetType.SHARE.equals(asset.getAssetType())) {
				InstrumentIdentifiersType identifiers = new InstrumentIdentifiersType();
				identifiers.setExchangeCode(FinancialMarketInstrumentExchangeCode.ASX.name());
				identifiers.setExchangeTickerCode(asset.getAssetCode());
				financialMarketInstrumentType.setInstrumentIdentifiers(identifiers);
			} else {
				InstrumentIdentifierType identifier = new InstrumentIdentifierType();
				identifier.setInstrumentCode(asset.getAssetCode());
				identifier.setInstrumentCodeIssuer(InstrumentCodeIssuerType.APIR);

				InstrumentIdentifiersType identifiers = new InstrumentIdentifiersType();
				identifiers.getInstrumentIdentifier().add(identifier);
				financialMarketInstrumentType.setInstrumentIdentifiers(identifiers);
			}

			request.getFinancialMarketInstrument().add(financialMarketInstrumentType);
		}

		try {
			RetrieveInstrumentPriceResponseMsgType response =
					(RetrieveInstrumentPriceResponseMsgType) provider.sendWebServiceWithSecurityHeader(
							userSamlService.getSamlToken(),
							WebServiceProviderConfig.FINANCIAL_MARKET_INSTRUMENT.getConfigName(), request);

			return processResponse(response, assets, useFallbackOnFailure);
		} catch (SoapFaultClientException | SaajSoapEnvelopeException | WebServiceIOException ex) {
			logger.error("Unable to retrieve asset prices from ICC/MDH: {}", ex);

			if (!useFallbackOnFailure) {
				throw ex;
			}
		}


		return createFallbackAssetPrices(assets);
	}

	private List<AssetPrice> processResponse(RetrieveInstrumentPriceResponseMsgType response, Collection<Asset> assets,
											 boolean useFallbackOnFailure) {
		if (useFallbackOnFailure &&
				(StatusTypeCode.ERROR.equals(response.getStatus()) || StatusTypeCode.WARNING.equals(response.getStatus()))) {
			return createFallbackAssetPrices(assets);
		}

		return converter.toAssetPrices(response, assets);
	}

	private List<AssetPrice> createFallbackAssetPrices(Collection<Asset> assets) {
		List<AssetPrice> assetPrices = new ArrayList<>();

		for (Asset asset : assets) {
			assetPrices.add(createFallbackAssetPrice(asset));
		}

		return assetPrices;
	}

	private AssetPrice createFallbackAssetPrice(Asset asset) {
		if (asset instanceof ShareAsset) {
			SharePriceImpl sharePrice = new SharePriceImpl(asset, AssetPriceStatus.SUCCESS, AssetPriceSource.AVALOQ);
			BigDecimal price = ((ShareAsset) asset).getPrice();
			sharePrice.setLastPrice(price != null ? price.doubleValue() : 0.0);

			return sharePrice;
		} else if (asset instanceof ManagedFundAsset) {
			ManagedFundPriceImpl managedFundPrice = new ManagedFundPriceImpl(asset, AssetPriceStatus.SUCCESS, AssetPriceSource.AVALOQ);
			BigDecimal price = ((ManagedFundAsset) asset).getPrice();
			managedFundPrice.setLastPrice(price != null ? price.doubleValue() : 0.0);

			return managedFundPrice;
		}

		throw new IllegalArgumentException("Asset type \"" + asset.getAssetType() + "\" is not supported");
	}
}
