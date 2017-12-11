package com.bt.nextgen.service.avaloq.transfer.transfergroup;

import com.avaloq.abs.bb.fld_def.NrFld;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.avaloq.transaction.TransactionValidationConverter;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.order.ModelPreferenceAction;
import com.bt.nextgen.service.integration.transfer.BeneficialOwnerChangeStatus;
import com.bt.nextgen.service.integration.transfer.SponsorDetails;
import com.bt.nextgen.service.integration.transfer.TaxParcel;
import com.bt.nextgen.service.integration.transfer.transfergroup.TransferAsset;
import com.bt.nextgen.service.integration.transfer.transfergroup.TransferGroupDetails;
import com.btfin.abs.trxservice.xferbdl.v1_0.AssetList;
import com.btfin.abs.trxservice.xferbdl.v1_0.MpPrefItem;
import com.btfin.abs.trxservice.xferbdl.v1_0.MpPrefList;
import com.btfin.abs.trxservice.xferbdl.v1_0.TaxParcelList;
import com.btfin.abs.trxservice.xferbdl.v1_0.XferType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class GroupTransferAssetHelper {

    @Autowired
    protected TransactionValidationConverter validationConverter;

    public XferType toTransferType(TransferGroupDetails transferDetails) {
        XferType transferType = AvaloqObjectFactory.getTransferGroupObjectFactory().createXferType();
        transferType.setInSpecieXferTypeId(AvaloqGatewayUtil.createExtlIdVal(transferDetails.getExternalTransferType().getCode()));
        if (transferDetails.getChangeOfBeneficialOwnership() != null) {
            transferType.setXferChgBenefId(AvaloqGatewayUtil
                    .createExtlIdVal(transferDetails.getChangeOfBeneficialOwnership().getCode()));
        }
        return transferType;
    }

    public AssetList setTransferAssetList(TransferGroupDetails transferDetails) {
        // set asset-list, tax_parcel
        AssetList assetList = AvaloqObjectFactory.getTransferGroupObjectFactory().createAssetList();
        if (transferDetails.getTransferAssets() == null) {
            return assetList;
        }
        for (TransferAsset tfrAsset : transferDetails.getTransferAssets()) {
            com.btfin.abs.trxservice.xferbdl.v1_0.Asset asset = AvaloqObjectFactory.getTransferGroupObjectFactory().createAsset();
            asset.setAssetId(AvaloqGatewayUtil.createIdVal(tfrAsset.getAssetId()));
            asset.setQty(AvaloqGatewayUtil.createNumberVal(tfrAsset.getQuantity()));
            asset.setIsBtCashXfer(AvaloqGatewayUtil.createBoolVal(tfrAsset.getIsCashTransfer()));

            // set sponsor details.
            if (tfrAsset.getSponsorDetails() != null) {
                SponsorDetails spDetail = tfrAsset.getSponsorDetails();
                asset.setDestBenefText(AvaloqGatewayUtil.createTextVal(getBenefText(spDetail)));

                if (spDetail.getSponsorId() != null) {
                    asset.setPidId(AvaloqGatewayUtil.createExtlIdVal(spDetail.getSponsorId()));
                }

                // There values are only set if non-null.
                // Otherwise, Avaloq throws a FATAL exception.
                if (spDetail.getPlatformId() != null) {
                    asset.setCustodian(AvaloqGatewayUtil.createTextVal(spDetail.getPlatformId()));
                }
            }

            // Set TaxParcel details
            if (BeneficialOwnerChangeStatus.YES != transferDetails.getChangeOfBeneficialOwnership()) {
                asset.setTaxParcelList(toTaxParcelList(tfrAsset));
            }

            assetList.getAsset().add(asset);
        }
        return assetList;
    }

    public TaxParcelList toTaxParcelList(TransferAsset transferAsset) {

        if (transferAsset != null && transferAsset.getTaxParcels() != null && !transferAsset.getTaxParcels().isEmpty()) {
            TaxParcelList list = AvaloqObjectFactory.getTransferGroupObjectFactory().createTaxParcelList();
            for (TaxParcel tax : transferAsset.getTaxParcels()) {
                com.btfin.abs.trxservice.xferbdl.v1_0.TaxParcel parcel = AvaloqObjectFactory.getTransferGroupObjectFactory()
                        .createTaxParcel();
                parcel.setTaxRelvDate(tax.getRelevanceDate() != null ? AvaloqGatewayUtil.createDateVal(tax.getRelevanceDate().toDate())
                        : null);
                parcel.setTaxVisibDate(tax.getVisibilityDate() != null ? AvaloqGatewayUtil.createDateVal(tax.getVisibilityDate()
                        .toDate()) : null);
                parcel.setTaxParcelQty(getNrFld(tax.getQuantity()));
                parcel.setImportCostBase(getNrFld(tax.getCostBase()));
                parcel.setOrigCostBase(getNrFld(tax.getOriginalCostBase()));
                parcel.setImportReduCostBase(getNrFld(tax.getReducedCostBase()));
                parcel.setImportIdxCostBase(getNrFld(tax.getIndexedCostBase()));
                parcel.setCurry(AvaloqGatewayUtil.createExtlIdVal(CurrencyType.AustralianDollar.getCurrency()));

                list.getTaxParcel().add(parcel);
            }
            return list;
        }

        return null;
    }

    /**
     * Translate all portfolio-preferences in the specified transferDetails to a list of MpPrefItem.
     * 
     * @param transferDetails
     * @return
     */
    public MpPrefList setPreference(TransferGroupDetails transferDetails) {
        // set mp-pref-list
        if (transferDetails.getPreferenceList() != null && !transferDetails.getPreferenceList().isEmpty()) {
            MpPrefList mpList = AvaloqObjectFactory.getTransferGroupObjectFactory().createMpPrefList();
            for (ModelPreferenceAction modelPref : transferDetails.getPreferenceList()) {
                MpPrefItem mpItem = AvaloqObjectFactory.getTransferGroupObjectFactory().createMpPrefItem();
                mpItem.setIssuerId(AvaloqGatewayUtil.createIdVal(modelPref.getIssuerKey().getId()));
                mpItem.setPrefActionId(AvaloqGatewayUtil.createExtlIdVal(modelPref.getAction().toString()));
                mpItem.setPrefTypeId(AvaloqGatewayUtil.createExtlIdVal(modelPref.getPreference().toString()));
                mpList.getMpPrefItem().add(mpItem);
            }
            return mpList;
        }
        return null;
    }

    /**
     * Create an instance of NrFld is the value specified is not NULL.
     * 
     * @param value
     * @return
     */
    private NrFld getNrFld(BigDecimal value) {
        if (value != null) {
            return AvaloqGatewayUtil.createNumberVal(value);
        }
        return null;
    }

    private String getBenefText(SponsorDetails spDetail) {
        if (spDetail != null) {
            if (spDetail.getInvestmentId() != null) {
                return spDetail.getInvestmentId();
            }

            if (spDetail.getRegistrationDetails() != null) {
                return spDetail.getRegistrationDetails();
            }

            if (spDetail.getSourceContainerId() != null) {
                return spDetail.getSourceContainerId();
            }
        }
        return null;
    }
}
