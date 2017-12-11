package com.bt.nextgen.api.fees.service;

import com.bt.nextgen.api.account.v1.model.AccountKey;
import com.bt.nextgen.api.fees.model.DollarFeeDto;
import com.bt.nextgen.api.fees.model.FeeScheduleDto;
import com.bt.nextgen.api.fees.model.FeesComponentDto;
import com.bt.nextgen.api.fees.model.FeesScheduleTrxnDto;
import com.bt.nextgen.api.fees.model.FeesTypeDto;
import com.bt.nextgen.api.fees.model.FeesTypeTrxnDto;
import com.bt.nextgen.api.fees.model.FlatPercentageFeeDto;
import com.bt.nextgen.api.fees.model.GlobalFeeDto;
import com.bt.nextgen.api.fees.model.InvestmentMgmtFeesDto;
import com.bt.nextgen.api.fees.model.IpsFeesTypeTrxnDto;
import com.bt.nextgen.api.fees.model.PercentageAssetDto;
import com.bt.nextgen.api.fees.model.PercentageFeeDto;
import com.bt.nextgen.api.fees.model.SlidingScaleFeeDto;
import com.bt.nextgen.api.fees.model.SlidingScaleFeeTierDto;
import com.bt.nextgen.api.fees.validation.FeesScheduleDtoErrorMapper;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.fees.AdminPercentageFeeComponent;
import com.bt.nextgen.service.avaloq.fees.DollarFeesComponent;
import com.bt.nextgen.service.avaloq.fees.FeeAssetDesc;
import com.bt.nextgen.service.avaloq.fees.FeesComponentType;
import com.bt.nextgen.service.avaloq.fees.FeesComponents;
import com.bt.nextgen.service.avaloq.fees.FeesMiscType;
import com.bt.nextgen.service.avaloq.fees.FeesScheduleImpl;
import com.bt.nextgen.service.avaloq.fees.FeesType;
import com.bt.nextgen.service.avaloq.fees.FlatPercentFeesComponent;
import com.bt.nextgen.service.avaloq.fees.GlobalFeesComponent;
import com.bt.nextgen.service.avaloq.fees.PercentageFeesComponent;
import com.bt.nextgen.service.avaloq.fees.ProductFlatPercentFeesComponent;
import com.bt.nextgen.service.avaloq.fees.ProductSlidingScaleFeesComponent;
import com.bt.nextgen.service.avaloq.fees.SlidingScaleFeesComponent;
import com.bt.nextgen.service.avaloq.fees.SlidingScaleTiers;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.fees.FeesSchedule;
import com.bt.nextgen.service.integration.fees.FeesScheduleIntegrationService;
import com.bt.nextgen.service.integration.fees.FeesScheduleTransaction;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementInterface;
import com.bt.nextgen.service.integration.ips.IpsFee;
import com.bt.nextgen.service.integration.ips.IpsIdentifier;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.ips.IpsTariff;
import com.bt.nextgen.service.integration.ips.IpsTariffBoundary;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.integration.account.SubAccount;
import com.btfin.panorama.service.integration.account.SubAccountIdentifier;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional(value = "springJpaTransactionManager")
// Sonar issues will be fixed in v2. Created technical story to refactor code
@SuppressWarnings("all")
public class FeeScheduleDtoServiceImpl implements FeeScheduleDtoService {
    @Autowired
    private FeesScheduleIntegrationService feesScheduleIntegrationService;

    @Autowired
    private FeesScheduleDtoErrorMapper feesScheduleDtoErrorMapper;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Autowired
    InvestmentPolicyStatementIntegrationService investmentPolicyStatementIntegrationService;

    @Autowired
    private StaticIntegrationService staticService;

    private static final String CONTRIBUTION_FEE = "Contribution fee";

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FeeScheduleDtoServiceImpl.class);

    @Override
    public FeeScheduleDto find(AccountKey key, ServiceErrors serviceErrors) {
        List<FeesSchedule> interfaceList = feesScheduleIntegrationService
                .getFees(new EncodedString(key.getAccountId()).plainText(), serviceErrors);
        List<FeesTypeDto> dtos = toFeesScheduleDto(interfaceList, serviceErrors);
        List<InvestmentMgmtFeesDto> investmentMgmtFees = getInvestmentMgmtFee(new EncodedString(key.getAccountId()).plainText(),
                serviceErrors);
        investmentMgmtFees.addAll(getPortfolioMgmtFee(interfaceList, serviceErrors));
        FeeScheduleDto feeScheduleDto = new FeeScheduleDto();
        feeScheduleDto.setKey(key);
        feeScheduleDto.setFees(dtos);
        feeScheduleDto.setInvestmentMgmtFees(investmentMgmtFees);
        return feeScheduleDto;
    }

    protected List<InvestmentMgmtFeesDto> getPortfolioMgmtFee(List<FeesSchedule> interfaceList, ServiceErrors serviceErrors) {
        List<InvestmentPolicyStatementInterface> ipsList = investmentPolicyStatementIntegrationService
                .loadInvestmentPolicyStatement(serviceErrors);
        List<InvestmentMgmtFeesDto> result = new ArrayList<>();
        for (FeesSchedule fee : interfaceList) {
            if (fee.getFeesType() == FeesType.PORTFOLIO_MANAGEMENT_FEE) {
                List<FeesComponents> componentModelList = fee.getFeesComponents();
                for (FeesComponents component : componentModelList) {
                    result.add(getImFeeDto(ipsList, component));
                }
            }
        }
        return result;
    }

    public List<InvestmentMgmtFeesDto> getInvestmentMgmtFee(String accountId, ServiceErrors serviceErrors) {
        List<InvestmentPolicyStatementInterface> ipsList = investmentPolicyStatementIntegrationService
                .loadInvestmentPolicyStatement(serviceErrors);
        WrapAccountDetail accountImpl = accountIntegrationService
                .loadWrapAccountDetail(com.bt.nextgen.service.integration.account.AccountKey.valueOf(accountId), serviceErrors);
        if (null != accountImpl)
            return toInvestMgmtDto(ipsList, accountImpl.getSubAccounts());
        else
            return new ArrayList<>();
    }

    /**
     *
     * @param ipsList
     *            List InvestmentPolicyStatementInterface
     * @param subAccounts
     *            List SubAccount
     * @return List InvestmentManagementFeesDto
     */
    private List<InvestmentMgmtFeesDto> toInvestMgmtDto(List<InvestmentPolicyStatementInterface> ipsList,
            List<SubAccount> subAccounts) {
        List<InvestmentMgmtFeesDto> investmentMgmtFees = new ArrayList<>();

        if (subAccounts != null && !subAccounts.isEmpty()) {
            for (SubAccount subAccount : subAccounts) {
                if (ContainerType.MANAGED_PORTFOLIO.equals(subAccount.getSubAccountType())) {

                    if (ipsList != null && !ipsList.isEmpty()) {

                        for (InvestmentPolicyStatementInterface ipsElement : ipsList) {

                            if (subAccount.getInvPolicySchemId() != null) {

                                if (subAccount.getInvPolicySchemId().getIpsKey().equals(ipsElement.getIpsKey())) {

                                    if (ipsElement.getFeeList() != null) {
                                        for (IpsFee ipsFeeDetail : ipsElement.getFeeList()) {
                                            if (ipsFeeDetail.getMasterBookKind().equals(FeesType.INVESTMENT_MANAGEMENT_FEE)) {
                                                InvestmentMgmtFeesDto investmentMgmtFeesDto = getImFeeDto(ipsFeeDetail);
                                                investmentMgmtFeesDto.setIpsId(ipsElement.getIpsKey().getId());
                                                investmentMgmtFeesDto.setCode(ipsElement.getCode());
                                                investmentMgmtFeesDto.setApirCode(ipsElement.getApirCode());
                                                investmentMgmtFeesDto.setInvestmentName(ipsElement.getInvestmentName());
                                                investmentMgmtFees.add(investmentMgmtFeesDto);
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
        return investmentMgmtFees;
    }

    private InvestmentMgmtFeesDto getImFeeDto(IpsFee ipsFeeDetail) {
        InvestmentMgmtFeesDto investmentMgmtFeesDto = new InvestmentMgmtFeesDto();
        investmentMgmtFeesDto.setFeeType(FeesType.INVESTMENT_MANAGEMENT_FEE);
        if (ipsFeeDetail.getTariffList() != null) {
            for (IpsTariff ipsTariff : ipsFeeDetail.getTariffList()) {
                if (ipsTariff.getTariffFactor() != null) {
                    investmentMgmtFeesDto.setPercent(ipsTariff.getTariffFactor().multiply(new BigDecimal(100)));
                }
                investmentMgmtFeesDto.setMinimumFee(ipsTariff.getMin());
                investmentMgmtFeesDto.setMaximumFee(ipsTariff.getMax());
                List<SlidingScaleFeeTierDto> slidingScaleFeeTierDtoList = new ArrayList<>();
                if (ipsTariff.getTariffBndList() != null) {
                    for (IpsTariffBoundary ipsTariffBound : ipsTariff.getTariffBndList()) {
                        SlidingScaleFeeTierDto slidingScaleFeeTierDto = new SlidingScaleFeeTierDto();
                        slidingScaleFeeTierDto.setLowerBound(ipsTariffBound.getBoundFrom());
                        slidingScaleFeeTierDto.setUpperBound(ipsTariffBound.getBoundTo());
                        if (ipsTariffBound.getTariffFactor() != null) {
                            slidingScaleFeeTierDto.setPercentage(ipsTariffBound.getTariffFactor().multiply(new BigDecimal(100)));
                        }

                        slidingScaleFeeTierDtoList.add(slidingScaleFeeTierDto);
                    }
                }

                investmentMgmtFeesDto.setSlidingScaleFeeTier(slidingScaleFeeTierDtoList);
            }
        }
        return investmentMgmtFeesDto;

    }

    private InvestmentMgmtFeesDto getImFeeDto(List<InvestmentPolicyStatementInterface> ipsList, FeesComponents component) {
        InvestmentPolicyStatementInterface ips = getIps(ipsList, ((IpsIdentifier) component).getIpsKey());
        InvestmentMgmtFeesDto investmentMgmtFeesDto = new InvestmentMgmtFeesDto();
        investmentMgmtFeesDto.setFeeType(FeesType.PORTFOLIO_MANAGEMENT_FEE);
        if (component.getFeesComponentType() == FeesComponentType.PERCENTAGE_FEE) {
            investmentMgmtFeesDto.setPercent(((FlatPercentFeesComponent) component).getRate().multiply(new BigDecimal(100)));
        } else if (component.getFeesComponentType() == FeesComponentType.SLIDING_SCALE_FEE) {
            investmentMgmtFeesDto.setSlidingScaleFeeTier(getTiers((SlidingScaleFeesComponent) component));
        }
        SubAccountKey subaccount = ((SubAccountIdentifier) component).getSubAccountKey();
        investmentMgmtFeesDto.setSubaccountId(EncodedString.fromPlainText(subaccount.getId()).toString());
        investmentMgmtFeesDto.setIpsId(ips.getIpsKey().getId());
        investmentMgmtFeesDto.setCode(ips.getCode());
        investmentMgmtFeesDto.setApirCode(ips.getApirCode());
        investmentMgmtFeesDto.setInvestmentName(ips.getInvestmentName());

        return investmentMgmtFeesDto;
    }

    protected List<FeesTypeDto> toFeesScheduleDto(List<FeesSchedule> feesScheduleInterface, ServiceErrors serviceErrors) {
        List<FeesTypeDto> feeScheduleDtoList = new ArrayList<>();
        FeesTypeDto ongoingDto = new FeesTypeDto();
        FeesTypeDto licenseeDto = null;
        FeesTypeDto adminDto = null;
        FeesTypeDto trusteeDto = null;
        FeesTypeDto smsfDto = null, smsfBdlDto = null;
        FeesTypeDto wrapAdvantageRebateDto = null;
        FeesTypeDto advanceManagedFundRebateDto = null;
        FeesTypeDto contributionFeeDto = null;

        ongoingDto.setType(Constants.ONGOING_FEE);

        for (FeesSchedule fees : feesScheduleInterface) {
            List<FeesComponents> componentModelList = fees.getFeesComponents();
            switch (fees.getFeesType()) {
                case ONGOING_FEE:
                    ongoingDto
                            .setFeesComponent(toFeesComponentDto(componentModelList, Constants.ONGOING_FEE_LABEL, serviceErrors));
                    break;
                case TRUSTEE_FEE:
                    trusteeDto = new FeesTypeDto();
                    trusteeDto.setType(Constants.TRUSTEE_FEE);
                    trusteeDto.setFeesComponent(
                            toAdminFeesComponentDto(componentModelList, Constants.TRUSTEE_FEE_LABEL, serviceErrors));
                    trusteeDto.setSpecialDiscount(fees.getDiscount());
                    break;
                case LICENSEE_FEE:
                    // LAF CR
                    if (componentModelList != null) {
                        if (licenseeDto == null) {
                            licenseeDto = new FeesTypeDto();
                            licenseeDto.setType(Constants.LICENSEE_FEE);

                        }
                        licenseeDto.setFeesComponent(
                                toFeesComponentDto(componentModelList, Constants.LICENSEE_FEE_LABEL, serviceErrors));
                    }
                    break;
                case ADMIN_FEE:
                    adminDto = new FeesTypeDto();
                    adminDto.setType(Constants.ADMIN_FEE);
                    adminDto.setFeesComponent(
                            toAdminFeesComponentDto(componentModelList, Constants.ADMIN_FEE_LABEL, serviceErrors));
                    populateDiscounts(adminDto.getFeesComponent(), adminDto);
                    break;
                case SMSF_FEE:
                    smsfDto = new FeesTypeDto();
                    smsfDto.setType(Constants.SMSF_FEE);
                    smsfDto.setFeesComponent(toFeesComponentDto(componentModelList, Constants.SMSF_FEE_LABEL, serviceErrors));
                    break;
                case SMSF_BDL_FEE:
                    smsfBdlDto = new FeesTypeDto();
                    smsfBdlDto.setType(Constants.SMSF_FEE);
                    smsfBdlDto.setFeesComponent(toFeesComponentDto(componentModelList, Constants.SMSF_FEE_LABEL, serviceErrors));
                    break;
                case WRAP_ADVANTAGE_REBATE:
                    wrapAdvantageRebateDto = new FeesTypeDto();
                    wrapAdvantageRebateDto.setType(Constants.WRAP_ADVANTAGE_REBATE);
                    wrapAdvantageRebateDto.setFeesComponent(
                            toFlatFeesComponentDto(componentModelList, Constants.WRAP_ADVANTAGE_REBATE, serviceErrors));
                    break;
                case ADVANCE_MANAGED_FUND_REBATE:
                    advanceManagedFundRebateDto = new FeesTypeDto();
                    advanceManagedFundRebateDto.setType(Constants.ADVANCE_MANAGED_FUND_REBATE);
                    advanceManagedFundRebateDto.setFeesComponent(
                            toFlatFeesComponentDto(componentModelList, Constants.ADVANCE_MANAGED_FUND_REBATE, serviceErrors));
                    break;
                case CONTRIBUTION_FEE:
                    contributionFeeDto = new FeesTypeDto();
                    contributionFeeDto.setType(CONTRIBUTION_FEE);
                    contributionFeeDto
                            .setFeesComponent(toFlatFeesComponentDto(componentModelList, CONTRIBUTION_FEE, serviceErrors));
                    break;
            }
        }
        if (ongoingDto != null) {
            feeScheduleDtoList.add(ongoingDto);
        }
        if (licenseeDto != null) {
            feeScheduleDtoList.add(licenseeDto);
        }
        if (smsfDto != null) {
            feeScheduleDtoList.add(smsfDto);
        }
        if (smsfBdlDto != null) {
            feeScheduleDtoList.add(smsfBdlDto);
        }
        if (adminDto != null) {
            feeScheduleDtoList.add(adminDto);
        }
        if (trusteeDto != null) {
            feeScheduleDtoList.add(trusteeDto);
        }
        if (wrapAdvantageRebateDto != null) {
            feeScheduleDtoList.add(wrapAdvantageRebateDto);
        }
        if (advanceManagedFundRebateDto != null) {
            feeScheduleDtoList.add(advanceManagedFundRebateDto);
        }
        if (contributionFeeDto != null) {
            feeScheduleDtoList.add(contributionFeeDto);
        }
        return feeScheduleDtoList;
    }

    protected List<FeesComponentDto> toAdminFeesComponentDto(List<FeesComponents> modelList, String feesType,
            ServiceErrors serviceErrors) {
        List<FeesComponentDto> componentDtoList = new ArrayList<>();
        if (modelList != null && !modelList.isEmpty()) {
            GlobalFeeDto globalFeeDto = new GlobalFeeDto();
            for (int i = 0; i < modelList.size(); i++) {
                FeesComponents feeModel = modelList.get(i);
                if (feeModel instanceof DollarFeesComponent) {
                    DollarFeeDto dollarFeeDto = new DollarFeeDto();
                    dollarFeeDto.setName(feesType.concat(dollarFeeDto.getType().toLowerCase()));
                    toDollarFeeDto((DollarFeesComponent) feeModel, dollarFeeDto);
                    componentDtoList.add(dollarFeeDto);
                } else if (feeModel instanceof AdminPercentageFeeComponent) {
                    PercentageFeeDto percentageFeeDto = new PercentageFeeDto();
                    toAdminPercentageFeeDto((AdminPercentageFeeComponent) feeModel, percentageFeeDto);
                    percentageFeeDto.setName(feesType.concat(percentageFeeDto.getType().toLowerCase()));
                    componentDtoList.add(percentageFeeDto);
                } else if (feeModel instanceof SlidingScaleFeesComponent) {
                    List<SlidingScaleFeeDto> slidingScaleDtoLst = new ArrayList<SlidingScaleFeeDto>();
                    SlidingScaleFeeDto slidingScaleDto = new SlidingScaleFeeDto();
                    if (Properties.getSafeBoolean("feature.adminfeeslidingscale")) {
                        slidingScaleDto
                                .setName(feesType.concat(slidingScaleDto.getType().toLowerCase()).concat(String.valueOf(i)));
                    } else {
                        slidingScaleDto.setName(feesType.concat(slidingScaleDto.getType().toLowerCase()));
                    }
                    toSlidingScaleFeeDto((SlidingScaleFeesComponent) feeModel, slidingScaleDto, serviceErrors);
                    slidingScaleDtoLst.add(slidingScaleDto);
                    componentDtoList.add(slidingScaleDto);
                } else if (feeModel instanceof GlobalFeesComponent) {
                    globalFeeDto.setName("Global Fee");
                    toGlobalFeeDto((GlobalFeesComponent) feeModel, globalFeeDto);
                    componentDtoList.add(globalFeeDto);
                }
            }
        }
        return componentDtoList;
    }

    protected List<FeesComponentDto> toFeesComponentDto(List<FeesComponents> modelList, String feesType,
            ServiceErrors serviceErrors) {
        List<FeesComponentDto> componentDtoList = new ArrayList<>();
        if (modelList != null && !modelList.isEmpty()) {
            for (FeesComponents feeModel : modelList) {
                if (feeModel instanceof DollarFeesComponent) {
                    DollarFeeDto dollarFeeDto = new DollarFeeDto();
                    dollarFeeDto.setName(feesType.concat(dollarFeeDto.getType().toLowerCase()));
                    toDollarFeeDto((DollarFeesComponent) feeModel, dollarFeeDto);
                    componentDtoList.add(dollarFeeDto);
                } else if (feeModel instanceof PercentageFeesComponent) {
                    PercentageFeeDto percentageFeeDto = new PercentageFeeDto();
                    toPercentageFeeDto((PercentageFeesComponent) feeModel, percentageFeeDto);
                    percentageFeeDto.setName(feesType.concat(percentageFeeDto.getType().toLowerCase()));
                    componentDtoList.add(percentageFeeDto);
                } else if (feeModel instanceof SlidingScaleFeesComponent) {
                    SlidingScaleFeeDto slidingScaleDto = new SlidingScaleFeeDto();
                    slidingScaleDto.setName(feesType.concat(slidingScaleDto.getType().toLowerCase()));
                    toSlidingScaleFeeDto((SlidingScaleFeesComponent) feeModel, slidingScaleDto, serviceErrors);
                    componentDtoList.add(slidingScaleDto);
                }
            }
        }
        return componentDtoList;
    }

    protected void toDollarFeeDto(DollarFeesComponent dollarModel, DollarFeeDto dollarDto) {
        String indexationDate = ApiFormatter.asMonthYear(dollarModel.getIndexation());
        dollarDto.setAmount(null != dollarModel.getDollar() ? dollarModel.getDollar() : dollarDto.getAmount());
        dollarDto.setDate(null != indexationDate && !"".equals(indexationDate) ? indexationDate : dollarDto.getDate());
        dollarDto.setLabel(Constants.DOLLAR_FEE);
        dollarDto.setCpiindex(dollarModel.isCpiindex() ? dollarModel.isCpiindex() : dollarDto.isCpiindex());
    }

    protected void toAdminPercentageFeeDto(AdminPercentageFeeComponent percentageModel, PercentageFeeDto percentageDto) {
        if (percentageModel != null) {
            if (percentageModel.getPercentMap() != null) {
                percentageDto.setLabel(Constants.PERCENTAGE_FEE);
                Set<FeesMiscType> keys = percentageModel.getPercentMap().keySet();
                for (FeesMiscType key : keys) {
                    PercentageAssetDto percentageMF = new PercentageAssetDto();
                    List<String> assetClasses = new ArrayList<>();
                    List<FeesMiscType> avaloqAssetClasses = percentageModel.getPercentMap().get(key).getAssetClass();
                    for (FeesMiscType assetClass : avaloqAssetClasses) {

                        assetClasses.add(FeeAssetDesc.getFeesAssetType(assetClass.getLabel()).getValue());
                    }
                    percentageMF.setAssetClasses(assetClasses);
                    percentageMF.setMaxFees(null != percentageModel.getPercentMap().get(key).getMaxFees()
                            ? percentageModel.getPercentMap().get(key).getMaxFees().setScale(2, BigDecimal.ROUND_UP)
                            : new BigDecimal(0).setScale(2));
                    percentageMF.setMinFees(null != percentageModel.getPercentMap().get(key).getMinFees()
                            ? percentageModel.getPercentMap().get(key).getMinFees().setScale(2, BigDecimal.ROUND_UP)
                            : new BigDecimal(0).setScale(2));
                    // percentageMF.setTailored(false);
                    percentageMF
                            .setTariffFactor(
                                    null != percentageModel.getPercentMap().get(key).getTariffFactor()
                                            ? percentageModel.getPercentMap().get(key).getTariffFactor()
                                                    .multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_UP)
                                            : new BigDecimal(0).setScale(2));
                    percentageMF.setIsTailored(percentageModel.getPercentMap().get(key).getIsTailored());
                    percentageDto.getListAssets().add(percentageMF);

                }
            }

        }
    }

    protected void toPercentageFeeDto(PercentageFeesComponent percentageModel, PercentageFeeDto percentageDto) {
        if (percentageModel != null) {
            percentageDto.setManagedFund(new BigDecimal(0).setScale(2));
            percentageDto.setShare(new BigDecimal(0).setScale(2));
            percentageDto.setManagedPortfolio(new BigDecimal(0).setScale(2));
            percentageDto.setTermDeposit(new BigDecimal(0).setScale(2));
            percentageDto.setCash(new BigDecimal(0).setScale(2));
            percentageDto.setMaximumFee(percentageModel.getMaxFees());
            percentageDto.setMinimumFee(percentageModel.getMinFees());
            if (percentageModel.getPercentMap() != null) {
                percentageDto.setLabel(Constants.PERCENTAGE_FEE);
                Set<FeesMiscType> keys = percentageModel.getPercentMap().keySet();
                for (FeesMiscType key : keys) {
                    switch (key) {
                        case PERCENT_MANAGED_FUND:
                            percentageDto.setManagedFund((percentageModel.getPercentMap().get(key).multiply(new BigDecimal(100)))
                                    .setScale(2, BigDecimal.ROUND_UP));
                            break;
                        case PERCENT_MANAGED_PORTFOLIO:
                            percentageDto
                                    .setManagedPortfolio((percentageModel.getPercentMap().get(key).multiply(new BigDecimal(100)))
                                            .setScale(2, BigDecimal.ROUND_UP));
                            break;
                        case PERCENT_TERM_DEPOSIT:
                            percentageDto.setTermDeposit((percentageModel.getPercentMap().get(key).multiply(new BigDecimal(100)))
                                    .setScale(2, BigDecimal.ROUND_UP));
                            break;
                        case PERCENT_CASH:
                            percentageDto.setCash((percentageModel.getPercentMap().get(key).multiply(new BigDecimal(100)))
                                    .setScale(2, BigDecimal.ROUND_UP));
                            break;
                        case PERCENT_SHARE:
                            percentageDto.setShare((percentageModel.getPercentMap().get(key).multiply(new BigDecimal(100)))
                                    .setScale(2, BigDecimal.ROUND_UP));
                            break;
                        default:
                            logger.info("Fee Misc Type not matched");
                    }
                }
            }
        }
    }

    /**
     *
     * @param slidingModel
     *            SlidingScaleFeesComponent
     * @return List SlidingScaleFeeTierDto
     */
    protected List<SlidingScaleFeeTierDto> getTiers(SlidingScaleFeesComponent slidingModel) {
        List<SlidingScaleFeeTierDto> tiers = new ArrayList<>();
        for (SlidingScaleTiers tier : slidingModel.getTiers()) {
            SlidingScaleFeeTierDto tierDto = new SlidingScaleFeeTierDto();
            tierDto.setLowerBound(tier.getLowerBound());
            tierDto.setUpperBound(tier.getUpperBound() == null ? null
                    : Constants.AVALOQ_NULL_FEE_UPPER_BOUND.compareTo(tier.getUpperBound()) == 0 ? null : tier.getUpperBound());
            tierDto.setPercentage(tier.getPercent().multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_UP));
            tiers.add(tierDto);
        }
        return tiers;
    }

    /**
     *
     * @param slidingModel
     *            SlidingScaleFeesComponent
     * @param slidingDto
     *            SlidingScaleFeeDto
     */
    protected void toSlidingScaleFeeDto(SlidingScaleFeesComponent slidingModel, SlidingScaleFeeDto slidingDto,
            ServiceErrors serviceErrors) {
        slidingDto.setSlidingScaleFeeTier(getTiers(slidingModel));
        Set<FeesMiscType> typeSet = new HashSet<>();

        if (slidingModel.getTransactionType() != null && !slidingModel.getTransactionType().isEmpty()) {
            for (FeesMiscType transaction : slidingModel.getTransactionType()) {
                typeSet.add(transaction);
                switch (transaction) {
                    case PERCENT_CASH:
                        slidingDto.setCash(true);
                        break;
                    case PERCENT_TERM_DEPOSIT:
                        slidingDto.setTermDeposit(true);
                        break;
                    case PERCENT_MANAGED_PORTFOLIO:
                        slidingDto.setManagedPortfolio(true);
                        break;
                    case PERCENT_MANAGED_FUND:
                        slidingDto.setManagedFund(true);
                        break;
                    case PERCENT_SHARE:
                        slidingDto.setShare(true);
                        break;
                    default:
                        logger.info("Fee Asset Type not matched");
                }
            }
        }

        slidingDto.setMaximumFee(slidingModel.getMaxFees());
        slidingDto.setMinimumFee(slidingModel.getMinFees());
        slidingDto.setLabel(Constants.SLIDING_SCALE_FEE);
        slidingDto.setSpclDiscount(slidingModel.getSpclDiscount());
        slidingDto.setAssetCount(typeSet.size());
    }

    @Override
    public FeeScheduleDto validate(FeeScheduleDto keyedObject, ServiceErrors serviceErrors) {
        return executeAvaloqOperation(keyedObject, serviceErrors, Constants.VALIDATE);
    }

    /**
     *
     * @param feeScheduleDto
     *            FeeScheduleDto
     * @param serviceErrors
     *            ServiceErrors
     * @param operation
     *            String
     * @return FeeScheduleDto
     */
    public FeeScheduleDto executeAvaloqOperation(FeeScheduleDto feeScheduleDto, ServiceErrors serviceErrors, String operation) {
        AccountKey key = feeScheduleDto.getKey();
        FeesScheduleTrxnDto trxDto = feeScheduleDto.getTransactionDto();
        FeesTypeTrxnDto ongoingFees = trxDto.getOnGoingFees();
        FeesTypeTrxnDto licenseeFees = trxDto.getLicenseeFees();
        List<IpsFeesTypeTrxnDto> portfolioFees = trxDto.getPortfolioFees();
        List<FeesTypeTrxnDto> contributionFees = trxDto.getContributionFees();

        List<FeesSchedule> feesList = new ArrayList<>();
        feesList.add(getOngoingFeesInterface(key, ongoingFees));
        if (licenseeFees != null) {
            feesList.add(getLicenseeFeesInterface(key, licenseeFees));
        }
        if (portfolioFees != null) {
            feesList.add(getPortfolioFeesInterface(key, portfolioFees));
        }
        if (contributionFees != null) {
            feesList.add(getContributionFeesInterface(key, contributionFees));
        }

        FeesScheduleTransaction feesScheduleTransaction;
        if (Constants.VALIDATE.equals(operation)) {
            feesScheduleTransaction = feesScheduleIntegrationService.validateFeeSchedule(feesList, serviceErrors);
        } else {
            feesScheduleTransaction = feesScheduleIntegrationService.submitFeeSchedule(feesList, serviceErrors);
        }
        return getFeesDto(feesScheduleTransaction, operation, serviceErrors);
    }

    /**
     *
     * @param feesScheduleTransactionImpl
     *            FeesScheduleTransactionImpl
     * @param operation
     *            String
     * @return FeeScheduleDto
     */
    protected FeeScheduleDto getFeesDto(FeesScheduleTransaction feesScheduleTransactionImpl, String operation,
            ServiceErrors serviceErrors) {
        List<FeesSchedule> interfaceList = feesScheduleTransactionImpl.getFeesScheduleInterfaceList();
        FeeScheduleDto feesDto = new FeeScheduleDto();
        feesDto.setFees(toFeesScheduleDto(interfaceList, serviceErrors));
        feesDto.setWarnings(feesScheduleDtoErrorMapper.map(feesScheduleTransactionImpl.getValidationErrors()));
        // TODO: Submit date should be returned from avaloq
        if (Constants.EXECUTE.equals(operation)) {
            feesDto.setSubmitDate(ApiFormatter.aestFormat(new Date(System.currentTimeMillis())));
        }
        return feesDto;
    }

    /**
     *
     * @param key
     *            AccountKey
     * @param licenseeFees
     *            FeesTypeTrxnDto
     * @return FeesSchedule
     */
    private FeesSchedule getOngoingFeesInterface(AccountKey key, FeesTypeTrxnDto licenseeFees) {
        FeesSchedule ongoingFeesInterface = createFeesScheduleInterface(key, licenseeFees);
        ongoingFeesInterface.setType(FeesType.ONGOING_FEE);
        return ongoingFeesInterface;
    }

    /**
     *
     * @param key
     *            AccountKey
     * @param ongoingFees
     *            FeesTypeTrxnDto
     * @return FeesSchedule
     */
    private FeesSchedule getLicenseeFeesInterface(AccountKey key, FeesTypeTrxnDto ongoingFees) {
        FeesSchedule licenseeFeesInterface = createFeesScheduleInterface(key, ongoingFees);
        licenseeFeesInterface.setType(FeesType.LICENSEE_FEE);
        return licenseeFeesInterface;
    }

    /**
     *
     * @param key
     *            AccountKey
     * @param contributionFees
     *            FeesTypeTrxnDto
     * @return FeesSchedule
     */
    protected FeesSchedule getContributionFeesInterface(AccountKey key, List<FeesTypeTrxnDto> contributionFees) {
        FeesScheduleImpl fees = new FeesScheduleImpl();
        List<FeesComponents> components = new ArrayList<>();
        for (FeesTypeTrxnDto fee : contributionFees) {
            if (fee.getDollarFee() != null) {
                components.add(toDollarFeesModel(fee.getDollarFee()));
            } else if (fee.getFlatPercentageFee() != null) {
                components.add(toFlatPercentageFeesModel(fee.getFlatPercentageFee()));
            }
        }

        fees.setAccountId(new EncodedString(key.getAccountId()).plainText());
        fees.setType(FeesType.CONTRIBUTION_FEE);
        fees.setFeesComponents(components);
        return fees;
    }

    /**
     *
     * @param key
     *            AccountKey
     * @param licenseeFees
     *            FeesTypeTrxnDto
     * @return FeesSchedule
     */
    private FeesSchedule createFeesScheduleInterface(AccountKey key, FeesTypeTrxnDto licenseeFees) {
        FeesSchedule feesInterface = new FeesScheduleImpl();
        tofeesComponentInterface(feesInterface, licenseeFees);
        feesInterface.setAccountId(new EncodedString(key.getAccountId()).plainText());
        return feesInterface;
    }

    /**
     * @param feesInterface
     *            FeesSchedule
     * @param fee
     *            FeesTypeTrxnDto
     */
    private void tofeesComponentInterface(FeesSchedule feesInterface, FeesTypeTrxnDto fee) {
        List<FeesComponents> feesComponents = new ArrayList<>();

        if (fee.getDollarFee() != null) {
            feesComponents.add(toDollarFeesModel(fee.getDollarFee()));
        }
        if (fee.getPercentageFee() != null) {
            feesComponents.add(toPercentageFeesModel(fee.getPercentageFee()));
        }
        if (fee.getSlidingScaleFee() != null) {
            feesComponents.add(toSlidingScaleFeesModel(fee.getSlidingScaleFee()));
        }
        feesInterface.setFeesComponents(feesComponents);
    }

    protected DollarFeesComponent toDollarFeesModel(DollarFeeDto dollarFeesDto) {
        DollarFeesComponent dollarFeesComponent = new DollarFeesComponent();
        dollarFeesComponent.setDollar(dollarFeesDto.getAmount());
        dollarFeesComponent.setCpiindex(dollarFeesDto.isCpiindex());
        dollarFeesComponent.setFeesComponentType(FeesComponentType.DOLLAR_FEE);
        if (StringUtils.isNotEmpty(dollarFeesDto.getName())) {
            FeesMiscType feesMiscType = FeesMiscType.forDisplay(dollarFeesDto.getName());
            if (feesMiscType != null) {
                dollarFeesComponent.setName(feesMiscType.getCode());
            }
        }
        return dollarFeesComponent;
    }

    /**
     * to convert PercentageFeeDto to interface
     */
    private PercentageFeesComponent toPercentageFeesModel(PercentageFeeDto percentageFeeDto) {
        PercentageFeesComponent percentageFeesComponent = new PercentageFeesComponent();
        Map<FeesMiscType, BigDecimal> percentageFeesMap = new HashMap<>();
        if (percentageFeeDto.getManagedFund() != null) {
            percentageFeesMap.put(FeesMiscType.PERCENT_MANAGED_FUND,
                    percentageFeeDto.getManagedFund().divide(new BigDecimal(100)));
        }
        percentageFeesMap.put(FeesMiscType.PERCENT_MANAGED_PORTFOLIO,
                percentageFeeDto.getManagedPortfolio().divide(new BigDecimal(100)));
        percentageFeesMap.put(FeesMiscType.PERCENT_TERM_DEPOSIT, percentageFeeDto.getTermDeposit().divide(new BigDecimal(100)));
        percentageFeesMap.put(FeesMiscType.PERCENT_CASH, percentageFeeDto.getCash().divide(new BigDecimal(100)));
        if (percentageFeeDto.getShare() != null) {
            percentageFeesMap.put(FeesMiscType.PERCENT_SHARE, percentageFeeDto.getShare().divide(new BigDecimal(100)));
        }
        percentageFeesComponent.setPercentMap(percentageFeesMap);
        return percentageFeesComponent;
    }

    /**
     * to convert FlatPercentageFeeDto to interface
     */
    private PercentageFeesComponent toFlatPercentageFeesModel(FlatPercentageFeeDto flatPercentageFeeDto) {
        PercentageFeesComponent percentageFeesComponent = new PercentageFeesComponent();
        Map<FeesMiscType, BigDecimal> percentageFeesMap = new HashMap<>();
        percentageFeesMap.put(FeesMiscType.forDisplay(flatPercentageFeeDto.getName()),
                flatPercentageFeeDto.getRate().divide(new BigDecimal(100)));
        percentageFeesComponent.setPercentMap(percentageFeesMap);
        return percentageFeesComponent;
    }

    /**
     *
     * @param scaleFeeDto
     *            SlidingScaleFeeDto
     * @return List <SlidingScaleTiers>
     */
    private List<SlidingScaleTiers> getTierList(SlidingScaleFeeDto scaleFeeDto) {
        List<SlidingScaleTiers> tiersList = new ArrayList<>();
        for (SlidingScaleFeeTierDto dto : scaleFeeDto.getSlidingScaleFeeTier()) {
            SlidingScaleTiers tiers = new SlidingScaleTiers();
            tiers.setLowerBound(dto.getLowerBound());
            tiers.setUpperBound(dto.getUpperBound());
            tiers.setPercent(dto.getPercentage().divide(new BigDecimal(100)));
            tiersList.add(tiers);
        }
        return tiersList;
    }

    /**
     * to convert SlidingScaleFeeDto to interface
     */
    private SlidingScaleFeesComponent toSlidingScaleFeesModel(SlidingScaleFeeDto scaleFeeDto) {
        SlidingScaleFeesComponent slidingScaleFeesComponent = new SlidingScaleFeesComponent();
        slidingScaleFeesComponent.setFeesComponentType(FeesComponentType.SLIDING_SCALE_FEE);
        slidingScaleFeesComponent.setMaxFees(scaleFeeDto.getMaximumFee());
        slidingScaleFeesComponent.setMinFees(scaleFeeDto.getMinimumFee());

        slidingScaleFeesComponent.setTiers(getTierList(scaleFeeDto));
        List<FeesMiscType> transactionType = new ArrayList<>();
        slidingScaleFeesComponent.setTransactionType(transactionType);
        // asset type
        if (scaleFeeDto.isCash()) {
            transactionType.add(FeesMiscType.PERCENT_CASH);
        }
        if (scaleFeeDto.isTermDeposit()) {
            transactionType.add(FeesMiscType.PERCENT_TERM_DEPOSIT);
        }
        if (scaleFeeDto.isManagedPortfolio()) {
            transactionType.add(FeesMiscType.PERCENT_MANAGED_PORTFOLIO);
        }
        if (scaleFeeDto.isManagedFund()) {
            transactionType.add(FeesMiscType.PERCENT_MANAGED_FUND);
        }
        if (scaleFeeDto.isShare()) {
            transactionType.add(FeesMiscType.PERCENT_SHARE);
        }
        return slidingScaleFeesComponent;
    }

    protected FeesSchedule getPortfolioFeesInterface(AccountKey key, List<IpsFeesTypeTrxnDto> portfolioFees) {
        FeesScheduleImpl fees = new FeesScheduleImpl();
        fees.setFeesType(FeesType.PORTFOLIO_MANAGEMENT_FEE);
        List<FeesComponents> components = new ArrayList<>();
        for (IpsFeesTypeTrxnDto ipsFee : portfolioFees) {
            FeesComponentType type = FeesComponentType.valueOf(ipsFee.getComponentType());
            if (type == FeesComponentType.SLIDING_SCALE_FEE) {
                components.add(toSlidingFeesComponent(ipsFee));
            } else if (type == FeesComponentType.PERCENTAGE_FEE) {
                components.add(toPercentageFeesComponent(ipsFee));
            }
        }
        fees.setFeesComponents(components);
        return fees;
    }

    private FeesComponents toPercentageFeesComponent(IpsFeesTypeTrxnDto ipsFee) {
        BigDecimal rate = ipsFee.getPercentage().divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
        return new ProductFlatPercentFeesComponent(SubAccountKey.valueOf(EncodedString.toPlainText(ipsFee.getSubaccountId())),
                IpsKey.valueOf(ipsFee.getIpsId()), rate);
    }

    private FeesComponents toSlidingFeesComponent(IpsFeesTypeTrxnDto ipsFee) {
        List<SlidingScaleTiers> tiers = new ArrayList<>();
        for (SlidingScaleFeeTierDto dto : ipsFee.getSlidingScaleFeeTier()) {
            BigDecimal rate = dto.getPercentage().divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
            SlidingScaleTiers tier = new SlidingScaleTiers(dto.getLowerBound(), dto.getUpperBound(), rate);
            tiers.add(tier);
        }
        return new ProductSlidingScaleFeesComponent(SubAccountKey.valueOf(EncodedString.toPlainText(ipsFee.getSubaccountId())),
                IpsKey.valueOf(ipsFee.getIpsId()), tiers);
    }

    /**
     * to convert DollarFeeDto to interface
     */
    @Override
    public FeeScheduleDto submit(FeeScheduleDto keyedObject, ServiceErrors serviceErrors) {
        return executeAvaloqOperation(keyedObject, serviceErrors, Constants.EXECUTE);
    }

    private void toGlobalFeeDto(GlobalFeesComponent globalFeesComponent, GlobalFeeDto globalFeeDto) {

        globalFeeDto.setMaximumFee(globalFeesComponent.getMaxFees());
        globalFeeDto.setMinimumFee(globalFeesComponent.getMinFees());
        globalFeeDto.setLabel("Global Fee");

        if (globalFeesComponent.getTransactionTypes() != null && !globalFeesComponent.getTransactionTypes().isEmpty()) {
            for (FeesMiscType transaction : globalFeesComponent.getTransactionTypes()) {
                switch (transaction) {
                    case PERCENT_CASH:
                        globalFeeDto.setCash(true);
                        break;
                    case PERCENT_TERM_DEPOSIT:
                        globalFeeDto.setTermDeposit(true);
                        break;
                    case PERCENT_MANAGED_PORTFOLIO:
                        globalFeeDto.setManagedPortfolio(true);
                        break;
                    case PERCENT_MANAGED_FUND:
                        globalFeeDto.setManagedFund(true);
                        break;
                    case PERCENT_SHARE:
                        globalFeeDto.setShare(true);
                        break;
                    default:
                        logger.info("Fee Asset Type not matched");
                }
            }
        }

    }

    private InvestmentPolicyStatementInterface getIps(List<InvestmentPolicyStatementInterface> ipsList, IpsKey key) {
        for (InvestmentPolicyStatementInterface ips : ipsList) {
            if (ips.getIpsKey().equals(key)) {
                return ips;
            }
        }
        return null;
    }

    private void populateDiscounts(List<FeesComponentDto> feesComponents, FeesTypeDto feesTypeDto) {
        // TODO this looks bugged. What if they don't have a sliding fee?
        for (FeesComponentDto feesComponentDto : feesComponents) {
            if (feesComponentDto instanceof SlidingScaleFeeDto) {
                SlidingScaleFeeDto slidingScaleFeeDto = (SlidingScaleFeeDto) feesComponentDto;
                if (null != slidingScaleFeeDto.getSpclDiscount()) {
                    feesTypeDto.setSpecialDiscount(slidingScaleFeeDto.getSpclDiscount());
                }
            }
        }
    }

    protected List<FeesComponentDto> toFlatFeesComponentDto(List<FeesComponents> modelList, String name,
            ServiceErrors serviceErrors) {
        List<FeesComponentDto> componentDtoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(modelList)) {
            for (FeesComponents feeModel : modelList) {
                if (feeModel instanceof DollarFeesComponent) {
                    DollarFeeDto dollarFeeDto = new DollarFeeDto();
                    dollarFeeDto.setAmount(((DollarFeesComponent) feeModel).getDollar());
                    dollarFeeDto.setLabel(Constants.DOLLAR_FEE);
                    dollarFeeDto.setName(((DollarFeesComponent) feeModel).getName());
                    componentDtoList.add(dollarFeeDto);
                } else if (feeModel instanceof FlatPercentFeesComponent) {
                    FlatPercentageFeeDto flatPercentageDto = new FlatPercentageFeeDto();
                    flatPercentageDto.setName(name);
                    flatPercentageDto.setLabel(Constants.PERCENTAGE_FEE);
                    flatPercentageDto.setRate(((FlatPercentFeesComponent) feeModel).getRate().multiply(new BigDecimal(100))
                            .setScale(2, BigDecimal.ROUND_UP));
                    componentDtoList.add(flatPercentageDto);
                } else if (feeModel instanceof PercentageFeesComponent) {
                    FlatPercentageFeeDto flatPercentageDto = new FlatPercentageFeeDto();
                    if (((PercentageFeesComponent) feeModel).getPercentMap() != null) {
                        flatPercentageDto.setLabel(Constants.PERCENTAGE_FEE);
                        Set<FeesMiscType> keys = ((PercentageFeesComponent) feeModel).getPercentMap().keySet();
                        for (FeesMiscType key : keys) {
                            switch (key) {
                                case EMPLOYER_CONTRIBUTION:
                                case REGULAR_DEPOSIT:
                                case REGULAR_PERSONAL_CONTRIBUTION:
                                case REGULAR_SPOUSE_CONTRIBUTION:
                                case ONEOFF_DEPOSIT:
                                case ONEOFF_PERSONAL_CONTRIBUTION:
                                case ONEOFF_SPOUSE_CONTRIBUTION:
                                    flatPercentageDto.setName(key.getDisplayName());
                                    flatPercentageDto.setRate(((PercentageFeesComponent) feeModel).getPercentMap().get(key)
                                            .multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_UP));
                                    break;
                                default:
                                    logger.info("Fee Misc Type not matched");
                            }
                        }
                        componentDtoList.add(flatPercentageDto);
                    }
                }
            }
        }
        return componentDtoList;
    }
}