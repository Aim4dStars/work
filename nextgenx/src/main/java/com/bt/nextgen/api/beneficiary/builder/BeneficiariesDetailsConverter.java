package com.bt.nextgen.api.beneficiary.builder;

import com.bt.nextgen.api.beneficiary.model.Beneficiary;
import com.bt.nextgen.api.beneficiary.model.BeneficiaryTrxnDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.util.StringUtil;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.bt.nextgen.service.avaloq.AvaloqUtils;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.avaloq.beneficiary.*;
import com.bt.nextgen.service.integration.TransactionStatus;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.domain.Gender;
import com.btfin.abs.trxservice.base.v1_0.Req;
import com.btfin.abs.trxservice.bp.v1_0.BpReq;
import com.btfin.abs.trxservice.bp.v1_0.SABenefDet;
import com.btfin.abs.trxservice.bp.v1_0.SABenefDetList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by L067218 on 14/07/2016.
 */
public class BeneficiariesDetailsConverter {

    // Add transaction status to BeneficiaryTrxnDto
    public static BeneficiaryTrxnDto toBeneficiariesDetailsResponseDto(TransactionStatus status, BeneficiaryTrxnDto benefTransactionDto)
    {
        benefTransactionDto.setTransactionStatus(status.isSuccessful() ? "saved" : "notSaved");

        return benefTransactionDto;
    }

    // Convert from {@link BeneficiaryTrxnDto} to {@link SaveBeneficiariesDetails}
    public static SaveBeneficiariesDetails toBeneficiaryDetails(BeneficiaryTrxnDto beneficiaryTrxnDto, WrapAccountDetail accountDetail)
    {
        SaveBeneficiariesDetails benefDetails = new SaveBeneficiariesDetailsImpl();
        benefDetails.setAccountKey(beneficiaryTrxnDto.getKey());
        List<BeneficiaryDetails> benefList= new ArrayList<>();
        if(!beneficiaryTrxnDto.getBeneficiaries().isEmpty()) {
            for (Beneficiary dto : beneficiaryTrxnDto.getBeneficiaries()) {
                BeneficiaryDetails benef = new BeneficiaryDetailsImpl();
                benef.setNominationTypeinAvaloqFormat(dto.getNominationType());
                benef.setRelationshipType(RelationshipType.findByAvaloqId(dto.getRelationshipType()));
                benef.setAllocationPercent(new BigDecimal(dto.getAllocationPercent().replaceAll(",", "")));
                benef.setFirstName(dto.getFirstName());
                benef.setLastName(dto.getLastName());
                if( StringUtil.isNotNullorEmpty(dto.getDateOfBirth())){
                benef.setDateOfBirth(ApiFormatter.parseDate(dto.getDateOfBirth()));
                }
                if(StringUtil.isNotNullorEmpty(dto.getGender())) {
                    benef.setGender(Gender.valueOf(dto.getGender().toUpperCase()));
                }
                benef.setPhoneNumber(dto.getPhoneNumber());
                benef.setEmail(dto.getEmail());

                benefList.add(benef);
            }
        }
        benefDetails.setBeneficiaries(benefList);
        benefDetails.setModificationSeq(accountDetail.getModificationSeq());
        return benefDetails;
    }

    // Convert from {@link SaveBeneficiariesDetails} to {@link BpReq}
    public static BpReq makeBeneficiariesRequest(SaveBeneficiariesDetails request) {

        BpReq bpReq = AvaloqObjectFactory.getBprequestfactory().createBpReq();
        bpReq.setHdr(AvaloqGatewayUtil.createHdr());

        com.btfin.abs.trxservice.bp.v1_0.Data data = AvaloqObjectFactory.getBprequestfactory().createData();
        com.btfin.abs.trxservice.bp.v1_0.Action action = AvaloqObjectFactory.getBprequestfactory().createAction();
        data.setBp(AvaloqGatewayUtil.createIdVal(new EncodedString(request.getAccountKey().getAccountId()).plainText()));
        data.setModiSeqNr(AvaloqGatewayUtil.createNumberVal(request.getModificationSeq()));
        SABenefDetList benefList = new SABenefDetList();
        setBeneficiariesList(benefList, action, request);

        data.setAction(action);
        bpReq.setData(data);
        Req req = AvaloqUtils.createTransactionServiceExecuteReq();
        bpReq.setReq(req);
        return bpReq;
    }

    //Set beneficiary list in avaloq request
    public static void setBeneficiariesList(SABenefDetList benefList, com.btfin.abs.trxservice.bp.v1_0.Action action, SaveBeneficiariesDetails request){
        if(request.getBeneficiaries().isEmpty()) {
            action.setIsDelSaBenefDet(AvaloqGatewayUtil.createBoolVal(true));
        }
        else{
            for (BeneficiaryDetails beneficiary : request.getBeneficiaries()) {
                SABenefDet benefDet = new SABenefDet();
                benefDet.setNomnType(AvaloqGatewayUtil.createExtlIdVal(beneficiary.getNominationTypeinAvaloqFormat()));
                benefDet.setRelType(AvaloqGatewayUtil.createExtlIdVal(beneficiary.getRelationshipType().getAvaloqInternalId()));
                if(beneficiary.getGender()!=null) {
                    benefDet.setGender(AvaloqGatewayUtil.createExtlIdVal(beneficiary.getGender().toString()));
                }
                benefDet.setCtactNr(AvaloqGatewayUtil.createTextVal(beneficiary.getPhoneNumber()));
                if(beneficiary.getDateOfBirth()!=null) {
                    benefDet.setDob(AvaloqGatewayUtil.createDateVal(beneficiary.getDateOfBirth().toDate()));
                }
                benefDet.setEmail(AvaloqGatewayUtil.createTextVal(beneficiary.getEmail()));
                benefDet.setFirstName(AvaloqGatewayUtil.createTextVal(beneficiary.getFirstName()));
                benefDet.setLastName(AvaloqGatewayUtil.createTextVal(beneficiary.getLastName()));
                benefDet.setPct(AvaloqGatewayUtil.createNumberVal(beneficiary.getAllocationPercent()));
                benefList.getSaBenefDet().add(benefDet);
            }
            action.getUpdSaBenefDet().add(benefList);
        }
    }
}
