package com.bt.nextgen.core.reporting.charts;

import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTick;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.ui.RectangleEdge;
import org.joda.time.DateTime;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class MappedDateAxis extends DateAxis
{
	private Map <String, String> labelMap;
	private List <String> labelList;
	private boolean upperCaseLabel = false;
    private DateTime firstDateTick;

	// Modify the domain-tick start.
	private DateTickUnit tickAdjustment;

	public MappedDateAxis()
	{
		super();
	}

	public MappedDateAxis(String label)
	{
		super(label);
	}

	public MappedDateAxis(String label, TimeZone zone, Locale locale)
	{
		super(label, zone, locale);
	}

	public Map <String, String> getLabelMap()
	{
		return labelMap;
	}

	public void setLabelMap(Map <String, String> labelMap)
	{
		this.labelMap = labelMap;
	}

	public List <String> getLabelList()
	{
		return labelList;
	}

	public void setLabelList(List <String> labelList)
	{
		this.labelList = labelList;
	}

	public boolean isUpperCaseLabel()
	{
		return upperCaseLabel;
	}

	public void setUpperCaseLabel(boolean upperCaseLabel)
	{
		this.upperCaseLabel = upperCaseLabel;
	}

	public List refreshTicks(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge)
	{
		List <DateTick> tickList = super.refreshTicks(g2, state, dataArea, edge);

		// Replace mode
		if (labelList != null && !labelList.isEmpty())
		{
			List <DateTick> newTickList = new ArrayList <DateTick>();

			for (int i = 0; i < tickList.size(); i++)
			{
				DateTick tick = tickList.get(i);

				if (i < labelList.size())
				{
					String replacement = labelList.get(i);

					newTickList.add(new DateTick(tick.getDate(),
						upperCaseLabel ? replacement.toUpperCase() : replacement,
						tick.getTextAnchor(),
						tick.getRotationAnchor(),
						tick.getAngle()));
				}
				else
				{
					newTickList.add(tick);
				}
			}

			return newTickList;
		}
		// Remap mode
		else if (labelMap != null && !labelMap.isEmpty())
		{
			List <DateTick> newTickList = new ArrayList <DateTick>(tickList.size());

			for (DateTick tick : tickList)
			{
				String replacement = labelMap.get(tick.getText());

				if (replacement != null)
				{
					newTickList.add(new DateTick(tick.getDate(),
						upperCaseLabel ? replacement.toUpperCase() : replacement,
						tick.getTextAnchor(),
						tick.getRotationAnchor(),
						tick.getAngle()));
				}
				else if (upperCaseLabel)
				{
					newTickList.add(new DateTick(tick.getDate(),
						tick.getText().toUpperCase(),
						tick.getTextAnchor(),
						tick.getRotationAnchor(),
						tick.getAngle()));
				}
				else
				{
					newTickList.add(tick);
				}
			}

			return newTickList;
		}

		return tickList;
	}

	@Override
	protected Date previousStandardDate(Date date, DateTickUnit unit)
	{
		Date reDate = super.previousStandardDate(date, unit);
		if (reDate != null)
		{
			if (tickAdjustment != null && tickAdjustment.getUnitType().equals(unit.getUnitType()))
			{
				// Only adjust when the DateTickUnitType matches.
				Calendar cal = Calendar.getInstance();
				cal.setTime(reDate);
				if (DateTickUnitType.DAY.equals(unit.getUnitType()))
				{
					cal.set(Calendar.DATE, cal.get(Calendar.DATE) + tickAdjustment.getMultiple());
				}
				else if (DateTickUnitType.MONTH.equals(unit.getUnitType()))
				{
					cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + tickAdjustment.getMultiple());
				}
				else if (DateTickUnitType.YEAR.equals(unit.getUnitType()))
				{
					cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + tickAdjustment.getMultiple());
				}
				return cal.getTime();
			}
		}

		return reDate;
	}

	public DateTickUnit getTickAdjustment()
	{
		return tickAdjustment;
	}

	public void setTickAdjustment(DateTickUnit tickAdjustment)
	{
		this.tickAdjustment = tickAdjustment;
	}

    public Date calculateLowestVisibleTickValue(DateTickUnit unit) {
        if (firstDateTick != null) {
            return firstDateTick.toDate();
        }
        return super.calculateLowestVisibleTickValue(unit);
    }

    public void setFirstDateTick(DateTime date) {
        this.firstDateTick = date;
    }

}
