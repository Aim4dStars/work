package com.bt.nextgen.core.reporting.view;

import com.bt.nextgen.core.reporting.ReportIdentity;
import com.bt.nextgen.core.reporting.view.ViewColumnConfig.HorizontalAlignment;
import com.bt.nextgen.core.reporting.view.ViewColumnConfig.VerticalAlignment;
import net.sf.jasperreports.components.table.StandardColumn;
import net.sf.jasperreports.components.table.StandardColumnGroup;
import net.sf.jasperreports.components.table.StandardTable;
import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignComponentElement;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.HorizontalAlignEnum;
import net.sf.jasperreports.engine.type.StretchTypeEnum;
import net.sf.jasperreports.engine.type.VerticalAlignEnum;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class ReportViewProcessorJasperImpl implements ReportViewProcessor
{
	private static final Logger logger = LoggerFactory.getLogger(ReportViewProcessorJasperImpl.class);
    private static final ReportIdentity SUBREPORT_TEMPLATE = ReportIdentity.ReportIdentityString.asIdentity("DynamicSrTemplate");

    @Autowired
    private ReportViewFactory reportViewFactory;




    @Override
    public JasperDesign process(ReportView reportView) {
        JasperDesign jasperDesign = null;
        if (!(reportView instanceof DefaultReportViewImpl)) {
            try {
                if ("application/vnd.jrxml".equals(reportView.getReportTemplate().getType())) {
                    jasperDesign = JRXmlLoader.load(reportView.getReportTemplate().getAsStream());
                    processTableConfigs(reportView, jasperDesign);
                }
                processSubreports(reportView);
            } catch (JRException | IOException ex) {
                throw new RuntimeException("Unable to load jrxml file for " + reportView.getReportTemplate().getId(), ex);
            }

        }
        return jasperDesign;
    }

    private void processSubreports(ReportView reportView) throws JRException, IOException {
        Map<String, ViewConfig> tableConfigs = reportView.getViewConfigs();

        if (tableConfigs != null && !tableConfigs.isEmpty()) {
            for (String id : tableConfigs.keySet()) {                
                ReportView subreportView = reportViewFactory.createReportView(SUBREPORT_TEMPLATE);
                JasperDesign subreport = JRXmlLoader.load(subreportView.getReportTemplate().getAsStream());
                addFieldsToDataSource(id, reportView.getViewConfigs().get(id), subreport);
                configureColumnHeaders(reportView.getViewConfigs().get(id), (JRDesignBand) subreport.getColumnHeader(),
                        subreport.getStylesMap());
                configureDetailRows(reportView.getViewConfigs().get(id),
                        (JRDesignBand) subreport.getDetailSection().getBands()[0],
                        subreport.getStylesMap());
                reportView.addSubreport(id, JasperCompileManager.compileReport(subreport));
            }
        }
    }

    private void configureColumnHeaders(ViewConfig config, JRDesignBand columnHeader, Map<String, JRStyle> styles) {
        int i=0;
        int x = 0;
        for(ViewColumnConfig column: config.getViewColumnConfigs()) {
            JRDesignTextField header = new JRDesignTextField();
            header.setExpression(new JRDesignExpression("\"" + column.getHeaderLabel() + "\""));
            if(i==0) {
                header.setStyle(styles.get("TemplateTableHeaderFirst"));
            } else if (i == config.getViewColumnConfigs().size() - 1) {
                header.setStyle(styles.get("TemplateTableHeaderNumericLast"));
            } else  {
                header.setStyle(styles.get("TemplateTableHeaderNumeric"));
            }
            header.setX(x);
            header.setY(0);
            header.setHeight(15);
            header.setWidth(column.getWidth());
            header.setStretchWithOverflow(true);
            header.setStretchType(StretchTypeEnum.RELATIVE_TO_BAND_HEIGHT);
            header.setMarkup(column.getHeaderMarkup().getCode());
            x += column.getWidth();
            i += 1;
            columnHeader.addElement(header);
        }
    }

    private void configureDetailRows(ViewConfig config, JRDesignBand detail, Map<String, JRStyle> styles) {
        int i = 0;
        int x = 0;
        for (ViewColumnConfig column : config.getViewColumnConfigs()) {
            JRDesignTextField data = new JRDesignTextField();
            data.setExpression(new JRDesignExpression(column.getDetailExpression()));
            if (i == 0) {
                data.setStyle(styles.get("TemplateTableBodyFirst"));
            } else if (i == config.getViewColumnConfigs().size() - 1) {
                data.setStyle(styles.get("TemplateTableBodyNumericLast"));
            } else {
                data.setStyle(styles.get("TemplateTableBodyNumeric"));
            }
            data.setX(x);
            data.setY(0);
            data.setHeight(15);
            data.setWidth(column.getWidth());
            data.setStretchWithOverflow(true);
            data.setStretchType(StretchTypeEnum.RELATIVE_TO_BAND_HEIGHT);
            data.setMarkup(column.getHeaderMarkup().getCode());
            x += column.getWidth();
            i += 1;
            detail.addElement(data);
        }
    }


    private void processTableConfigs(ReportView reportView, JasperDesign jasperDesign)
	{
        Map<String, ViewConfig> tableConfigs = reportView.getViewConfigs();

		if (tableConfigs != null && !tableConfigs.isEmpty())
		{
			for (String tableId : tableConfigs.keySet())
			{
				JRDesignComponentElement tableElement = findTableComponentElement(tableId, jasperDesign);

				if (tableElement != null)
				{
					ViewConfig tableConfig = tableConfigs.get(tableId);

					addFieldsToDataSource(tableId, tableConfig, tableElement, jasperDesign);
                    addColumnsToTable(tableConfig, tableElement);
				}
			}
		}
	}

    private void addFieldsToDataSource(String tableId, ViewConfig srConfig, JasperDesign jasperDesign) throws JRException {
        for (DataSourceField field : srConfig.getDataSourceFields()) {
            JRDesignField jsField = new JRDesignField();
            jsField.setName(field.getFieldName());
            jsField.setValueClass(field.getFieldClass());
            jasperDesign.addField(jsField);
        }
    }

	private void addFieldsToDataSource(String tableId, ViewConfig tableConfig, JRDesignComponentElement tableElement,
		JasperDesign jasperDesign)
	{
		StandardTable standardTable = (StandardTable)tableElement.getComponent();

		String dataSetName = standardTable.getDatasetRun().getDatasetName();

		if (StringUtils.isBlank(dataSetName))
		{
			logger.error("Missing data source name for table {}.  Please associate the table to a data source.", tableId);
			throw new RuntimeException("Missing data source name for table " + tableId
				+ ".  Please associate the table to a data source.");
		}

		JRDesignDataset dataSet = (JRDesignDataset)jasperDesign.getDatasetMap().get(dataSetName);

		try
		{
			for (DataSourceField dsField : tableConfig.getDataSourceFields())
			{
				JRDesignField field = new JRDesignField();
				field.setName(dsField.getFieldName());
				field.setValueClass(dsField.getFieldClass());

				dataSet.addField(field);
			}
		}
		catch (JRException ex)
		{
			throw new RuntimeException("Unable to add field to data source \"" + dataSetName + "\" for table " + tableId);
		}
	}

    private void addColumnsToTable(ViewConfig tableConfig, JRDesignComponentElement tableElement)
	{
		StandardTable standardTable = (StandardTable)tableElement.getComponent();

		int totalWidth = 0;

		if (!standardTable.getColumns().isEmpty())
		{
			if (standardTable.getColumns().get(0) instanceof StandardColumnGroup)
			{
				StandardColumnGroup refColumnGroup = (StandardColumnGroup)standardTable.getColumns().get(0);
				StandardColumnGroup columnGroup = (StandardColumnGroup)refColumnGroup.clone();
				StandardColumn columnRef = (StandardColumn)columnGroup.getColumns().get(0);
				columnGroup.getColumns().clear();
				standardTable.getColumns().clear();

				for (ViewColumnConfig colConfig : tableConfig.getViewColumnConfigs())
				{
					StandardColumn newCol = (StandardColumn)columnRef.clone();

					newCol.setWidth(colConfig.getWidth());
					totalWidth += colConfig.getWidth();

					// Handle dynamic column header
					JRDesignStaticText colHeaderText = null;

					for (JRChild child : newCol.getColumnHeader().getChildren())
					{
						// Change the first instance of static text
						if (child instanceof JRDesignStaticText)
						{
							colHeaderText = (JRDesignStaticText)child;
							break;
						}
					}

					if (colHeaderText == null)
					{
						colHeaderText = new JRDesignStaticText();
						newCol.getColumnHeader().getChildren().add(colHeaderText);
					}

					colHeaderText.setText(colConfig.getHeaderLabel());

					if (colConfig.getHeaderMarkup() != null)
					{
						colHeaderText.setMarkup(colConfig.getHeaderMarkup().getCode());
					}

					colHeaderText.setWidth(colConfig.getWidth());

					// Handle dynamic column detail
					JRDesignTextField colDetailField = null;

					for (JRChild child : newCol.getDetailCell().getChildren())
					{
						if (child instanceof JRDesignTextField)
						{
							colDetailField = (JRDesignTextField)child;
							break;
						}
					}

					if (colDetailField == null)
					{
						colDetailField = new JRDesignTextField();
						newCol.getDetailCell().getChildren().add(colDetailField);
					}

					colDetailField.setExpression(new JRDesignExpression(colConfig.getDetailExpression()));

					if (colConfig.getDetailMarkup() != null)
					{
						colDetailField.setMarkup(colConfig.getDetailMarkup().getCode());
					}

					colDetailField.setWidth(colConfig.getWidth());

					// Handle dynamic column footer
					JRDesignTextField colFooterField = null;

					if (newCol.getColumnFooter() != null && StringUtils.isNotBlank(colConfig.getFooterExpression()))
					{
						for (JRChild child : newCol.getColumnFooter().getChildren())
						{
							if (child instanceof JRDesignTextField)
							{
								colFooterField = (JRDesignTextField)child;
								break;
							}
						}

						if (colFooterField == null)
						{
							colFooterField = new JRDesignTextField();
							newCol.getColumnFooter().getChildren().add(colFooterField);
						}

						colFooterField.setExpression(new JRDesignExpression(colConfig.getFooterExpression()));
						colFooterField.setWidth(colConfig.getWidth());
					}

					// Handle text alignments
					if (colConfig.getTextHorizontalAlignment() != null)
					{
						HorizontalAlignEnum hAlign = getHorizontalAlignment(colConfig.getTextHorizontalAlignment());

						colHeaderText.setHorizontalAlignment(hAlign);
						colDetailField.setHorizontalAlignment(hAlign);

						if (colFooterField != null)
						{
							colFooterField.setHorizontalAlignment(hAlign);
						}
					}

					if (colConfig.getTextVerticalAlignment() != null)
					{
						VerticalAlignEnum vAlign = getVerticalAlignment(colConfig.getTextVerticalAlignment());

						colHeaderText.setVerticalAlignment(vAlign);
						colDetailField.setVerticalAlignment(vAlign);

						if (colFooterField != null)
						{
							colFooterField.setVerticalAlignment(vAlign);
						}
					}

					columnGroup.getColumns().add(newCol);
				}
				columnGroup.setWidth(totalWidth);
				standardTable.getColumns().add(columnGroup);
			}
		}

		tableElement.setWidth(totalWidth);
	}

	private JRDesignComponentElement findTableComponentElement(String tableId, JasperDesign jasperDesign)
	{
		// May need recursion?
		if (jasperDesign.getDetailSection().getBands().length > 0)
		{
			for (JRBand band : jasperDesign.getDetailSection().getBands())
			{
				for (JRChild child : band.getChildren())
				{
					if (child instanceof JRDesignComponentElement)
					{
						JRDesignComponentElement el = (JRDesignComponentElement)child;

						if (el.getComponent() instanceof StandardTable)
						{
							if (tableId.equals(el.getPropertiesMap()
								.getProperty("net.sf.jasperreports.export.headertoolbar.table.name")))
							{
								return el;
							}
						}
					}
				}
			}
		}

		return null;
	}


	private HorizontalAlignEnum getHorizontalAlignment(HorizontalAlignment textHorizontalAlignment)
	{
		if (HorizontalAlignment.RIGHT.equals(textHorizontalAlignment))
		{
			return HorizontalAlignEnum.RIGHT;
		}
		else if (HorizontalAlignment.CENTER.equals(textHorizontalAlignment))
		{
			return HorizontalAlignEnum.CENTER;
		}
		else if (HorizontalAlignment.JUSTIFIED.equals(textHorizontalAlignment))
		{
			return HorizontalAlignEnum.JUSTIFIED;
		}

		return HorizontalAlignEnum.LEFT;
	}

	private VerticalAlignEnum getVerticalAlignment(VerticalAlignment textVerticalAlignment)
	{
		if (VerticalAlignment.TOP.equals(textVerticalAlignment))
		{
			return VerticalAlignEnum.TOP;
		}
		else if (VerticalAlignment.BOTTOM.equals(textVerticalAlignment))
		{
			return VerticalAlignEnum.BOTTOM;
		}
		else if (VerticalAlignment.JUSTIFIED.equals(textVerticalAlignment))
		{
			return VerticalAlignEnum.JUSTIFIED;
		}

		return VerticalAlignEnum.MIDDLE;
	}
}
