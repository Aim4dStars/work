package com.bt.nextgen.api.modelportfolio.v2.service.detail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.api.modelportfolio.v2.model.defaultparams.ModelPortfolioType;
import com.bt.nextgen.api.modelportfolio.v2.model.detail.ModelOfferDto;
import com.bt.nextgen.api.modelportfolio.v2.model.detail.ModelPortfolioDetailDto;
import com.bt.nextgen.api.modelportfolio.v2.model.detail.ModelPortfolioDetailDtoImpl;
import com.bt.nextgen.api.modelportfolio.v2.model.detail.TargetAllocationDto;
import com.bt.nextgen.api.modelportfolio.v2.service.defaultparams.TailoredPortfolioOfferDtoService;
import com.bt.nextgen.api.modelportfolio.v2.util.common.AdviserModelHelper;
import com.bt.nextgen.api.modelportfolio.v2.util.common.ModelPortfolioHelper;
import com.bt.nextgen.api.modelportfolio.v2.validation.ModelPortfolioDtoErrorMapper;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.modelportfolio.detail.ModelPortfolioDetailImpl;
import com.bt.nextgen.service.avaloq.modelportfolio.detail.OfferDetailImpl;
import com.bt.nextgen.service.avaloq.modelportfolio.detail.TargetAllocationImpl;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;
import com.bt.nextgen.service.integration.modelportfolio.detail.ConstructionType;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioDetail;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioDetailIntegrationService;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioStatus;
import com.bt.nextgen.service.integration.modelportfolio.detail.OfferDetail;
import com.bt.nextgen.service.integration.modelportfolio.detail.TargetAllocation;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

@Service("ModelPortfolioDetailDtoServiceV2")
@Transactional(value = "springJpaTransactionManager")
public class ModelPortfolioDetailDtoServiceImpl implements ModelPortfolioDetailDtoService {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private ModelPortfolioDetailIntegrationService modelPortfolioService;

    @Autowired
    private InvestmentPolicyStatementIntegrationService invPolicyService;

    @Autowired
    private ModelPortfolioDtoErrorMapper errorMapper;

    @Autowired
    private ModelPortfolioHelper helper;

    @Autowired
    private AdviserModelHelper advModelhelper;

    @Autowired
    private TailoredPortfolioOfferDtoService tmpOfferService;

    @Override
    public ModelPortfolioDetailDto validate(ModelPortfolioDetailDto dto, ServiceErrors serviceErrors) {

        ModelPortfolioDetail model = modelPortfolioService.validateModelPortfolio(toModel(dto, serviceErrors), serviceErrors);
        List<TargetAllocationDto> targetAllocationDtos = getTargetAllocationDtos(model.getTargetAllocations(), Boolean.TRUE,
                serviceErrors);

        return new ModelPortfolioDetailDtoImpl(model, targetAllocationDtos, getOfferDtoList(model), errorMapper.map(model
                .getValidationErrors()));
    }

    @Override
    public ModelPortfolioDetailDto submit(ModelPortfolioDetailDto dto, ServiceErrors serviceErrors) {

        ModelPortfolioDetail model = modelPortfolioService.submitModelPortfolio(toModel(dto, serviceErrors), serviceErrors);
        List<TargetAllocationDto> targetAllocationDtos = getTargetAllocationDtos(model.getTargetAllocations(), Boolean.FALSE,
                serviceErrors);

        return new ModelPortfolioDetailDtoImpl(model, targetAllocationDtos, getOfferDtoList(model), errorMapper.map(model
                .getValidationErrors()));
    }

    @Override
    public ModelPortfolioDetailDto find(ModelPortfolioKey key, ServiceErrors serviceErrors) {
        // Retrieve ModelPortfolioDetails from BTFG$UI_IPS_LIST.IPS#IPS_DET for editing
        final IpsKey ipsKey = IpsKey.valueOf(key.getModelId());
        Map<IpsKey, ModelPortfolioDetail> result = invPolicyService.getModelDetails(Collections.singletonList(ipsKey),
                serviceErrors);

        if (result != null && result.keySet().contains(ipsKey)) {
            ModelPortfolioDetail model = result.get(ipsKey);
            List<TargetAllocationDto> targetAllocationDtos = getTargetAllocationDtos(model.getTargetAllocations(), Boolean.TRUE,
                    serviceErrors);

            return new ModelPortfolioDetailDtoImpl(model, targetAllocationDtos, getOfferDtoList(model), null);
        }
        return null;
    }

    @Override
    public ModelPortfolioDetailDto update(ModelPortfolioDetailDto dto, ServiceErrors serviceErrors) {

        ModelPortfolioDetail model = modelPortfolioService.submitModelPortfolio(toModel(dto, serviceErrors), serviceErrors);
        List<TargetAllocationDto> targetAllocationDtos = getTargetAllocationDtos(model.getTargetAllocations(), Boolean.FALSE,
                serviceErrors);

        return new ModelPortfolioDetailDtoImpl(model, targetAllocationDtos, getOfferDtoList(model), errorMapper.map(model
                .getValidationErrors()));
    }

    /**
     * Convert the specified ModelPortfolioDetailDTO to the corresponding model object.
     * 
     * @param dto
     * @param serviceErrors
     * @return
     */
    protected ModelPortfolioDetail toModel(ModelPortfolioDetailDto dto, ServiceErrors serviceErrors) {
        BrokerKey investmentManagerKey = getInvestmentManagerKey(serviceErrors);
        ModelPortfolioType portfolioType = getPortfolioType();

        ModelPortfolioDetailImpl model = new ModelPortfolioDetailImpl();
        model.setId(dto.getKey() == null ? null : dto.getKey().getModelId());
        model.setName(dto.getModelName());
        model.setSymbol(dto.getModelCode());

        // Auto generate model symbol for Adviser Model.
        if (Properties.getSafeBoolean("feature.model.advisermodel")) {
            if (dto.getKey() == null && dto.getModelCode() == null) {
                model.setSymbol(advModelhelper.generateUniqueModelId("XA", serviceErrors));
            }
            model.setInvestmentStyleDesc(dto.getOtherInvestmentStyle());
            model.setModelDescription(dto.getModelDescription());
            model.setMinimumTradeAmount(dto.getMinimumOrderAmount());
            model.setMinimumTradePercent(dto.getMinimumOrderPercent());
        }

        model.setMinimumInvestment(dto.getMinimumInvestment());
        model.setInvestmentManagerId(investmentManagerKey);
        model.setStatus(ModelPortfolioStatus.forName(dto.getStatus()));
        model.setOpenDate(dto.getOpenDate());
        model.setModelStructure(dto.getModelStructure());
        model.setInvestmentStyle(dto.getInvestmentStyle());
        model.setModelAssetClass(dto.getModelAssetClass());
        model.setModelType(dto.getModelType());
        model.setModelConstruction(ModelPortfolioType.PREFERRED.equals(portfolioType) ? ConstructionType.FIXED : ConstructionType
                .forDisplayValue(dto.getModelConstruction()));
        model.setPortfolioConstructionFee(dto.getPortfolioConstructionFee());
        model.setTargetAllocations(getTargetAllocations(dto.getTargetAllocations()));
        model.setMpSubType(portfolioType.getIntlId());

        ModelType ipsModel = ModelType.forCode(dto.getAccountType());
        if (ipsModel != null) {
            model.setAccountType(ipsModel.getCode());
        }

        model.setOfferDetails(getOfferModelList(dto.getModelOffers()));
        return model;
    }

    /**
     * Determine which user is the IM for the portfolio. IM/PM/DG will manage TMP, Adviser will manage PP
     * 
     * @param serviceErrors
     * @return
     */
    private BrokerKey getInvestmentManagerKey(ServiceErrors serviceErrors) {
        if (userProfileService.isDealerGroup() || userProfileService.isInvestmentManager()
                || userProfileService.isPortfolioManager()) {
            return userProfileService.getInvestmentManager(serviceErrors).getKey();
        }
        return BrokerKey.valueOf(userProfileService.getPositionId());
    }

    /**
     * Determine which type of portfolio is to be created based on the active user
     * 
     * @return
     */
    private ModelPortfolioType getPortfolioType() {
        if (userProfileService.isDealerGroup() || userProfileService.isInvestmentManager()
                || userProfileService.isPortfolioManager()) {
            return ModelPortfolioType.TAILORED;
        }
        return ModelPortfolioType.PREFERRED;
    }

    /**
     * Construct a collection of TargetAllocation model object based on the specified DTOs.
     * 
     * @param targetAllocationDtos
     * @return
     */
    private List<TargetAllocation> getTargetAllocations(List<TargetAllocationDto> targetAllocationDtos) {

        List<TargetAllocation> targetAllocations = new ArrayList<TargetAllocation>();

        if (targetAllocationDtos != null) {
            for (TargetAllocationDto targetAllocationDto : targetAllocationDtos) {
                TargetAllocation targetAllocation = new TargetAllocationImpl();
                targetAllocation.setAssetClass(targetAllocationDto.getAssetClass());
                targetAllocation.setMinimumWeight(targetAllocationDto.getMinimumWeight());
                targetAllocation.setMaximumWeight(targetAllocationDto.getMaximumWeight());
                targetAllocation.setNeutralPos(targetAllocationDto.getNeutralPos());
                if (targetAllocationDto.getIndexAsset() != null) {
                    targetAllocation.setIndexAssetId(targetAllocationDto.getIndexAsset().getAssetId());
                }
                targetAllocations.add(targetAllocation);
            }
        }
        return targetAllocations;
    }

    /**
     * Construct a collection of TargetAllocationDto based on the taaList specified.
     * 
     * @param taaList
     * @param multiplier
     * @param serviceErrors
     * @return
     */
    private List<TargetAllocationDto> getTargetAllocationDtos(List<TargetAllocation> taaList, boolean multiplier,
            ServiceErrors serviceErrors) {

        List<TargetAllocationDto> taaDtos = new ArrayList<TargetAllocationDto>();

        if (taaList != null && !taaList.isEmpty()) {
            Map<String, AssetDto> assetDtoMap = helper.getAssetDtoMap(taaList, serviceErrors);
            for (TargetAllocation taa : taaList) {
                AssetDto indexAsset = assetDtoMap.get(taa.getIndexAssetId());
                TargetAllocationDto taaDto = helper.getTargetAllocationDto(taa, indexAsset, multiplier);
                taaDtos.add(taaDto);
            }
        }
        return taaDtos;
    }

    /**
     * Based on the ModelOfferDto list specified, construct the corresponding model objects.
     * 
     * @param offerDtoList
     * @return A list of OfferDetail model objects.
     * @deprecated OfferModel removed as part of Packaging changes. Targeting April '18 release.
     */
    @Deprecated
    private List<OfferDetail> getOfferModelList(List<ModelOfferDto> offerDtoList) {

        List<OfferDetail> detailList = new ArrayList<OfferDetail>();

        if (offerDtoList != null) {
            for (ModelOfferDto dto : offerDtoList) {
                OfferDetail offerDetail = new OfferDetailImpl();
                offerDetail.setOfferId(dto.getOfferId());
                detailList.add(offerDetail);
            }
        }
        return detailList;
    }

    /**
     * Construct a list of ModelOfferDto from the specified modelPortfolioDetail model. This method should be used to convert
     * OfferDetail model to DTO objects for front-layer.
     * 
     * @param model
     * @return List of ModelOfferDTO objects.
     * @deprecated OfferModel removed as part of Packaging changes. Targeting April '18 release.
     */
    @Deprecated
    protected List<ModelOfferDto> getOfferDtoList(ModelPortfolioDetail model) {
        // Load the product details relevant to the specified ipsEnum to retrieve offer-name.
        if (Properties.getSafeBoolean("feature.model.tmpofferRemoval")) {
            return null;
        }
        List<ModelOfferDto> offerList = new ArrayList<>();
        if (userProfileService.isPortfolioManager()) {
            return offerList;
        }

        ModelType ipsEnum = ModelType.forCode(model.getAccountType());
        if (ipsEnum == null) {
            ipsEnum = ModelType.forId(model.getAccountType());
        }
        List<ModelOfferDto> dtoList = tmpOfferService.getModelOffers(userProfileService.getDealerGroupBroker().getKey(), ipsEnum,
                new ServiceErrorsImpl());

        List<OfferDetail> offerDetails = model.getOfferDetails();
        if (offerDetails != null && !offerDetails.isEmpty()) {
            for (OfferDetail detail : offerDetails) {
                for (ModelOfferDto d : dtoList) {
                    if (d.getOfferId().equals(detail.getOfferId())) {
                        offerList.add(d);
                        break;
                    }
                }
            }
        }
        return offerList;
    }
}