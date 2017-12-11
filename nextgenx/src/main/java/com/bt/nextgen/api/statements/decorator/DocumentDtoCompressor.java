package com.bt.nextgen.api.statements.decorator;

import com.bt.nextgen.api.statements.model.DocumentDto;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * consumer
 */
public class DocumentDtoCompressor {
    private static final Logger logger = LoggerFactory.getLogger(DocumentDtoCompressor.class);

    private List<DocumentDto> documents;
    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("ddMMYYYY_HHmmss");

    public DocumentDtoCompressor(List<DocumentDto> documents) {
        this.documents = documents;
    }

    public DocumentDto getDto() {
        DocumentDto dto = new DocumentDto();
        dto.setDocumentBytes(loadDocumentZipped(documents));
        dto.setFileExtension("zip");
        return dto;
    }

    public String getCreateName(String accountNumber, String accountName) {
        String dateTime = formatter.print(System.currentTimeMillis());
        return "Download_" + accountName + "_" + accountNumber + "_" + dateTime + ".zip";
    }

    public byte[] loadDocumentZipped(List<DocumentDto> documentsDtos) {
        if(documentsDtos ==  null || documentsDtos.isEmpty())
            throw new IllegalArgumentException("DocumentList is empty");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        List<DocumentDto> updatedDocumentsDtos = updateDocumentNameIfDuplicate(documentsDtos);
        try {
            for (DocumentDto doc : updatedDocumentsDtos) {
                ZipEntry entry = new ZipEntry(doc.getDocumentName());
                byte[] input = doc.getDocumentBytes();
                entry.setSize(input.length);
                zos.putNextEntry(entry);
                zos.write(input);
            }
            zos.closeEntry();
            zos.close();
        } catch (IOException e) {
            logger.error("Unable to zip file." + e);
        }
        return baos.toByteArray();
    }

    private Map<String, List<DocumentDto>> groupByName(List<DocumentDto> documents) {
        Map<String, List<DocumentDto>> groupsByDocumentName = new HashMap<>();
        for (DocumentDto documentDto : documents) {
            List<DocumentDto> groupedDocumentDto = groupsByDocumentName.get(documentDto.getDocumentName());
            if (null == groupedDocumentDto) {
                groupedDocumentDto = new ArrayList<>();
                groupsByDocumentName.put(documentDto.getDocumentName(), groupedDocumentDto);
            }
            groupedDocumentDto.add(documentDto);
        }
        return groupsByDocumentName;
    }

    private List<DocumentDto> updateDocumentNameIfDuplicate(List<DocumentDto> documents) {
        List<DocumentDto> documentsWithUpdatedNames = new ArrayList<>();
        Map<String, List<DocumentDto>> groupsByDocumentName = groupByName(documents);
        for (Map.Entry<String,List<DocumentDto>> entry : groupsByDocumentName.entrySet()) {
            List<DocumentDto> documentDtos = entry.getValue();
            if (documentDtos.size()>1) {
                for (int documentIndex = 1; documentIndex<documentDtos.size(); documentIndex++) {
                    DocumentDto dto = documentDtos.get(documentIndex);
                    if(dto.getDocumentName().indexOf(".") > 0) {
                        StringTokenizer splitDocumentName = new StringTokenizer(dto.getDocumentName(), ".");
                        dto.setDocumentName(splitDocumentName.nextToken()+"("+documentIndex+")."+splitDocumentName.nextToken());
                    }
                    else {
                        dto.setDocumentName(dto.getDocumentName()+"("+documentIndex+")");
                    }

                    documentsWithUpdatedNames.add(dto);
                }
            }
            documentsWithUpdatedNames.add(documentDtos.get(0));
        }
        return documentsWithUpdatedNames;
    }
}
