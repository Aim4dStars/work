package com.bt.nextgen.api.cms;

import com.bt.nextgen.api.cms.model.CmsDtoKey;
import com.bt.nextgen.api.cms.model.CmsFileMetaDto;
import com.bt.nextgen.api.cms.service.CmsFileMetaDataDtoServiceImpl;
import com.bt.nextgen.core.util.Properties;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.cms.CmsCall;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by L070589 on 25/02/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class CmsFileMetaDataDtoServiceTest {

    private static final String MOCK_CONTENT_FLAG = "aem.mock.content";
    @InjectMocks
    private CmsFileMetaDataDtoServiceImpl cmsFileMetaDataDtoService;

    @Mock
    private CmsCall cmsCall;

    String response = "[\n" +
            "  {\n" +
            "    \"jcr:primaryType\": \"nt:unstructured\",\n" +
            "    \"jcr:createdBy\": \"admin\",\n" +
            "    \"fileReference\": \"app/style/images/PanoramaOverviewHero.png\",\n" +
            "    \"jcr:lastModifiedBy\": \"admin\",\n" +
            "    \"altText\": \"BT Panorama\",\n" +
            "    \"jcr:created\": \"2015-04-29T07:05:22.497+05:30\",\n" +
            "    \"text\": \"Panorama allows you to easily manage your wealth by putting cash, term deposits and managed portfolios in one place. Easy online access to your investments gives you the control and flexibility to administer your investments.\",\n" +
            "    \"title\": \"A new experience in wealth management\",\n" +
            "    \"jcr:lastModified\": \"2015-04-29T07:07:07.343+05:30\",\n" +
            "    \"sling:resourceType\": \"panorama/components/content/title-text-image\",\n" +
            "    \"textIsRich\": \"true\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"assetList\": [\n" +
            "      {\n" +
            "        \"title\": \"Panorama IDPS\",\n" +
            "        \"name\": \"Panorama_Investor_Guide.pdf\",\n" +
            "        \"path\": \"../../public/static/pdf/Panorama_Investor_Guide.pdf\",\n" +
            "        \"type\": \"application/pdf\",\n" +
            "        \"size\": \"324608\",\n" +
            "        \"tags\": \"panorama:PROD-WL-35D1B6570418\",\n" +
            "        \"date\": \"2015-03-16T11:51:46.000+05:30\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"title\": \"Asset Administrator IDPS\",\n" +
            "        \"name\": \"IOOF_Panorama_Investors_Guide_0215px.pdf\",\n" +
            "        \"path\": \"../../public/static/pdf/IOOF_Panorama_Investors_Guide_0215px.pdf\",\n" +
            "        \"type\": \"application/pdf\",\n" +
            "        \"size\": \"311296\",\n" +
            "        \"tags\": \"panorama:PROD-WL-060F52DC6D17421EAF1632AC9EFAE210\",\n" +
            "        \"date\": \"2015-03-16T11:11:47.000+05:30\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"title\": \"Panorama FSG\",\n" +
            "        \"name\": \"Panorama FSG_V0 4.pdf\",\n" +
            "        \"path\": \"../../public/static/pdf/Panorama FSG_V0 4.pdf\",\n" +
            "        \"type\": \"application/pdf\",\n" +
            "        \"size\": \"147456\",\n" +
            "        \"tags\": \"panorama:PROD-WL-35D1B6570418\",\n" +
            "        \"date\": \"2015-03-16T04:36:00.000Z\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"title\": \"Panorama AIB\",\n" +
            "        \"name\": \"Panorama Additional Info and Terms.pdf\",\n" +
            "        \"path\": \"../../public/static/pdf/Panorama Additional Info and Terms.pdf\",\n" +
            "        \"type\": \"application/pdf\",\n" +
            "        \"size\": \"236544\",\n" +
            "        \"date\": \"2015-03-16T04:36:00.000Z\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "]";


    @Test
    public void testFindResource() throws Exception {
        Properties.all().put("aem.mock.content", false);
        Mockito.when(cmsCall.sendAndReceiveFromCms(Mockito.any(CmsDtoKey.class))).thenReturn(response);
        CmsFileMetaDto dto = cmsFileMetaDataDtoService.find(new CmsDtoKey("AEM.Pro.IP-0001", "bt"), new FailFastErrorsImpl());
        Assert.assertNotNull(dto);
        Assert.assertEquals(dto.getValue().lastIndexOf("Panorama AIB"), 1786);
        Assert.assertEquals(dto.getValue().length(), 2064);
        Assert.assertEquals(dto.getKey().getKey(), "AEM.Pro.IP-0001");
        Assert.assertEquals(dto.getKey().getQuery(), "bt");
        Assert.assertEquals(dto.getType(), "CmsFileMeta");

    }

    @Test
    public void testFindMockResource() throws Exception {
        Properties.all().setProperty("aem.mock.content","true");
        CmsFileMetaDto dto = cmsFileMetaDataDtoService.find(new CmsDtoKey("any-key", ""), new FailFastErrorsImpl());
        Assert.assertEquals(Properties.getBoolean(MOCK_CONTENT_FLAG),true);
        Assert.assertNotNull(dto);
        Assert.assertEquals(dto.getValue().lastIndexOf("path"), 1914);
        Assert.assertEquals(dto.getValue().length(), 2117);
        Assert.assertEquals(dto.getKey().getKey(), "any-key");
        Assert.assertEquals(dto.getKey().getQuery(), "");
        Assert.assertEquals(dto.getType(), "CmsFileMeta");
    }


}
