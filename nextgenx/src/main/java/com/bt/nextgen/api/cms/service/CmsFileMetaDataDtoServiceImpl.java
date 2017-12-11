package com.bt.nextgen.api.cms.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.cms.model.CmsDtoKey;
import com.bt.nextgen.api.cms.model.CmsFileMetaDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.cms.CmsCall;

/**
 * Created by L070589 on 23/02/2015.
 */
@Service
public class CmsFileMetaDataDtoServiceImpl implements CmsFileMetaDataDtoService {

    private static final String TRANSFORM_PROPERTIES = "cmstransform.properties";
    private static final String MOCK_CONTENT_FLAG = "aem.mock.content";
    private static final String MOCK_AEM_DIR = "aem.mock.dir";
    private static final String MOCK_CONTENT_FILE = "mock-content";

    @Autowired
    private CmsCall cmsCall;

    private static final Logger logger = LoggerFactory.getLogger(CmsFileMetaDataDtoServiceImpl.class);

    @Override
    public CmsFileMetaDto find(CmsDtoKey key, ServiceErrors serviceErrors) {
        String cmsResponse;
        try {
            logger.info("Value for MOCK_CONTENT_FLAG {}",com.bt.nextgen.core.util.Properties.getBoolean(MOCK_CONTENT_FLAG));
            cmsResponse = com.bt.nextgen.core.util.Properties.getBoolean(MOCK_CONTENT_FLAG)
                ? getMockContent() : cmsCall.sendAndReceiveFromCms(key);
        } catch (IOException e) {
            logger.error("Error Occurred while getting response from AEM {}", e);
            return null;
        }
        if (StringUtils.isNotBlank(cmsResponse)) {
            return convertToDto(transformProperties(cmsResponse), key);
        }
        return null;
    }

    /**
     * Transform the default AEM JSON properties to those used by web and mobile client
     *
     * @param cmsResponse
     * @return
     */
    private String transformProperties(String cmsResponse) {
        try {
            Properties props = PropertiesLoaderUtils.loadProperties(new ClassPathResource(TRANSFORM_PROPERTIES));
            return StringUtils.replaceEach(cmsResponse, props.keySet().toArray(new String[0]), props.values()
                .toArray(new String[0]));
        } catch (IOException e) {
            logger.error("Error loading transform properties file: {}", e);
            return cmsResponse;
        }
    }

    private CmsFileMetaDto convertToDto(String cmsResponse, CmsDtoKey key) {
        CmsFileMetaDto cmsFileMetaDto = new CmsFileMetaDto();
        cmsFileMetaDto.setKey(key);
        cmsFileMetaDto.setValue(cmsResponse);
        return cmsFileMetaDto;
    }

    private String getMockContent() {
        String mockContentPath = com.bt.nextgen.core.util.Properties.get(MOCK_AEM_DIR)
            + com.bt.nextgen.core.util.Properties.getString(MOCK_CONTENT_FILE);
        try {
            return FileUtils.readFileToString(new File(getClass().getClassLoader().getResource(mockContentPath).getFile()),
                StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("Error trying to retrieve mock content: {}", e);
            return null;
        }
    }
}
