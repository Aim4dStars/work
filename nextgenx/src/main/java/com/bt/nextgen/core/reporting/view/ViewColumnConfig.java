package com.bt.nextgen.core.reporting.view;

public interface ViewColumnConfig
{
	public enum HorizontalAlignment
	{
		LEFT, CENTER, RIGHT, JUSTIFIED
    }

	public enum VerticalAlignment
	{
		TOP, MIDDLE, BOTTOM, JUSTIFIED
    }

	public enum Markup
	{
		HTML("html"), STYLED("styled"), RTF("rtf"), NONE("none");

        private final String code;

		Markup(String code)
		{
			this.code = code;
		}

		public String getCode()
		{
			return code;
		}
	}

	String getHeaderLabel();

	int getWidth();

	HorizontalAlignment getTextHorizontalAlignment();

	VerticalAlignment getTextVerticalAlignment();

	String getDetailExpression();

	String getFooterExpression();

	Markup getHeaderMarkup();

	Markup getDetailMarkup();
}
