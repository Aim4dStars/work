package com.bt.nextgen.core.reporting.view;

public class ViewColumnConfigImpl implements ViewColumnConfig {
    private String headerLabel;
    private int width = 50;
    private HorizontalAlignment textHorizontalAlignment;
    private VerticalAlignment textVerticalAlignment;
    private String detailExpression;
    private String footerExpression;
    private Markup headerMarkup;
    private Markup detailMarkup;

    public ViewColumnConfigImpl() {
        // default constructor
    }

    public ViewColumnConfigImpl(String headerLabel, String detailExpression, int width) {
        this.headerLabel = headerLabel;
        this.detailExpression = detailExpression;
        this.width = width;
    }

    public ViewColumnConfigImpl(String headerLabel, String detailExpression, HorizontalAlignment textHorizontalAlignment,
            int width) {
        this.headerLabel = headerLabel;
        this.detailExpression = detailExpression;
        this.textHorizontalAlignment = textHorizontalAlignment;
        this.width = width;
    }

    public ViewColumnConfigImpl(String headerLabel, String detailExpression, VerticalAlignment textVerticalAlignment, int width) {
        this.headerLabel = headerLabel;
        this.detailExpression = detailExpression;
        this.textVerticalAlignment = textVerticalAlignment;
        this.width = width;
    }

    public ViewColumnConfigImpl(String headerLabel, String detailExpression, HorizontalAlignment textHorizontalAlignment,
            VerticalAlignment textVerticalAlignment, int width) {
        this.headerLabel = headerLabel;
        this.detailExpression = detailExpression;
        this.textHorizontalAlignment = textHorizontalAlignment;
        this.textVerticalAlignment = textVerticalAlignment;
        this.width = width;
    }

    @Override
    public String getHeaderLabel() {
        return headerLabel;
    }

    public void setHeaderLabel(String headerLabel) {
        this.headerLabel = headerLabel;
    }

    @Override
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public HorizontalAlignment getTextHorizontalAlignment() {
        return textHorizontalAlignment;
    }

    public void setTextHorizontalAlignment(HorizontalAlignment textHorizontalAlignment) {
        this.textHorizontalAlignment = textHorizontalAlignment;
    }

    @Override
    public VerticalAlignment getTextVerticalAlignment() {
        return textVerticalAlignment;
    }

    public void setTextVerticalAlignment(VerticalAlignment textVerticalAlignment) {
        this.textVerticalAlignment = textVerticalAlignment;
    }

    @Override
    public String getDetailExpression() {
        return detailExpression;
    }

    public void setDetailExpression(String detailExpression) {
        this.detailExpression = detailExpression;
    }

    @Override
    public String getFooterExpression() {
        return footerExpression;
    }

    public void setFooterExpression(String footerExpression) {
        this.footerExpression = footerExpression;
    }

    @Override
    public Markup getHeaderMarkup() {
        if(headerMarkup ==null) {
            return Markup.HTML;
        }
        return headerMarkup;
    }

    public void setHeaderMarkup(Markup headerMarkup) {
        this.headerMarkup = headerMarkup;
    }

    @Override
    public Markup getDetailMarkup() {
        if (headerMarkup == null) {
            return Markup.HTML;
        }
        return detailMarkup;
    }

    public void setDetailMarkup(Markup detailMarkup) {
        this.detailMarkup = detailMarkup;
    }
}
