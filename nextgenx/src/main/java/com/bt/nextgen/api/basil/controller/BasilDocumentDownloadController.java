package com.bt.nextgen.api.basil.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.btfin.panorama.core.security.aes.AESEncryptService;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.bt.nextgen.api.basil.UriConfig;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import com.bt.nextgen.service.avaloq.basil.DocumentType;

/**
 * Created by M035995 on 7/11/2016.
 */
@Controller
public class BasilDocumentDownloadController {

    @Value("${eam.aes.encryption.key}")
    private String encryptionKey;

    @Value("${basil.user}")
    private String userName;

    @Value("${basil.encryption.key}")
    private String basilEncryptionKey;

    @Value("${basil.pdf.url}")
    private String documentUrl;

    @Autowired
    private AESEncryptService aesEncryptService;

    private static final Logger LOGGER = LoggerFactory.getLogger(BasilDocumentDownloadController.class);

    /**
     * Size of a byte buffer to read/write file
     */
    private static final int BUFFER_SIZE = 4096;

    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports') and @basilPermissionService.hasBasilDocumentAccess(#accountId, #documentId)")
    @RequestMapping(method = RequestMethod.GET, value = "${api.basil.v3.uri}")
    public void downloadBasilDocuments(HttpServletResponse response,
                                       @RequestParam(value = UriConfig.DOCUMENT_ID) String documentId, @PathVariable(value = "account-id") String accountId,
                                       @RequestParam(value = UriConfig.MIME_TYPE) String mimeType,
                                       @RequestParam(value = UriConfig.DOCUMENT_TYPE) String documentType) throws Exception {

        if (StringUtils.isEmpty(mimeType) || StringUtils.isEmpty(documentType)) {
            throw new BadRequestException("Either mimeType or documentType is null. The system cannot download insurance documents");
        }

        //Throw an error in case the mimeType & document types are not white-listed. Adding these checks to avoid fortify issues.
        final String[] arguments = mimeType.split(";");
        final String mimeTypeForDoc = arguments[0];
        if (!DocumentType.isDocumentTypeValid(documentType) && !isMimeTypeValid(mimeTypeForDoc)) {
            throw new BadRequestException("Either mimeType or documentType value is invalid. The system cannot download insurance documents");
        }

        Authenticator.setDefault(new CustomAuthenticator());
        final URL url = new URL(documentUrl + ConsistentEncodedString.toPlainText(documentId));
        LOGGER.info("Initialising URL:" + url.toURI());
        final URLConnection urlConnection = url.openConnection(Proxy.NO_PROXY);
        urlConnection.setDoOutput(true);
        final InputStream inputStream = urlConnection.getInputStream();
        // The mimeType contains and output like "application/pdf;name=\"archive.pdf\""
        // Split the information and set the name of the file as an attachment

        response.setContentType(mimeTypeForDoc);
        final String fileName = StringUtils.isNotEmpty(arguments[1]) ? arguments[1].split("=")[1].replace("\\", "") : documentType;
        LOGGER.info("Getting InputStream from the URLConnection; MimeType is:" + mimeTypeForDoc + " and FileName is:" + fileName);

        // Get the fileName from response; however get the extension type from name in mimeType from Basil
        response.setHeader("Content-Disposition", "attachment; filename=" + documentType +
                (fileName.lastIndexOf(".") != -1 ? fileName.substring(fileName.lastIndexOf(".")).replace("\"", "") : ""));
        final OutputStream outStream = response.getOutputStream();
        final byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;
        // write bytes read from the input stream into the output stream
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
        inputStream.close();
        outStream.close();

    }

    class CustomAuthenticator extends Authenticator {
        protected PasswordAuthentication getPasswordAuthentication() {
            try {
                return new PasswordAuthentication(userName, aesEncryptService.decrypt(basilEncryptionKey).toCharArray());
            } catch (Exception e) {
                LOGGER.error("error decrypting password", e);
                throw new IllegalStateException("error decrypting password");
            }
        }
    }

    private boolean isMimeTypeValid(String mimeType) {
        final List<String> mimeTypeList = Arrays.asList("application/pdf", "application/zip", "application/msword", "text/csv");
        if (mimeTypeList.contains(mimeType)) {
            return true;
        }
        return false;
    }
}
