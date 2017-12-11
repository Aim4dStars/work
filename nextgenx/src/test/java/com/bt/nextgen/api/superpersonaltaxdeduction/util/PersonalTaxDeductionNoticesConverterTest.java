package com.bt.nextgen.api.superpersonaltaxdeduction.util;

import com.bt.nextgen.api.superpersonaltaxdeduction.model.NoticeDetailsDto;
import com.bt.nextgen.api.superpersonaltaxdeduction.model.NoticeDto;
import com.bt.nextgen.api.superpersonaltaxdeduction.model.PersonalDeductionNoticesDto;
import com.bt.nextgen.api.superpersonaltaxdeduction.model.PersonalTaxDeductionNoticeTrxnDto;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.bt.nextgen.service.avaloq.superpersonaltaxdeduction.PersonalTaxDeduction;
import com.bt.nextgen.service.avaloq.superpersonaltaxdeduction.PersonalTaxDeductionNotices;
import com.btfin.abs.trxservice.ausa.v1_0.ActionType;
import com.btfin.abs.trxservice.ausa.v1_0.AuSaReq;
import com.btfin.abs.trxservice.ausa.v1_0.AuSaRsp;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@RunWith(MockitoJUnitRunner.class)
public class PersonalTaxDeductionNoticesConverterTest {

    String accountNumber = "";
    AuSaRsp taxResponse = new AuSaRsp();
    com.btfin.abs.trxservice.ausa.v1_0.Data data;

    @Before
    public void init() {
        accountNumber = "400002374";
    }

    PersonalTaxDeductionNoticesConverter converter;
    @Test
    public void getAsPersonalDeductionNoticesDto() {
        final List<PersonalTaxDeductionNotices> notices = new ArrayList<PersonalTaxDeductionNotices>();
        final PersonalTaxDeduction personalTaxDeduction = makeTaxDeduction(notices);
        final PersonalTaxDeductionNotices notice1a = makeNotice(1000L, null, new DateTime(2016, 8, 4, 10, 11), "-1500.00", "-2000.00", false);
        final PersonalTaxDeductionNotices notice1b = makeNotice(1100L, 1000L, new DateTime(2016, 8, 5, 10, 11), "-1400.00", "-2000.00", true);
        final PersonalTaxDeductionNotices notice2a = makeNotice(200L, null, new DateTime(2016, 8, 3, 10, 11), "-1300.00", "-3000.00", false);
        final PersonalTaxDeductionNotices notice2b = makeNotice(820L, 200L, new DateTime(2016, 8, 3, 10, 11), "-1200.00", "-3000.00", true);
        final PersonalTaxDeductionNotices notice2c = makeNotice(920L, 200L, new DateTime(2016, 8, 3, 10, 11), "-1100.00", "-3000.00", true);
        final PersonalTaxDeductionNotices notice3a = makeNotice(3000L, null, new DateTime(2016, 8, 6, 10, 11), "-6000.00", null, false);
        final PersonalDeductionNoticesDto result;
        int dtoIndex;
        int pnIndex;
        NoticeDetailsDto noticeDetailsDto;
        NoticeDto pastNotice;

        // add notices in mixed order
        notices.add(notice2a);
        notices.add(notice1a);
        notices.add(notice2c);
        notices.add(notice2b);
        notices.add(notice3a);
        notices.add(notice1b);

        result = PersonalTaxDeductionNoticesConverter.getAsPersonalDeductionNoticesDto(personalTaxDeduction);
        assertThat("result", result, notNullValue());
        assertThat("total amount", result.getTotalNotifiedAmount(), equalTo(new BigDecimal("8500.00")));
        assertThat("number of notices", result.getNotices().size(), equalTo(3));


        // DTO[0]
        dtoIndex = 0;
        noticeDetailsDto = result.getNotices().get(dtoIndex);
        checkNotice("noticeDetails[" + dtoIndex + "]", "Notice", noticeDetailsDto, notice3a);
        assertThat("noticeDetails[" + dtoIndex + "] - pastNotices", noticeDetailsDto.getPastNotices(), nullValue());


        // DTO[1]
        dtoIndex = 1;
        noticeDetailsDto = result.getNotices().get(dtoIndex);
        checkNotice("noticeDetails[" + dtoIndex + "]", "Variation notice", noticeDetailsDto, notice1b);
        assertThat("noticeDetails[" + dtoIndex + "] - pastNotices", noticeDetailsDto.getPastNotices(), notNullValue());
        assertThat("noticeDetails[" + dtoIndex + "] - pastNotices size", noticeDetailsDto.getPastNotices().size(), equalTo(1));

        pnIndex = 0;
        pastNotice = noticeDetailsDto.getPastNotices().get(pnIndex);
        checkNotice("noticeDetails[" + dtoIndex + "] - pastNotices[" + pnIndex + "]", "Notice", pastNotice, notice1a);


        // DTO[2]
        dtoIndex = 2;
        noticeDetailsDto = result.getNotices().get(dtoIndex);
        checkNotice("noticeDetails[" + dtoIndex + "]", "Variation notice", noticeDetailsDto, notice2c);
        assertThat("noticeDetails[" + dtoIndex + "] - pastNotices", noticeDetailsDto.getPastNotices(), notNullValue());
        assertThat("noticeDetails[" + dtoIndex + "] - pastNotices size", noticeDetailsDto.getPastNotices().size(), equalTo(2));

        pnIndex = 0;
        pastNotice = noticeDetailsDto.getPastNotices().get(pnIndex);
        checkNotice("noticeDetails[" + dtoIndex + "] - pastNotices[" + pnIndex + "]", "Variation notice", pastNotice, notice2b);

        pnIndex = 1;
        pastNotice = noticeDetailsDto.getPastNotices().get(pnIndex);
        checkNotice("noticeDetails[" + dtoIndex + "] - pastNotices[" + pnIndex + "]", "Notice", pastNotice, notice2a);
    }

    @Test
    public void getAsPersonalDeductionNoticesDtoWithoutNotices() {
        final List<PersonalTaxDeductionNotices> notices = new ArrayList<PersonalTaxDeductionNotices>();
        final PersonalTaxDeduction personalTaxDeduction = makeTaxDeduction(notices);
        final PersonalDeductionNoticesDto result;

        result = PersonalTaxDeductionNoticesConverter.getAsPersonalDeductionNoticesDto(personalTaxDeduction);
        assertThat("result", result, notNullValue());
        assertThat("total amount", result.getTotalNotifiedAmount(), equalTo(BigDecimal.ZERO));
        assertThat("number of notices", result.getNotices().size(), equalTo(0));
    }


    private void checkNotice(final String infoStr, final String noticeType, final NoticeDto noticeDto,
                             final PersonalTaxDeductionNotices notice) {
        assertThat(infoStr + " - type", noticeDto.getNoticeType(), equalTo(noticeType));
        assertThat(infoStr + " - docId", noticeDto.getDocId(), equalTo(notice.getDocId().toString()));
        assertThat(infoStr + " - noticeDate", noticeDto.getNoticeDate(), equalTo(notice.getNoticeDate()));
        assertThat(infoStr + " - noticeAmount", noticeDto.getNoticeAmount(), equalTo(notice.getNoticeAmount().abs()));

        if (noticeDto.getOriginalDocId() == null) {
            assertThat(infoStr + " - originalDocId", notice.getRefDocId(), nullValue());
        }
        else {
            assertThat(infoStr + " - originalDocId", noticeDto.getOriginalDocId(), equalTo(notice.getRefDocId().toString()));
        }

        if (noticeDto.getUnalterableNoticeAmount() == null) {
            assertThat(infoStr + " - unalterableNoticeAmountStr", notice.getUnalterableNoticeAmount(), nullValue());
        }
        else {
            assertThat(infoStr + " - unalterableNoticeAmountStr", noticeDto.getUnalterableNoticeAmount(),
                    equalTo(notice.getUnalterableNoticeAmount().abs()));
        }
    }


    private PersonalTaxDeduction makeTaxDeduction(final List<PersonalTaxDeductionNotices> notices) {
        return new PersonalTaxDeduction() {
            @Override
            public List<PersonalTaxDeductionNotices> getTaxDeductionNotices() {
                return notices;
            }
        };
    }


    private PersonalTaxDeductionNotices makeNotice(final Long docId, final Long refDocId, final DateTime dateTime,
                                                   final String amountStr, final String unalterableNoticeAmountStr,
                                                   final boolean isVarNotice) {
        return new PersonalTaxDeductionNotices() {
            @Override
            public BigDecimal getNoticeAmount() {
                return new BigDecimal(amountStr);
            }

            @Override
            public Long getDocId() {
                return docId;
            }

            @Override
            public Long getRefDocId() {
                return refDocId;
            }

            @Override
            public DateTime getNoticeDate() {
                return dateTime;
            }

            @Override
            public Boolean getIsVarNotice() {
                return isVarNotice;
            }

            @Override
            public BigDecimal getUnalterableNoticeAmount() {
                return unalterableNoticeAmountStr == null ? null : new BigDecimal(unalterableNoticeAmountStr);
            }
        };
    }

    @Test
    public void testMakePersonalTaxDeductionRequest(){
        converter= new PersonalTaxDeductionNoticesConverter();
        AuSaReq taxRequest = converter.makePersonalTaxDeductionRequest(accountNumber, "223669",new DateTime("2016-06-30").toDate() ,new DateTime("2017-07-10").toDate() , new BigDecimal(100));
        assertThat("Tax Request is not null", taxRequest, is(notNullValue()));
        assertThat("BP Id is:", taxRequest.getData().getBpId().getExtlVal().getVal(), is("400002374"));
        assertThat("Action:", taxRequest.getData().getAction().value(), is(ActionType.DEDUCT_NOTICE.value()));
        assertThat("Start date:", taxRequest.getData().getValidFrom().getVal().toString(), is("2016-06-30"));
        assertThat("End date:", taxRequest.getData().getValidTo().getVal().toString(), is("2017-07-10"));
        assertThat("Claim amount", taxRequest.getData().getClaimAmountTc().getVal(), is(new BigDecimal(100)));
        assertThat("Doc Id", taxRequest.getData().getRefDocId().getVal(), is(new BigDecimal(223669)));
    }

    @Test
    public void testToPersonalTaxDeductionResponseDtoWhenProcessed(){
        converter= new PersonalTaxDeductionNoticesConverter();
        data = AvaloqObjectFactory.getAuSaObjectFactory().createData();
        data.setPrcInfo(AvaloqGatewayUtil.createTextVal("Processed"));
        taxResponse.setData(data);
        PersonalTaxDeductionNoticeTrxnDto taxDto = converter.toPersonalTaxResponseDto(taxResponse);
        assertThat("PersonalTaxDeductionNoticeTrxnDto is not null", taxDto, is(notNullValue()));
        assertThat("Tax Save Status is saved:", taxDto.getTransactionStatus(), is("saved"));

    }

    @Test
    public void testToPersonalTaxDeductionResponseDtoWhenRejected(){
        converter = new PersonalTaxDeductionNoticesConverter();
        data = AvaloqObjectFactory.getAuSaObjectFactory().createData();
        data.setPrcInfo(AvaloqGatewayUtil.createTextVal("Rejected"));
        taxResponse.setData(data);
        PersonalTaxDeductionNoticeTrxnDto taxDto = converter.toPersonalTaxResponseDto(taxResponse);
        assertThat("PersonalTaxDeductionNoticeTrxnDto is not null", taxDto, is(notNullValue()));
        assertThat("Tax Save Status is not saved:", taxDto.getTransactionStatus(), is("notSaved"));

    }


}
