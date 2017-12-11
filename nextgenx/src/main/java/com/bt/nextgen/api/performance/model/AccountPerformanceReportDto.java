package com.bt.nextgen.api.performance.model;

import java.math.BigDecimal;
import java.util.List;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.core.util.LambdaMatcher;

import ch.lambdaj.Lambda;

/**
 * @author L079690
 *
 */
public class AccountPerformanceReportDto extends BaseDto implements KeyedDto <AccountPerformanceKey>
{
	private AccountPerformanceKey accountPerformanceKey;
	private List <PerformanceReportDataDto> performanceData;
	private List <PerformanceReportDataDto> netReturnData;
	private List <String> colHeaders;
	private static final int OTHER_FEES_INDEX_FROM_END = 2;

	public AccountPerformanceReportDto(AccountPerformanceKey accountPerformanceKey,
		List <PerformanceReportDataDto> performanceData, List <PerformanceReportDataDto> netReturnData, List <String> colHeaders)
	{
		this.accountPerformanceKey = accountPerformanceKey;
		this.performanceData = performanceData;
        this.netReturnData = (netReturnData == null ? null : filterZeroOtherFees(netReturnData));
		this.colHeaders = colHeaders;
	}
	@Override
	public AccountPerformanceKey getKey()
	{
		return accountPerformanceKey;
	}
	public AccountPerformanceKey getAccountPerformanceKey()
	{
		return accountPerformanceKey;
	}

	public List <String> getColHeaders()
	{
		return colHeaders;
	}

	public List <PerformanceReportDataDto> getPerformanceData()
	{
		return performanceData;
	}

	public List <PerformanceReportDataDto> getNetReturnData()
	{
		return netReturnData;
	}

    private int getOtherFeesIndex(PerformanceReportDataDto performanceReportDataDto) {
        return performanceReportDataDto.getDataList().size() - OTHER_FEES_INDEX_FROM_END;
    }

    // To remove Other Fee that have Zero Period Fees
    private boolean hasOtherFeesAsZeroLambda(PerformanceReportDataDto performanceReportDataDto) {
        return performanceReportDataDto.getDescription().contains("Other Fees") && performanceReportDataDto.getDataList()
                .get(getOtherFeesIndex(performanceReportDataDto)).compareTo(BigDecimal.ZERO) == 0;
    }
    
    // To check if the update Fee List has Other Fees or not
    private boolean hasOtherFeesLambda(PerformanceReportDataDto performanceReportDataDto) {
        return performanceReportDataDto.getDescription().contains("Other Fees");
    }    
    
    private List<PerformanceReportDataDto> filterZeroOtherFees(List<PerformanceReportDataDto> netReturnData) { 
        return Lambda.filter(new LambdaMatcher<PerformanceReportDataDto>() {
            @Override
            protected boolean matchesSafely(PerformanceReportDataDto performanceReportDataDto) {
                return !hasOtherFeesAsZeroLambda(performanceReportDataDto);
            }
        }, netReturnData);
    }

    /**
     * @return true/false to be used by report to show respective notes
     */
    public boolean hasOtherFees() {
        return !Lambda.filter(new LambdaMatcher<PerformanceReportDataDto>() {
            @Override
            protected boolean matchesSafely(PerformanceReportDataDto performanceReportDataDto) {
                return hasOtherFeesLambda(performanceReportDataDto);
            }
        }, this.netReturnData).isEmpty();
    }    
}
