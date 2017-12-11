/**
 * @namespace org.bt.modules.referenceData
 */
org.bt.modules.referenceData =  (function($,window,document,moment) {

 return{
         /**
         * @memberOf org.bt.modules.referenceData
         * @public
         * @constant {String} SMSF
         */
        SMSF : 'SMSF',
        SMSF_FUND_ADMINISTRATION :'SMSF Fund Administration',


         /**
         * @memberOf org.bt.modules.referenceData
         * @public
         * @constant {String} INV
         */
        INVESTMENT : 'INV',

        /**
         * @memberOf org.bt.modules.referenceData
         * @public
         * @constant {String} SMSF
         */
        STATEMENT : 'STM',

        /**
         * @memberOf org.bt.modules.referenceData
         * @public
         * @constant {String} POBox
         */
        POBox : 'POBOX',

        /**
         * @memberOf org.bt.modules.referenceData
         * @public
         * @constant {String} EMAIL
         */
        EMAIL : 'EMAIL',

        /**
         * @memberOf org.bt.modules.referenceData
         * @public
         * @constant {String} SCANNED
         */
        SCANNED : 'SCANNED',

        /**
         * @memberOf org.bt.modules.referenceData
         * @public
         * @constant {String} IMMODELRPT
         */
        IMMODELRPT : 'IMMODELRPT',

         /**
           * @memberOf org.bt.modules.referenceData
           * @public
           * @constant {String} IMMODELRPT
           */

        APPROVAL  : 'APPROVAL',

        /**
         * @memberOf org.bt.modules.referenceData
         * @public
         * @constant {String} TAXSUPER
         */

        TAXSUPER: 'TAXSUPER',


        /**
         * @memberOf org.bt.modules.referenceData
         * @public
         * @constant [Array] of document categories text and values
         */
        DOCUMENT_CATEGORIES : [{text:'Advice Documentation', value:'ADVICE'},{text:'Approval',value:'APPROVAL'},{text:'Correspondence', value:'CORRO'},
          {text:'Email', value:'EMAIL'},{text:'Fax', value:'FAX'},
          {text:'Investments', value:'INV'},{text:'Model Report', value:'IMMODELRPT'},{text:'PO Box', value:'POBOX'},
          {text:'SMSF', value:'SMSF'},{text:'Scanned', value:'SCANNED'},{text:'Statements', value:'STM'},{text:'Tax', value:'TAXSUPER'},
          {text:'Tax returns', value:'TAX'},{text:'Other', value:'OTHER'}],

        /**
         * @memberOf org.bt.modules.referenceData
         * @public
         * @constant [Array] of SMSF sub categories text and values
         */
        SMSF_SUB_CATEGORIES : [ {text:'Any', value:'Any'},{text:'Company', value:'Company'},
          {text:'SMSF Fund Administration', value:'SMSF Fund Administration'},
          {text:'SMSF Fund Establishment', value:'SMSF Fund Establishment'},
          {text:'SMSF General', value:'SMSF General'}],

        /**
         * @memberOf org.bt.modules.referenceData
         * @public
         * @constant [Array] of SMSF  Fund Administration sub categories text and values
         */
        SMSF_FADM_SUB_CATEGORIES : [ {text:'Any', value:'Any'},{text:'Assets', value:'ASSETS'},
          {text:'Contributions', value:'CONTRIBS'},{text:'Expenses', value:'EXPENSES'},
          {text:'Financial statements and tax documents', value:'FNSTMTAX'},
          {text:'General', value:'GENERAL'},{text:'Income', value:'INCOME'},
          {text:'Liabilities', value:'LIABS'},{text:'Master documents', value:'MSTDOCS'},
          {text:'Pensions', value:'PENSIONS'}],

        /**
         * @memberOf org.bt.modules.referenceData
         * @public
         * @constant [Array] of investment sub categories text and values
         */
        INVESTMENT_SUB_CATEGORIES :  [ {text:'Any', value:'Any'},{text:'Corporate Actions', value:'Corporate Actions'},
          {text:'Transaction Confirmations', value:'Transaction Confirmations'},{text:'Asset Transfers', value:'Asset Transfers'}],

          ASSET_TITLE_CODES : [{text : 'Any',value:'Any'},{text:'Australian Standard Transfer Form',value:'ATASTF'},
          {text:'Limited Power of Attorney',value:'ATLPOA'}],

        /**
         * @memberOf org.bt.modules.referenceData
         * @public
         * @constant [Array] of document categories text and values
         */
        CUSTOMER_CATEGORIES : [{text:'Email', value:'EMAIL'},{text:'Fax', value:'FAX'},
          {text:'PO Box', value:'POBOX'},{text:'Scanned', value:'SCANNED'}],

        /**
         * @memberOf org.bt.modules.referenceData
         * @public
         * @constant [Array] of document title codes text and values
         */
        SMSF_DOCUMENT_TITLE_CODES : [{text:'Any', value:'Any'},{text:'Ancilliary Pack', value:'SMCPAC'},
          {text:'SMSF Trust Deed', value:'SMISTR'},{text:'Company Constitution', value:'SMCCON'},
          {text:'Consent to Appoint as a trustee', value:'SMICAT'},
          {text:'Trustee Declaration for Ind', value:'SMITRD'},{text:'Trustee Resolution to set up fund', value:'SMITRR'},
          {text:'Investment Strategy (Minute)', value:'SMIINS'},{text:'SMSF PDS -  (Mandatory)', value:'SMIPDS'},
          {text:'Member Applications (1 per member)', value:'SMIMAP'},
          {text:'Trust Deed letter of compliance', value:'SMICOM'},{text:'SMSF Tax Invoice',value:'SMTXIN'},{text:'Welcome Pack', value:'SMAPAC'},
           {text:'Confirmation Letter', value:'SMACLT'}],

        SMSF_FUND_ADMINISTRATION_TITLE_CODES:[{text:'Any',value:'Any'},{text:'SMSF Tax Invoice',value:'SMTXIN'},{text:'Welcome Pack', value:'SMAPAC'},
        {text:'Confirmation Letter', value:'SMACLT'}],

         SMSF_FUND_ESTABLISHMENT_TITLE_CODES : [{text:'Any', value:'Any'},{text:'Ancilliary Pack', value:'SMCPAC'},
                  {text:'SMSF Trust Deed', value:'SMISTR'},{text:'Company Constitution', value:'SMCCON'},
                  {text:'Consent to Appoint as a trustee', value:'SMICAT'},
                  {text:'Trustee Declaration for Ind', value:'SMITRD'},{text:'Trustee Resolution to set up fund', value:'SMITRR'},
                  {text:'Investment Strategy (Minute)', value:'SMIINS'},{text:'SMSF PDS -  (Mandatory)', value:'SMIPDS'},
                  {text:'Member Applications (1 per member)', value:'SMIMAP'},
                  {text:'Trust Deed letter of compliance', value:'SMICOM'} ],


        /**
         * @memberOf org.bt.modules.referenceData
         * @public
         * @constant [Array] of document statements title codes text and values
         */
        STATEMENT_TITLE_CODES : [{text:'Any', value:'Any'},{text:'Annual Centrelink Schedule', value:'SPCENA'},{text:'Annual Statement', value:'STMANN'},{text: 'Cash Management Account Bank Statement', value:'CMASTM'}, {text:'Centrelink Schedule', value:'SPCENT'},{text:'Exit Statement', value:'EXTSTM'},
          {text:'Fee Revenue Statement', value:'FRSSTM'},{text:'Full Exit Pack', value:'SPFEPK'} , {text:'IMRCTI', value:'IMRCTI'},{text:'Partial Withdrawal Pack', value:'SPPWPK'} ,{text:'PAYG Statement', value:'PYGSTM'},
          {text:'Quarterly Statement', value:'QTRSTM'},{text:'Rollover Benefit Statement', value:'SPRBEN'} , {text:'Super Exit Statement', value:'SPEXIT'} , {text:'Tax Statement', value:'STMTAX'}],

        /**
         * @memberOf org.bt.modules.referenceData
         * @public
         * @constant [Array] of document statements title codes text and values
         * @Bussiness value to document id for tax documents
         */
        TAXSUPER__TITLE_CODES: [{text:'Any', value:'Any'}, {text:'Annual PAYG Income Stream', value:'SPPGIA'}, {text:'PAYG Payment Summary (Lump Sum)', value:'SPPYGL'},
            {text:'PAYG Payment Summary (Income Stream)', value:'SPPYGI'}, {text:'Personal Tax Deduction Notice Acknowledgement', value:'SPTXAC'}],




        /**
         * @memberOf org.bt.modules.referenceData
         * @public
         * @constant [Array] of document statements title codes text and values
         */
          APPROVAL_TITLE_CODES : [{text:'Any' , value:'Any'},{text:'Offline Approval',value:'OFFAPR'}],

        /**
         * @memberOf org.bt.modules.referenceData
         * @public
         * @constant [Array] of sources for POBox text and values
         */
        POBOX_SOURCES : [{text:'Any', value:'Any'},{text:'ECMNGEN', value:'ECMNGEN'},{text:'NGENAMD', value:'NGENAMD'},
          {text:'NGENAPP', value:'NGENAPP'},{text:'NGENPMT', value:'NGENPMT'},
          {text:'NGENRED', value:'NGENRED'},{text:'NGENTD', value:'NGENTD'},
          {text:'NGENSMSFFE', value:'NGENSMSFFE'},{text:'NGENSMSFFA', value:'NGENSMSFFA'},
          {text:'NGENAT', value:'NGENAT'},{text:'NGENTRNS', value:'NGENTRNS'}, {text:'NGENSMSFAL', value:'NGENSMSFAL'}],

        /**
         * @memberOf org.bt.modules.referenceData
         * @public
         * @constant [Array] of sources for POBox text and values
         */
        SOURCES : [{text:'Any', value:'Any'},{text:'Upload', value:'Upload'},{text:'HP', value:'HP'}],

        /**
         * @memberOf org.bt.modules.referenceData
         * @public
         * @constant [Array] of relationship types text and values
         */
        RELATIONSHIP_TYPES : [{text:'Account', value:'ACCT'},{text:'Adviser Position', value:'AVSR_POS'},
          {text:'Counter Party ', value:'CNTPRTY'},{text:'Contracting Entity', value:'CE'},{text:'Customer', value:'CUST'},
          {text:'Dealer Group', value:'DG'},{text:'Issuer', value:'ISSUER'},
          {text:'Investment Manager Position', value:'INVST_MGR_POS'},
          {text:'Office', value:'OFFICE'},{text:'Practice', value:'PRACTICE'},
          {text:'Super Dealer Group', value:'SUPER_DG'}],


    };
})(jQuery,window, document, moment);