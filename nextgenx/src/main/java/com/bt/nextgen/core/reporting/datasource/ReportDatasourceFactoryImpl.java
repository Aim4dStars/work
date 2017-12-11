package com.bt.nextgen.core.reporting.datasource;

import com.bt.nextgen.core.exception.ApplicationConfigurationException;
import com.bt.nextgen.core.exception.ResourceNotFoundException;
import com.bt.nextgen.core.exception.ServiceException;
import com.bt.nextgen.core.reporting.ReportData;
import com.bt.nextgen.core.reporting.ReportIdentity;
import com.bt.nextgen.core.reporting.stereotype.MultiDataReport;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.core.reporting.stereotype.ReportImage;
import com.bt.nextgen.core.reporting.stereotype.ReportInitializer;
import net.sf.jasperreports.engine.Renderable;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Provides a configuration driven mappper for creating report data from the api tier.
 */
@Component
public class ReportDatasourceFactoryImpl implements ReportDatasourceFactory, ApplicationContextAware {

    private static final String REPORT_FILE_NAME_PARAM = "reportFileName";
    private static final String THREAD_POOL_SIZE_PARAM = "threadPoolSize";
    private ApplicationContext ctx;

    public void setApplicationContext(ApplicationContext appContext) throws BeansException {
        ctx = appContext;
    }

    @Override
    public List<ReportDatasource> createDataSources(ReportIdentity reportKey, final Map<String, Object> params) {

        Map<String, Object> dataCollections = new HashMap<>();
        final Object reportObject = getReportObject(reportKey);
        final Collection<?> dataBeans = getDataBeans(reportObject, params, dataCollections);
        final Collection<String> pageNames = getPageNames(reportObject, dataBeans);

        final String filename = getFileName(reportObject, reportKey, dataBeans);
        params.put(REPORT_FILE_NAME_PARAM, filename);

        final int threadPoolSize = getThreadPoolSize(reportObject);
        params.put(THREAD_POOL_SIZE_PARAM, threadPoolSize);

        List<ReportDatasource> reportDatasources = new ArrayList<>();
        ReportDatasource reportDatasource = null;

        final Map<String, Object> reportParams = new HashMap<>();
        reportParams.putAll(params);
        reportParams.putAll(getDataCollections(reportObject, params, dataCollections));
        reportParams.putAll(getGraphMap(reportObject, params));

        // Need to check declared classes as report.getClass() is a
        // cglib class created by the spring secruity layer.

        Class<?> clazz = getBeanClass(reportObject);
        if (clazz.getAnnotation(MultiDataReport.class) != null) {
            Iterator<?> dataBeansIter = dataBeans.iterator();
            Iterator<?> pageNamesIter = pageNames.iterator();

            while (dataBeansIter.hasNext() && pageNamesIter.hasNext()) {
                Object obj = dataBeansIter.next();
                String name = (String) pageNamesIter.next();
                reportDatasource = createDataSource(reportParams, Collections.singletonList(obj), name);
                reportDatasources.add(reportDatasource);
            }
            return reportDatasources;

        } else if (clazz.getAnnotation(Report.class) != null) {
            return Collections.singletonList(createDataSource(reportParams, dataBeans, null));
        }
        throw new IllegalArgumentException("Unsupported report object");
    }

    // Spring security layer injects a cglib proxy which does not have the declared annotations. We need the
    // real class in order to find the report type
    private Class<?> getBeanClass(final Object target) {
        if (target == null) {
            return null;
        }

        if (target instanceof Advised) {
            Advised advised = (Advised) target;
            try {
                return advised.getTargetSource().getTarget().getClass();
            } catch (Exception e) {
                throw new IllegalArgumentException("Specified bean has invalid class hierarchy", e);
            }
        }
        return target.getClass();
    }

    private ReportDatasource createDataSource(final Map<String, Object> reportParams, final Collection<?> dataBeans,
            final String name) {
        return new ReportDatasource() {
            @Override
            public Map<String, Object> getParameters() {
                return reportParams;
            }

            @Override
            public Collection<?> getBeans() {
                return (Collection<?>) dataBeans;
            }

            @Override
            public String getName() {
                return name;
            }
        };

    }

    private Object getReportObject(ReportIdentity reportKey) {
        Map<String, Object> reports = ctx.getBeansWithAnnotation(Report.class);
        Map<String, Object> multiDataReports = ctx.getBeansWithAnnotation(MultiDataReport.class);
        reports.putAll(multiDataReports);
        Object report = reports.get(reportKey.getTemplateKey());
        if (report == null) {
            throw new ResourceNotFoundException("Unknown reportType " + reportKey.getTemplateKey());
        }
        return report;
    }

    /*
     * Get the data to use in the report datasource beans
     */
    private Collection<?> getDataBeans(Object reportObject, Map<String, Object> params, Map<String, Object> dataCollections) {
        if (reportObject instanceof ReportData) {
            ReportData reportData = (ReportData) reportObject;
            Collection<?> data = reportData.getData(params, dataCollections);
            if (data != null) {
                return data;
            }
        }
        return Collections.emptyList();
    }

    private Collection<String> getPageNames(Object reportObject, Collection<?> data) {
        if (reportObject instanceof ReportData) {
            ReportData reportData = (ReportData) reportObject;
            Collection<String> pageNames = reportData.getReportPageNames(data);
            if (pageNames != null) {
                return pageNames;
            }
        }
        return Collections.emptyList();
    }

    // Allow setting of a custom filename - report template is the default if customisation not required
    private String getFileName(Object reportObject, ReportIdentity reportKey, Collection<?> data) {
        if (reportObject instanceof ReportData) {
            ReportData reportData = (ReportData) reportObject;
            String filename = reportData.getReportFileName(data);
            if (filename != null) {
                return filename;
            }
        }
        return reportKey.getTemplateKey();
    }

    // Specify thread pool size when concurrently generating report group
    private int getThreadPoolSize(Object reportObject) {
        if (reportObject instanceof ReportData) {
            ReportData reportData = (ReportData) reportObject;
            return reportData.getThreadPoolSize();
        }
        return 1;
    }

    /*
     * Get the data collections to be used in the report tables etc
     */
    private Map<String, Object> getDataCollections(Object reportObject, Map<String, Object> params,
            Map<String, Object> dataCollections) {
        Method methods[] = getBeanClass(reportObject).getMethods();
        // Method annotated with ReportInitializer will be invoked first.
        for (Method method : methods) {
            ReportInitializer reportInitializer = method.getAnnotation(ReportInitializer.class);
            if (null != reportInitializer) {
                dataCollections.put(reportInitializer.value(), invokeMethod(reportObject, params, dataCollections, method));
            }
        }
        for (Method method : methods) {
            ReportBean reportBean = method.getAnnotation(ReportBean.class);
            if (reportBean != null) {
                dataCollections.put(reportBean.value(), invokeMethod(reportObject, params, dataCollections, method));
            }
        }
        return dataCollections;
    }

    private Object invokeMethod(Object reportObject, Map<String, Object> params, Map<String, Object> data, Method method) {
        try {
            // better way of doing this?
            if (method.getParameterTypes().length == 2) {
                return method.invoke(reportObject, params, data);
            } else if (method.getParameterTypes().length == 1) {
                return method.invoke(reportObject, params);
            } else {
                return method.invoke(reportObject);
            }
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new ApplicationConfigurationException("unable to invoke supplied report collection method", e);
        } catch (InvocationTargetException e) {
            Throwable t = e.getCause();
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            throw new ServiceException("unable to invoke supplied report collection method", e);
        }
    }

    private Map<String, Renderable> getGraphMap(Object reportObject, Map<String, Object> params) {
        Map<String, Renderable> graphMap = new HashMap<>();
        Method methods[] = getBeanClass(reportObject).getMethods();
        for (Method method : methods) {
            ReportImage graph = method.getAnnotation(ReportImage.class);
            if (graph != null) {
                try {
                    Object result = method.invoke(reportObject, params);

                    if (result != null) {
                        if (result instanceof Renderable) {
                            graphMap.put(graph.value(), (Renderable) result);
                        } else {
                            throw new ApplicationConfigurationException(
                                    "ReportGraph annotated methods must return a Renderable, found " + result.getClass());
                        }
                    }
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    throw new ApplicationConfigurationException(
                            "unable to invoke supplied report bean method" + method.getClass().getName() + "." + method.getName(),
                            e);
                }
            }
        }
        return graphMap;
    }
}
