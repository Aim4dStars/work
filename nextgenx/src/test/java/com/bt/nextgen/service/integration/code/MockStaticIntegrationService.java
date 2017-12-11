package com.bt.nextgen.service.integration.code;

import ch.lambdaj.function.convert.Converter;
import ch.lambdaj.function.matcher.Predicate;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.service.ServiceError;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.code.StaticCodeHolder;
import org.slf4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.ws.client.core.SourceExtractor;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static ch.lambdaj.Lambda.map;
import static ch.lambdaj.Lambda.selectFirst;
import static java.lang.Thread.currentThread;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.junit.Assert.assertEquals;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Mock implementation of the {@code StaticIntegrationService}, that will simply look for static codes by category as
 * similarly-named XML resources in the classpath.
 */
public class MockStaticIntegrationService implements StaticIntegrationService {

    private static final Logger LOGGER = getLogger(MockStaticIntegrationService.class);

    private static final Converter<Code, String> BY_CODE_ID = new Converter<Code, String>() {
        @Override
        public String convert(Code from) {
            return from.getCodeId();
        }
    };

    /**
     * Extractor to suck out the static codes from the various XML files.
     */
    private final SourceExtractor<StaticCodeHolder> extractor = new DefaultResponseExtractor<>(StaticCodeHolder.class);

    /**
     * Document Builder Factory required to build nodes from the XML streams.
     */
    private final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    @Override
    public Code loadCode(CodeCategoryInterface category, final String codeId, ServiceErrors errors) {
        return selectFirst(loadCodes(category, errors), new Predicate<Code>() {
            @Override
            public boolean apply(Code code) {
                return code.getCodeId().equals(codeId);
            }
        });
    }

    //@Override
    public Code loadCode(String category, final String codeId, ServiceErrors serviceErrors) {
        return selectFirst(loadCodes(category, serviceErrors), new Predicate<Code>() {
            @Override
            public boolean apply(Code code) {
                return code.getName().equals(codeId);
            }
        });
    }


    @Override
    public Code loadCodeByName(CodeCategoryInterface category, final String name, ServiceErrors errors) {
        return selectFirst(loadCodes(category, errors), new Predicate<Code>() {
            @Override
            public boolean apply(Code code) {
                return code.getName().equals(name);
            }
        });
    }

    @Override
    public Code loadCodeByUserId(CodeCategoryInterface category, final String userId, ServiceErrors errors) {
        return selectFirst(loadCodes(category, errors), new Predicate<Code>() {
            @Override
            public boolean apply(Code code) {
                return code.getUserId().equals(userId);
            }
        });
    }

    @Override
    public Code loadCodeByAvaloqId(CodeCategoryInterface category, final String avaloqId, ServiceErrors errors) {
        return selectFirst(loadCodes(category, errors), new Predicate<Code>() {
            @Override
            public boolean apply(Code code) {
                return code.getIntlId().equals(avaloqId);
            }
        });
    }

    @Override
    public Collection<Code> loadCodes(CodeCategoryInterface category, ServiceErrors errors) {
        try {
            final StaticCodeHolder holder = extractor.extractData(codesXml((CodeCategory) category));
            final Collection<Code> codes = holder.getStaticCodes();
            if (codes.isEmpty()) {
                throw new EmptyResultDataAccessException("No codes found for category: " + category.getCode(), 1);
            }
            final Code first = codes.iterator().next();
            assertEquals("Static code category mismatch", category.getCode(), first.getCategory());
            return holder.getStaticCodes();
        } catch (Exception e) {
            ServiceError error = new ServiceErrorImpl("Error loading codes for category: " + category);
            error.setException(e);
            errors.addError(error);
        }
        return emptyList();
    }


    public Collection<Code> loadCodes(String category, ServiceErrors errors) {
        try {
            final StaticCodeHolder holder = extractor.extractData(codesXml(category));
            final Collection<Code> codes = holder.getStaticCodes();
            if (codes.isEmpty()) {
                throw new EmptyResultDataAccessException("No codes found for category: " + category, 1);
            }
            final Code first = codes.iterator().next();
            assertEquals("Static code category mismatch", category, first.getCategory());
            return holder.getStaticCodes();
        } catch (Exception e) {
            ServiceError error = new ServiceErrorImpl("Error loading codes for category: " + category);
            error.setException(e);
            errors.addError(error);
        }
        return emptyList();
    }

    @Override
    public Map<CodeCategoryInterface, Map<String, Code>> loadCodes(ServiceErrors errors) {
        final ClassLoader loader = currentThread().getContextClassLoader();
        final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(loader);
        final String pathPattern = pathOfThisClass() + "*.xml";
        try {
            final Map<CodeCategoryInterface, Map<String, Code>> result = new HashMap<>();
            for (Resource resource : resolver.getResources(pathPattern)) {
                String categoryName = resource.getFilename();
                categoryName = categoryName.substring(0, categoryName.indexOf('.')).toUpperCase();
                try {
                    final CodeCategory category = CodeCategory.valueOf(categoryName);
                    final Map<String, Code> codeMap = map(loadCodes(category, errors), BY_CODE_ID);
                    result.put(category, codeMap);
                } catch (IllegalArgumentException iae) {
                    LOGGER.info("Resource does not correspond to a CodeCategory: {}", resource.getFilename());
                } catch (Exception e) {
                    LOGGER.error("Error loading codes XML resource: {}", resource.getURI());
                    ServiceError error = new ServiceErrorImpl("I/O error loading resources from classpath:" + pathPattern);
                    error.setException(e);
                    errors.addError(error);
                }
            }
            return result;
        } catch (IOException ioe) {
            ServiceError error = new ServiceErrorImpl("I/O error loading resources from classpath:" + pathPattern);
            error.setException(ioe);
            errors.addError(error);
        }
        return emptyMap();
    }

    private Source codesXml(CodeCategory category) throws IOException, ParserConfigurationException, SAXException {
        return codesXml(category.name().toLowerCase());
    }

    private Source codesXml(String category) throws IOException, ParserConfigurationException, SAXException {
        final String path = pathOfThisClass() + category + ".xml";
        final InputStream is = currentThread().getContextClassLoader().getResourceAsStream(path);
        if (is == null) {
            throw new FileNotFoundException(path);
        }
        return codesXml(is);
    }

    private Source codesXml(InputStream is) throws IOException, ParserConfigurationException, SAXException {
        final DocumentBuilder builder = dbf.newDocumentBuilder();
        final Document document = builder.parse(is);
        return new DOMSource(document.getFirstChild());
    }

    private String pathOfThisClass() {
        String path = getClass().getName();
        return path.substring(0, path.lastIndexOf('.') + 1).replace('.', '/');
    }
}
