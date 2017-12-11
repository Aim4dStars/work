package com.bt.nextgen.api.superpersonaltaxdeduction.util;

import com.bt.nextgen.api.superpersonaltaxdeduction.model.NoticeDetailsDto;
import com.bt.nextgen.api.superpersonaltaxdeduction.model.PersonalDeductionNoticesDto;
import com.bt.nextgen.api.superpersonaltaxdeduction.model.PersonalTaxDeductionNoticeTrxnDto;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.bt.nextgen.service.avaloq.AvaloqUtils;
import com.bt.nextgen.service.avaloq.superpersonaltaxdeduction.PersonalTaxDeduction;
import com.bt.nextgen.service.avaloq.superpersonaltaxdeduction.PersonalTaxDeductionNotices;
import com.btfin.abs.trxservice.ausa.v1_0.ActionType;
import com.btfin.abs.trxservice.ausa.v1_0.AuSaReq;
import com.btfin.abs.trxservice.ausa.v1_0.AuSaRsp;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ReverseComparator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Converter for Personal Tax Deduction Notices
 */
public class PersonalTaxDeductionNoticesConverter {
    private static final String VARIATION_NOTICE = "Variation notice";
    private static final String DEFAULT_NOTICE = "Notice";
    private static final String NOTICE_CREATE_STATUS = "Processed";
    private static final String SAVED = "saved";
    private static final String NOT_SAVED = "notSaved";


    // Set Personal Tax Deduction Response
    public PersonalTaxDeductionNoticeTrxnDto toPersonalTaxResponseDto(AuSaRsp taxResponse) {
        final PersonalTaxDeductionNoticeTrxnDto taxTrxDto = new PersonalTaxDeductionNoticeTrxnDto();
        final String processingInfo = taxResponse.getData().getPrcInfo().getVal();

        taxTrxDto.setTransactionStatus(processingInfo != null && NOTICE_CREATE_STATUS.equals(processingInfo) ? SAVED : NOT_SAVED);

        return taxTrxDto;
    }

    //Make tax deduction request
    public AuSaReq makePersonalTaxDeductionRequest(String accountNumber, String originalDocId, Date startDate,
                                                   Date endDate, BigDecimal claimAmount) {
        final AuSaReq taxReq = AvaloqObjectFactory.getAuSaObjectFactory().createAuSaReq();
        final com.btfin.abs.trxservice.ausa.v1_0.Data data = AvaloqObjectFactory.getAuSaObjectFactory().createData();

        data.setBpId(AvaloqGatewayUtil.createExtlId(accountNumber, "bp_nr"));
        data.setClaimAmountTc(AvaloqGatewayUtil.createNumberVal(claimAmount));
        data.setValidTo(AvaloqGatewayUtil.createDateVal(endDate));
        data.setValidFrom(AvaloqGatewayUtil.createDateVal(startDate));
        data.setAction(ActionType.DEDUCT_NOTICE);

        if (originalDocId != null) {
            data.setRefDocId(AvaloqGatewayUtil.createNumberVal(originalDocId));
        }

        taxReq.setHdr(AvaloqGatewayUtil.createHdr());
        taxReq.setData(data);
        taxReq.setReq(AvaloqUtils.createTransactionServiceExecuteReq());

        return taxReq;
    }


    /**
     * Get a DTO version of Personal Notices.
     *
     * @param personalTaxDeduction PersonalTaxDeduction to convert to DTO.
     *
     * @return DTO version of Personal Tax Deduction Notices
     */
    public static PersonalDeductionNoticesDto getAsPersonalDeductionNoticesDto(final PersonalTaxDeduction personalTaxDeduction) {
        final PersonalDeductionNoticesDto noticesDto = new PersonalDeductionNoticesDto();

        noticesDto.setTotalNotifiedAmount(BigDecimal.ZERO);
        if (personalTaxDeduction != null) {
            processNotices(personalTaxDeduction, noticesDto);
        }

        return noticesDto;
    }


    private static void processNotices(PersonalTaxDeduction personalTaxDeduction, PersonalDeductionNoticesDto noticesDto) {
        final List<PersonalTaxDeductionNotices> notices = personalTaxDeduction.getTaxDeductionNotices();
        // use LinkedHashMap to guarantee order of iteration (later)
        final LinkedHashMap<Long, NoticeDetailsDto> noticeDetailsDtoMap = new LinkedHashMap<>();

        // sort using docId to guarantee chronological order
        Collections.sort(notices, new ReverseComparator(new BeanComparator("docId")));

        for (PersonalTaxDeductionNotices notice : notices) {
            final Long key = notice.getRefDocId() != null ? notice.getRefDocId() : notice.getDocId();
            NoticeDetailsDto noticeDetailsDto = noticeDetailsDtoMap.get(key);

            // latest notice/variation for an original docId
            if (noticeDetailsDto == null) {
                noticeDetailsDto = createNoticeDetail(notice);
                noticeDetailsDtoMap.put(key, noticeDetailsDto);
                noticesDto.setTotalNotifiedAmount(noticesDto.getTotalNotifiedAmount().add(noticeDetailsDto.getNoticeAmount()));
            }
            // link previous notice/variation to the latest variation
            else {
                noticeDetailsDto.updatePastNotices(createNoticeDetail(notice));
            }
        }

        noticesDto.setNotices(new ArrayList<NoticeDetailsDto>(noticeDetailsDtoMap.values()));
    }


    private static NoticeDetailsDto createNoticeDetail(PersonalTaxDeductionNotices notice) {
        final NoticeDetailsDto noticeDto = new NoticeDetailsDto();

        noticeDto.setDocId(notice.getDocId().toString());
        // amount is negative from Avaloq
        noticeDto.setNoticeAmount(notice.getNoticeAmount().abs());
        noticeDto.setNoticeDate(notice.getNoticeDate());

        if (notice.getUnalterableNoticeAmount() != null) {
            noticeDto.setUnalterableNoticeAmount(notice.getUnalterableNoticeAmount().abs());
        }

        if (notice.getIsVarNotice()) {
            noticeDto.setNoticeType(VARIATION_NOTICE);
            noticeDto.setOriginalDocId(notice.getRefDocId().toString());
        }
        else {
            noticeDto.setNoticeType(DEFAULT_NOTICE);
        }

        return noticeDto;
    }
}
