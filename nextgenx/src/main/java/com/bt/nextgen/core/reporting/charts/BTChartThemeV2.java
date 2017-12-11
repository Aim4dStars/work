package com.bt.nextgen.core.reporting.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;
import java.util.List;

import org.jfree.chart.ChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.RingPlot;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.ui.HorizontalAlignment;

public class BTChartThemeV2 implements ChartTheme {
    // TODO: Move properties to config file
    public static final Color PAINT_TITLE = new Color(0x008CC7);
    public static final Color PAINT_BACKGROUND = new Color(0xFFFFFF);

    public static final Font FONT_TITLE = new Font("Helvetica", Font.PLAIN, 12);

    public static final Color PAINT_PLOT_BACKGROUND = new Color(0xFFFFFF);
    public static final Color PAINT_PLOT_OUTLINE = new Color(0xFFFFFF);
    public static final Color PAINT_PLOT_DOMAIN_GRID = new Color(0xDFDFDF);
    public static final Color PAINT_PLOT_RANGE_GRID = new Color(0xDFDFDF);

    public static final Color PAINT_PLOT_RANGE_LABEL = new Color(0, 0, 0);
    public static final Color PAINT_PLOT_DOMAIN_LABEL = new Color(0, 0, 0);
    public static final Color PAINT_PLOT_RANGE_TICK_LABEL = new Color(0x404040);
    public static final Color PAINT_PLOT_DOMAIN_TICK_LABEL = new Color(0x404040);

    private static final List<Color> PAINT_PLOT_PIE_SECTIONS = Arrays.asList(new Color(0x00AFD7), new Color(0x8BC92F), new Color(
            0xD8E100), new Color(0xF2F000), new Color(0xFEB132), new Color(0xFA9400), new Color(0xEA6D00), new Color(0xF22766),
            new Color(0xDB235B), new Color(0xA31A60), new Color(0x90238E), new Color(0x672993), new Color(0x2A2067), new Color(
                    0x263591), new Color(0x0072BF));

    public static final Font FONT_PLOT_RANGE_LABEL = new Font("Calibri", Font.PLAIN, 9);
    public static final Font FONT_PLOT_DOMAIN_LABEL = new Font("Calibri", Font.PLAIN, 9);
    public static final Font FONT_PLOT_RANGE_TICK_LABEL = new Font("Helvetica", Font.PLAIN, 8);
    public static final Font FONT_PLOT_DOMAIN_TICK_LABEL = new Font("Helvetica", Font.PLAIN, 8);
    private static final Font FONT_PLOT_PIE_LABEL = new Font("Helvetica", Font.PLAIN, 9);

    public static final Color PAINT_BLUE = new Color(0x00B8DC);
    public static final Color PAINT_PURPLE = new Color(0x603284);
    public static final Color PAINT_GREEN = new Color(0x8bc92f);
    public static final Color PAINT_BAR_MAIN = new Color(0x00AFD7);
    public static final Color PAINT_LIGHT_PURPLE = new Color(0xBC8CBF);
    public static final Color PAINT_LIGHT_BLUE = new Color(0x70C4E4);
    public static final Color SEPARATOR = new Color(0x404040);
    public static final Color PAINT_BAR = new Color(0x0A4D67);
    public static final Color FILL_COLOR = new Color(0xffffff);

    public static final List<Color> PAINT_PLOT_LINE_COLORS = Arrays.asList(new Color(0xECCE32), new Color(0xA1B880), new Color(
            0xFFABC6), new Color(0x6B455A));

    public static final Color PAINT_BAR_ITEM_LABEL = new Color(0x838281);
    public static final Font FONT_BAR_ITEM_LABEL = new Font("Helvetica", Font.PLAIN, 8);

    public static final BasicStroke STROKE_LINE = new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    public static final BasicStroke DASH_LINE = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f,
            new float[] { 2f, 0f, 2f }, 0.0f);

    private static final StandardXYBarPainter xyBarPainter = new StandardXYBarPainter();
    private static final StandardBarPainter barPainter = new StandardBarPainter();

    private static final BTChartThemeV2 theme = new BTChartThemeV2();

    public static void applyTheme(JFreeChart chart) {
        theme.apply(chart);
    }

    @Override
    public void apply(JFreeChart chart) {
        TextTitle title = chart.getTitle();
        if (title != null) {
            title.setFont(FONT_TITLE);
            title.setPaint(PAINT_TITLE);
            title.setHorizontalAlignment(HorizontalAlignment.LEFT);
            title.setMargin(10, 8, 20, 0);
        }
        chart.setBackgroundPaint(PAINT_BACKGROUND);

        Plot plot = chart.getPlot();
        LegendTitle legend = chart.getLegend();

        if (plot != null) {
            applyToPlot(plot, legend);
        }
    }

    private void applyToPlot(Plot plot, LegendTitle legend) {
        if (plot.getBackgroundPaint() != null) {
            plot.setBackgroundPaint(PAINT_PLOT_BACKGROUND);
        }

        plot.setOutlinePaint(PAINT_PLOT_OUTLINE);

        if (plot instanceof XYPlot) {
            applyToXYPlot((XYPlot) plot);
        } else if (plot instanceof CategoryPlot) {
            applyToCategoryPlot((CategoryPlot) plot);
        } else if (plot instanceof PiePlot || plot instanceof RingPlot) {
            applyToPiePlot((PiePlot) plot);
            applyToPieLegend(legend);
        }
    }

    private void applyToPiePlot(PiePlot plot) {
        plot.setDrawingSupplier(new PieChartDrawingSupplier(PAINT_PLOT_PIE_SECTIONS));
    }

    private void applyToPieLegend(LegendTitle legend) {
        if (legend != null) {
            legend.setItemFont(FONT_PLOT_PIE_LABEL);
        }
    }

    private void applyToXYPlot(XYPlot plot) {
        plot.setDomainGridlinePaint(PAINT_PLOT_DOMAIN_GRID);
        plot.setRangeGridlinePaint(PAINT_PLOT_RANGE_GRID);

        ValueMarker valueZeroMarker = new ValueMarker(0.0d);
        valueZeroMarker.setPaint(Color.BLACK);
        plot.addRangeMarker(valueZeroMarker);

        for (int i = 0; i < plot.getDomainAxisCount(); i++) {
            ValueAxis axis = plot.getDomainAxis(i);
            if (axis != null) {
                applyToDomainAxis(axis);
            }
        }

        for (int i = 0; i < plot.getRangeAxisCount(); i++) {
            ValueAxis axis = plot.getRangeAxis(i);
            if (axis != null) {
                applyToRangeAxis(axis);
            }
        }

        for (int i = 0; i < plot.getRendererCount(); i++) {
            XYItemRenderer r = plot.getRenderer(i);
            if (r != null) {
                applyToXYItemRenderer(r);
            }
        }

    }

    private void applyToCategoryPlot(CategoryPlot plot) {
        plot.setDomainGridlinePaint(PAINT_PLOT_DOMAIN_GRID);
        plot.setRangeGridlinePaint(PAINT_PLOT_RANGE_GRID);

        // process all domain axes
        int domainAxisCount = plot.getDomainAxisCount();
        for (int i = 0; i < domainAxisCount; i++) {
            CategoryAxis axis = plot.getDomainAxis(i);
            if (axis != null) {
                applyToCategoryAxis(axis);
            }
        }

        // process all range axes
        int rangeAxisCount = plot.getRangeAxisCount();
        for (int i = 0; i < rangeAxisCount; i++) {
            ValueAxis axis = plot.getRangeAxis(i);
            if (axis != null) {
                applyToValueAxis(axis);
            }
        }

        // process all renderers
        int rendererCount = plot.getRendererCount();
        for (int i = 0; i < rendererCount; i++) {
            CategoryItemRenderer r = plot.getRenderer(i);
            if (r != null) {
                applyToCategoryItemRenderer(r);
            }
        }
    }

    private void applyToCategoryAxis(CategoryAxis axis) {
        axis.setLabelFont(FONT_PLOT_DOMAIN_LABEL);
        axis.setLabelPaint(PAINT_PLOT_DOMAIN_LABEL);
        axis.setTickLabelFont(FONT_PLOT_DOMAIN_TICK_LABEL);
        axis.setTickLabelPaint(PAINT_PLOT_DOMAIN_TICK_LABEL);
    }

    protected void applyToValueAxis(ValueAxis axis) {
        axis.setLabelFont(FONT_PLOT_RANGE_LABEL);
        axis.setLabelPaint(PAINT_PLOT_RANGE_LABEL);
        axis.setTickLabelFont(FONT_PLOT_RANGE_TICK_LABEL);
        axis.setTickLabelPaint(PAINT_PLOT_RANGE_TICK_LABEL);
    }

    protected void applyToCategoryItemRenderer(CategoryItemRenderer renderer) {
        renderer.setBaseItemLabelFont(FONT_BAR_ITEM_LABEL);
        renderer.setBaseItemLabelPaint(PAINT_BAR_ITEM_LABEL);

        // BarRenderer
        if (renderer instanceof BarRenderer) {
            BarRenderer br = (BarRenderer) renderer;
            br.setBarPainter(barPainter);
            br.setShadowVisible(false);
        }
    }

    private void applyToRangeAxis(ValueAxis axis) {
        axis.setLabelFont(FONT_PLOT_RANGE_LABEL);
        axis.setLabelPaint(PAINT_PLOT_RANGE_LABEL);
        axis.setTickLabelFont(FONT_PLOT_RANGE_TICK_LABEL);
        axis.setTickLabelPaint(PAINT_PLOT_RANGE_TICK_LABEL);
    }

    private void applyToDomainAxis(ValueAxis axis) {
        axis.setLabelFont(FONT_PLOT_DOMAIN_LABEL);
        axis.setLabelPaint(PAINT_PLOT_DOMAIN_LABEL);
        axis.setTickLabelFont(FONT_PLOT_DOMAIN_TICK_LABEL);
        axis.setTickLabelPaint(PAINT_PLOT_DOMAIN_TICK_LABEL);
    }

    private void applyToXYItemRenderer(XYItemRenderer renderer) {
        renderer.setBaseItemLabelFont(FONT_BAR_ITEM_LABEL);
        renderer.setBaseItemLabelPaint(PAINT_BAR_ITEM_LABEL);

        if (renderer instanceof XYBarRenderer) {
            XYBarRenderer br = (XYBarRenderer) renderer;
            br.setBarPainter(xyBarPainter);
            br.setShadowVisible(false);
        }
    }
}
