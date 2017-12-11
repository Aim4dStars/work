package com.bt.nextgen.service.avaloq.transfer;

import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.transfer.InspecieAsset;
import com.bt.nextgen.service.integration.transfer.SponsorDetails;
import com.bt.nextgen.service.integration.transfer.TaxParcel;
import com.bt.nextgen.service.integration.transfer.TransferDetails;
import com.btfin.abs.trxservice.masssettle.v1_0.SettleRec;
import com.btfin.abs.trxservice.masssettle.v1_0.SettleRecList;
import com.btfin.abs.trxservice.masssettle.v1_0.Sponsor;
import com.btfin.abs.trxservice.masssettle.v1_0.TaxParcelList;

public final class TransferUtil {

    private TransferUtil() {

    }

    public static Sponsor toSponsor(TransferDetails transferDetails) {
        Sponsor sponsor = AvaloqObjectFactory.getMassSettleObjectFactory().createSponsor();
        SponsorDetails details = transferDetails.getSponsorDetails();
        if (details.getSponsorId() != null) {
            sponsor.setDestBankBp(AvaloqGatewayUtil.createIdVal(details.getSponsorId()));
        }

        if (details.getInvestmentId() != null) {
            sponsor.setDestBenefText(AvaloqGatewayUtil.createTextVal(details.getInvestmentId()));
        }

        if (details.getPlatformId() != null) {
            sponsor.setDestBankBpText(AvaloqGatewayUtil.createTextVal(details.getPlatformId()));
        }

        if (details.getRegistrationDetails() != null) {
            sponsor.setRegDet(AvaloqGatewayUtil.createTextVal(details.getRegistrationDetails()));
        }
        return sponsor;
    }

    public static SettleRecList toSettlementRecordList(TransferDetails transferDetails) {
        SettleRecList settleList = AvaloqObjectFactory.getMassSettleObjectFactory().createSettleRecList();
        for (InspecieAsset asset : transferDetails.getTransferAssets()) {
            SettleRec rec = AvaloqObjectFactory.getMassSettleObjectFactory().createSettleRec();
            if (asset.getAssetId() != null) {
                rec.setAsset(AvaloqGatewayUtil.createIdVal(asset.getAssetId()));
            }
            if (asset.getHoldingId() != null) {
                rec.setPos(AvaloqGatewayUtil.createIdVal(asset.getHoldingId()));
            }
            rec.setQty(AvaloqGatewayUtil.createNumberVal(asset.getQuantity()));

            settleList.getSettleRec().add(rec);
        }

        return settleList;
    }

    public static TaxParcelList toTaxParcelList(TransferDetails transferDetails) {
        if (transferDetails != null && transferDetails.getTaxParcels() != null && !transferDetails.getTaxParcels().isEmpty()) {
            TaxParcelList list = AvaloqObjectFactory.getMassSettleObjectFactory().createTaxParcelList();
            for (TaxParcel tax : transferDetails.getTaxParcels()) {
                list.getTaxParcel().add(getTaxParcel(tax));
            }
            return list;
        }
        return null;
    }

    private static com.btfin.abs.trxservice.masssettle.v1_0.TaxParcel getTaxParcel(TaxParcel tax) {
        com.btfin.abs.trxservice.masssettle.v1_0.TaxParcel parcel = AvaloqObjectFactory.getMassSettleObjectFactory()
                .createTaxParcel();
        parcel.setAsset(AvaloqGatewayUtil.createIdVal(tax.getAssetId()));
        parcel.setQty(AvaloqGatewayUtil.createNumberVal(tax.getQuantity()));
        parcel.setCurry(AvaloqGatewayUtil.createExtlIdVal(CurrencyType.AustralianDollar.getCurrency()));

        if (tax.getRelevanceDate() != null) {
            parcel.setTaxRelvDate(AvaloqGatewayUtil.createDateVal(tax.getRelevanceDate().toDate()));
        }
        if (tax.getVisibilityDate() != null) {
            parcel.setTaxVisibDate(AvaloqGatewayUtil.createDateVal(tax.getVisibilityDate().toDate()));
        }
        if (tax.getCostBase() != null) {
            parcel.setImportCostBase(AvaloqGatewayUtil.createNumberVal(tax.getCostBase()));
        }
        if (tax.getOriginalCostBase() != null) {
            parcel.setOrigCostBase(AvaloqGatewayUtil.createNumberVal(tax.getOriginalCostBase()));
        }
        if (tax.getReducedCostBase() != null) {
            parcel.setImportReduCostBase(AvaloqGatewayUtil.createNumberVal(tax.getReducedCostBase()));
        }
        if (tax.getIndexedCostBase() != null) {
            parcel.setImportIdxCostBase(AvaloqGatewayUtil.createNumberVal(tax.getIndexedCostBase()));
        }
        return parcel;
    }
}
