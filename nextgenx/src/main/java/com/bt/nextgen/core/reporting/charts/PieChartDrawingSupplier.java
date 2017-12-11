package com.bt.nextgen.core.reporting.charts;

import org.jfree.chart.plot.DefaultDrawingSupplier;

import java.awt.Color;
import java.awt.Paint;
import java.util.List;

public class PieChartDrawingSupplier extends DefaultDrawingSupplier {

    public List<Color> paintList;
    public int paintIndex;
    public int fillPaintIndex;

    public PieChartDrawingSupplier(List<Color> paintList) {
        this.paintList = paintList;
    }

    @Override
    public Paint getNextPaint() {
        Paint next = paintList.get(paintIndex % paintList.size());
        paintIndex++;
        return next;
    }

    @Override
    public Paint getNextFillPaint() {
        Paint next = paintList.get(fillPaintIndex % paintList.size());
        fillPaintIndex++;
        return next;
    }
}