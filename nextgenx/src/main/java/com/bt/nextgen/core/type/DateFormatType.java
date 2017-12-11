package com.bt.nextgen.core.type;

public enum DateFormatType 
{
        DATEFORMAT_UPLOAD_OFFLINE("dd MMM yyyy hhmmss"),
        DATEFORMAT_FRONT_END("dd MMM yyyy"),
		DATEFORMAT_AVALOQ("yyyy-MM-dd");
		
		
		private String dateFormat = "";
		
		DateFormatType(String dateFormat)
		{
			this.dateFormat = dateFormat;
		}
		
		String getDateFormat()
		{
			return dateFormat;
		}
}
