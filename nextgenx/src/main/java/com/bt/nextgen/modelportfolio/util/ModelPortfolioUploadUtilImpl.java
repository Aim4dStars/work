package com.bt.nextgen.modelportfolio.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioAssetAllocationDto;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioUploadDto;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;

import au.com.bytecode.opencsv.CSVReader;
import ch.lambdaj.Lambda;

@Service
public class ModelPortfolioUploadUtilImpl implements ModelPortfolioUploadUtil
{
	private static final Logger logger = LoggerFactory.getLogger(ModelPortfolioUploadUtilImpl.class);
	private static final String CASH = "MACC.MP.AUD";
    private static final String MODEL_CASH = "Model Cash";
    private static final String MP_CASH = "MPCash";

	@Autowired
	private CmsService cmsService;

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;


	@Override
	public ModelPortfolioUploadDto parseFile(String modelId, MultipartFile file)
	{
		CSVReader reader = null;
		try
		{
			if (!("text/csv".equals(file.getContentType()) || MediaType.TEXT_PLAIN_VALUE.equals(file.getContentType()) || "application/vnd.ms-excel".equals(file.getContentType())))
			{
				throw new BadRequestException(ApiVersion.CURRENT_VERSION, cmsService.getContent("Err.IP-0119"));
			}

			String modelCode = null;
			String modelName = null;
			String commentary = null;
			List <ModelPortfolioAssetAllocationDto> modelAllocations = new ArrayList <>();

			reader = new CSVReader(new InputStreamReader(file.getInputStream()));
			List <String[]> lines = reader.readAll();
			if (lines != null)
			{
                Map<String, Asset> assetMap = getAssetMap(lines);
				int lineNum = 1;
				boolean foundAllocationHeading = false;

				for (String[] line : lines)
				{
					if (lineNum == 1)
					{
						modelCode = line[1];
					}
					else if (lineNum == 2)
					{
						modelName = line[1];
					}
					else if (lineNum == 3)
					{
						commentary = getCommentary(line);
					}
					else
					{
						if (!foundAllocationHeading)
						{
							if ("Asset".equals(line[0]))
							{
								foundAllocationHeading = true;
							}
						}
						else
						{
                            ModelPortfolioAssetAllocationDto assetAllocation = getAssetAllocation(line, assetMap);
							if (assetAllocation != null)
							{
								modelAllocations.add(assetAllocation);
							}
						}
					}

					lineNum++;
				}

				if (modelCode != null && !modelAllocations.isEmpty())
				{
					return new ModelPortfolioUploadDto(new ModelPortfolioKey(modelId),
						modelCode,
						modelName,
						commentary,
						modelAllocations);
				}
			}

			logger.error("failed to parse upload model file. modelId: " + modelId + " modelCode: " + modelCode + " modelName: "
				+ modelName + " commentary: " + commentary + " allocations size: " + modelAllocations.size());
			throw new BadRequestException(ApiVersion.CURRENT_VERSION, cmsService.getContent("Err.IP-0164"));
		}
		catch (IOException | NumberFormatException e)
		{
			logger.error("failed to read upload model file", e);
			throw new BadRequestException(ApiVersion.CURRENT_VERSION, cmsService.getContent("Err.IP-0164"));
		}
		finally
		{
			try
			{
				if (reader != null)
				{
					reader.close();
				}
			}
			catch (IOException e)
			{
				logger.error("failed to close upload file", e);
			}
		}
	}

	protected String getCommentary(String[] line)
	{
		String commentary = line[1];
		for (int i = 2; i < line.length; i++)
		{
			commentary += "," + line[i];
		}

		if (commentary.length() > 1000)
		{
			commentary = commentary.substring(0, 1000);
		}

		return commentary;
	}

    protected ModelPortfolioAssetAllocationDto getAssetAllocation(String[] line, Map<String, Asset> assetMap)
	{
		String assetCode = line[0];
		if (!StringUtils.isEmpty(assetCode))
		{
            if (isCashAsset(assetCode.trim()))
			{
				assetCode = CASH;
			}

			BigDecimal allocation = getPercentage(line[1], BigDecimal.ZERO);
			BigDecimal tradePercent = getPercentage(line[2], null);

            return new ModelPortfolioAssetAllocationDto(assetMap.get(assetCode), allocation, tradePercent, null);
		}

		return null;
	}

    private Map<String, Asset> getAssetMap(List<String[]> lines) {
        int lineNum = 2;
        boolean assetCodeStart = false;
        while (lineNum < lines.size() && !assetCodeStart) {
            String[] line = lines.get(lineNum);
            if (line.length > 0 && "Asset".equals(line[0].trim())) {
                assetCodeStart = true;
            }
            lineNum++;
        }
        List<String> assetCodes = new ArrayList<>();

        for (int i = lineNum; i < lines.size(); i++) {
            String[] line = lines.get(i);
            if (line.length > 0) {
                if (isCashAsset(line[0].trim())) {
                    assetCodes.add(CASH);
                } else {
                    assetCodes.add(line[0]);
                }
            }
        }

        List<Asset> assets = assetService.loadAssetsForAssetCodes(assetCodes, new FailFastErrorsImpl());
        if (assetCodes.contains(CASH)) {
            // Retrieve cash asset by loading it via the service directly.
            assets.add(getModelPortfolioCashAsset());
        }
        return Lambda.index(assets, Lambda.on(Asset.class).getAssetCode());
    }

	protected final BigDecimal getPercentage(String value, BigDecimal defaultVal)
	{
		if (!StringUtils.isEmpty(value) && !"-".equals(value))
		{
			if (value.contains("%"))
			{
				return new BigDecimal(value.substring(0, value.indexOf("%")));
			}
			else
			{
				return new BigDecimal(value);
			}
		}

		return defaultVal;
    }

    /**
     * Determine if the specified assetCode from the uploaded file is a cash asset.
     * 
     * @param assetCode
     * @return true if assetCode is either "ModelCash" or "MPCash".
     */
    private boolean isCashAsset(String assetCode) {
        return MODEL_CASH.equalsIgnoreCase(assetCode) || MP_CASH.equalsIgnoreCase(assetCode);
	}

    /**
     * Retrieve Managed Portfolio cash via the assetService.
     * 
     * @return
     */
    private Asset getModelPortfolioCashAsset() {
        Collection<AssetType> assetTypes = Collections.singletonList(AssetType.CASH);
        Map<String, Asset> cashAssetMap = assetService.loadAssetsForCriteria(
                (Collection<String>) Collections.<String> emptyList(), Constants.EMPTY_STRING, assetTypes,
                new FailFastErrorsImpl());
        for (Asset asset : cashAssetMap.values()) {
            if ("Managed Portfolio".equalsIgnoreCase(asset.getMoneyAccountType())) {
                AssetImpl cashAsset = new AssetImpl();
                cashAsset.setAssetId(asset.getAssetId());
                cashAsset.setAssetName(asset.getAssetName());
                cashAsset.setAssetType(asset.getAssetType());
                cashAsset.setAssetCode(CASH);
                return cashAsset;
            }
        }
        logger.error("failed to retrieve Cash asset for Managed Portfolio");
        return null;
    }
}
