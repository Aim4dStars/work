package com.bt.nextgen.service.cmis.constants;

import com.bt.nextgen.core.util.Properties;

/**
 * Consolidates all the constant values for Filenet integration.
 */
public final class DocumentConstants {

    private DocumentConstants() {
    }

    /**
     * Repository id of document management system. It is mandatory to have this for any operation on document management
     * system
     */
    public static final String REPOSITORY = Properties.getString("cmis.repository.id");

    /**
     * Service endpoint for CMIS discovery service used to query CMIS
     */
    public static final String SERVICE_TEMPLATE_QUERY = "cmisQuery";

    /**
     * Service endpoint for CMIS ObjectService used to upload new document, get folder id and other operations
     */
    public static final String SERVICE_TEMPLATE_OBJECT = "cmisObject";

    /**
     * Service endpoint for create object.
     */
    public static final String SERVICE_TEMPLATE_OBJECT_CREATE = "cmisCreateObject";

    /**
     * Service endpoint for create object.
     */
    public static final String SERVICE_TEMPLATE_GET_PATH = "cmisGetPath";

    /**
     * Service end point to update document meta data
     */
    public static final String SERVICE_TEMPLATE_UPDATE = "cmisPropertyUpdate";

    /**
     * Folder id for the document management server. Required to dynamically get the @code FOLDER_ID.
     */
    public static final String FOLDER_PATH = Properties.getString("cmis.folder.path");


    /**
     * Maximum results fetched from the filenet
     */
    public static final String MAX_RESULT = "1000";

    /**
     * Folder id column name
     */
    public static final String OBJECT_ID_COLUMN = "cmis:objectId";

    /**
     * Business area name for Panorama in FileNet
     */
    public static final String BUSINESS_AREA_PANORAMA = "PANORAMA";

    public static final String CHECKOUT_DOCUMENT = "cmisCheckout";

    public static final String CHECKIN_DOCUMENT = "cmisCheckin";

    public static final String DOCUMENT_VERSIONS = "cmisGetVersions";

    public static final String CANCEL_CHECKOUT = "cmisCancelCheckOut";

    public static final String DELETE_DOCUMENT = "cmisDeleteObject";

    /*
    * FileNet columns
    * */
    public static final String COLUMN_OBJECT_ID = "cmis:objectId";
    public static final String COLUMN_RELATIONSHIP_ID = "PanoramaIPRelationshipID";
    public static final String COLUMN_DOCUMENT_NAME = "PanoramaIPDocumentName";
    public static final String COLUMN_DOCUMENT_FILENAME = "cmis:contentStreamFileName";
    public static final String COLUMN_DOCUMENT_STATUS = "PanoramaIPDocumentStatus";
    public static final String COLUMN_DOCUMENT_TYPE = "PanoramaIPDocumentCategory";
    public static final String COLUMN_DOCUMENT_TITLE_CODE = "PanoramaIPDocumentTitleCode";
    public static final String COLUMN_AUDIT = "PanoramaIPAudit";
    public static final String COLUMN_MIME_TYPE = "cmis:contentStreamMimeType";
    public static final String COLUMN_UPLOADED_DATE = "DateCheckedIn";
    public static final String COLUMN_FINANCIAL_YEAR = "PanoramaIPFinancialYear";
    public static final String COLUMN_BUSINESS_AREA = "PanoramaIPBusinessArea";
    public static final String COLUMN_RELATIONSHIP_TYPE = "PanoramaIPRelationshipType";
    public static final String COLUMN_START_DATE = "PanoramaIPStartDate";
    public static final String COLUMN_END_DATE = "PanoramaIPEndDate";
    public static final String COLUMN_ADDEDBY_ROLE = "PanoramaIPAddedByRole";
    public static final String COLUMN_FILE_EXTENSION = "PanoramaIPDocumentFileExtension";
    public static final String COLUMN_VISIBILITY = "PanoramaIPVisibility";
    public static final String COLUMN_ORDER_ID = "PanoramaIPABSOrderID";
    public static final String COLUMN_ADDEDBY_NAME = "PanoramaIPAddedByName";
    public static final String COLUMN_CHANGE_TOKEN = "cmis:changeToken";
    public static final String COLUMN_SOURCE_ID = "PanoramaIPSourceID";
    public static final String COLUMN_ADDEDBY_ID = "PanoramaIPAddedByID";
    public static final String COLUMN_FILE_SIZE = "cmis:contentStreamLength";
    public static final String COLUMN_SUB_CATEGORY = "PanoramaIPDocumentSubCategory1";
    public static final String COLUMN_OBJECT_TYPE_ID = "cmis:objectTypeId";

    public static final String COLUMN_BATCH_ID = "PanoramaIPBatchID";
    public static final String COLUMN_EXTERNAL_ID = "PanoramaIPExternalID";
    public static final String COLUMN_EXPIRY_DATE = "PanoramaIPExpiryDate";
    public static final String COLUMN_MODEL_REPORT_ID = "PanoramaIPModelID";
    public static final String COLUMN_SUB_CATEGORY_2 = "PanoramaIPDocumentSubCategory2";
    public static final String COLUMN_ACTIVITY = "PanoramaIPActivity";
    public static final String COLUMN_LAST_MODIFIED_DATE = "cmis:lastModificationDate";

    /**
     * Base document class
     */
    public static final String DOCUMENT_CLASS = "PanoramaIPDocument";
    /*
    * Filenet Sub classes
    * */
    public static final String DOCUMENT_CLASS_SMSF = "PanoramaIPSMSFDocs";
    public static final String DOCUMENT_CLASS_CORROADHOC = "PanoramaIPCorroAdhoc";
    public static final String DOCUMENT_CLASS_CORROPOBOX = "PanoramaIPCorroPOBox";
    public static final String DOCUMENT_CLASS_MODELREPORT = "PanoramaIPIMModelReport";
    public static final String DOCUMENT_CLASS_STATEMENT = "PanoramaIPStatement";
    public static final String DOCUMENT_CLASS_OTHER_DOCUMENTS = "PanoramaIPOtherDocs";
    public static final String DOCUMENT_STATUS_DRAFT = "Draft";
    public static final String DOCUMENT_STATUS_FINAL = "Final";
    public static final String DOCUMENT_CLASS_OFFLINE = "PanoramaIPNotification";
    public static final String DOCUMENT_SUB_TYPE_ASSET_TRANSFER = "Asset Transfers";

    public static final String SOURCE_ID = "PanoramaUI";
    public static final String SERVICE_SOURCE_ID = "ServiceUI";
    public static final String COLUMN_DELETEDBY_ID = "PanoramaIPDeletedByID";
    public static final String COLUMN_RESTOREDBY_ID = "PanoramaIPRestoredByID";
    public static final String COLUMN_DELETED_ON = "PanoramaIPDeletedOn";
    public static final String COLUMN_RESTORED_ON = "PanoramaIPRestoredOn";
    public static final String COLUMN_DELETEDBY_ROLE = "PanoramaIPDeletedByRole";
    public static final String COLUMN_RESTOREDBY_ROLE = "PanoramaIPRestoredByRole";
    public static final String COLUMN_DELETEDBY_NAME = "PanoramaIPDeletedByName";
    public static final String COLUMN_RESTOREDBY_NAME = "PanoramaIPRestoredByName";
    public static final String COLUMN_DELETED = "PanoramaIPDeleted";
    public static final String COLUMN_PERMANENT = "PanoramaIPPermanent";
    public static final String COLUMN_UPDATEDBY_ID = "PanoramaIPUpdatedByID";
    public static final String COLUMN_UPDATEDBY_ROLE = "PanoramaIPUpdatedByRole";
    public static final String COLUMN_UPDATEDBY_NAME = "PanoramaIPUpdatedByName";

    public static final String RELATIONSHIP_TYPE_INVESTMENT_MANAGER = "INVST_MGR_POS";

}
