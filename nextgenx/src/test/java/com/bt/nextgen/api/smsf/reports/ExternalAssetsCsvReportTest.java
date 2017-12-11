package com.bt.nextgen.api.smsf.reports;

import com.bt.nextgen.api.smsf.constants.AssetClass;
import com.bt.nextgen.api.smsf.constants.AssetType;
import com.bt.nextgen.api.smsf.constants.PropertyType;
import com.bt.nextgen.api.smsf.model.*;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.PositionIdentifierImpl;
import com.bt.nextgen.service.integration.externalasset.model.ExternalAsset;
import com.bt.nextgen.service.integration.externalasset.model.OffPlatformExternalAssetImpl;
import com.bt.nextgen.service.integration.externalasset.model.OnPlatformExternalAssetImpl;
import com.bt.nextgen.service.integration.externalasset.service.ExternalAssetIntegrationService;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.*;

import static com.bt.nextgen.core.api.UriMappingConstants.ACCOUNT_ID_URI_MAPPING;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExternalAssetsCsvReportTest {
    public static final String ACCOUNT_ID = "abc123";
    public static final String MAP_KEY_HOLDING = "all";


    @Mock
    ContentDtoService contentDtoService;

    @InjectMocks
    ExternalAssetsCsvReport externalAssetCsvReport;

    @Mock
    private ExternalAssetIntegrationService externalAssetIntegrationService;

    @Test
    public void testDisclaimerFiltersOutSpecialCharacters() {
        ContentKey key = new ContentKey( "DS-IP-0062" );
        String cmsValue = "Hello <p></p> World";
        ContentDto content = new ContentDto( "DS-IP-0062", cmsValue );
        when( contentDtoService.find( any( ContentKey.class ), any( ServiceErrors.class ) ) ).thenReturn( content );
        assertEquals( "Hello  World", externalAssetCsvReport.getDisclaimer( new HashMap<String, String>() ) );
    }

    @Test
    public void getReportName() {
        assertThat( externalAssetCsvReport.getReportName( null ), equalTo( "External Assets valuation" ) );
    }

    @Test
    public void getStartDate() {
        final DateTime beforeDateTime = new DateTime();
        final DateTime startDate = externalAssetCsvReport.getStartDate( null );
        final DateTime afterDateTime = new DateTime();
        assertThat( "startDate after pre-method call", startDate.toDate().getTime() - beforeDateTime.toDate().getTime() >= 0, equalTo( true ) );
        assertThat( "startDate after method call", startDate.toDate().getTime() - afterDateTime.toDate().getTime() <= 0, equalTo( true ) );
    }

    @Test
    public void getExternalAssetHoldingsValuationWithoutAssets() {
        final Map<String, String> params = new HashMap();
        final Map<String, ExternalAssetHoldingsValuationDto> holdingsValuation;
        final Set<String> keys;
        final ExternalAssetHoldingsValuationDto dto;
        final List<ExternalAssetClassValuationDto> classValuationDtoList;
        List<ExternalAssetDto> assetList;

        params.put( ACCOUNT_ID_URI_MAPPING, EncodedString.fromPlainText( ACCOUNT_ID ).toString() );
        when( externalAssetIntegrationService.getExternalAssets( anyList(), any( DateTime.class ) ) ).thenReturn( getAssetHoldings() );

        holdingsValuation = externalAssetCsvReport.getExternalAssetHoldingsValuation( params );
        assertThat( "holdingsValuation not null", holdingsValuation, notNullValue() );
        assertThat( "holdingsValuation size", holdingsValuation.size(), equalTo( 1 ) );

        keys = holdingsValuation.keySet();
        assertThat( "holdingsValuation key", keys.toArray( new String[keys.size()] )[0], equalTo( MAP_KEY_HOLDING ) );

        dto = holdingsValuation.get( MAP_KEY_HOLDING );
        assertThat( "DTO not null", dto, notNullValue() );
        classValuationDtoList = dto.getValuationByAssetClass();
        assertThat( "class valuation DTOs not null", classValuationDtoList, notNullValue() );
        assertThat( "class valuation DTOs not empty", classValuationDtoList.size(), equalTo( 1 ) );

        assetList = classValuationDtoList.get( 0 ).getAssetList();
        assertThat( "asset list not null", assetList, notNullValue() );
        assertThat( "asset list empty", assetList.size(), equalTo( 0 ) );
    }

    @Test
    public void getExternalAssetHoldingsValuationWithOneAsset() {
        final DateTime valueDate = new DateTime();
        final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern( "dd MMM yyyy" );
        final Map<String, String> params = new HashMap();
        final Map<String, ExternalAssetHoldingsValuationDto> holdingsValuation;
        final Set<String> keys;
        final ExternalAssetHoldingsValuationDto dto;
        final List<ExternalAssetClassValuationDto> classValuationDtoList;
        List<ExternalAssetDto> assetList;
        ExternalAssetDto asset;

        params.put( ACCOUNT_ID_URI_MAPPING, EncodedString.fromPlainText( ACCOUNT_ID ).toString() );
        when( externalAssetIntegrationService.getExternalAssets( anyList(), any( DateTime.class ) ) ).thenReturn( getAssetHoldings( getListedSecurityAsset( valueDate ) ) );

        holdingsValuation = externalAssetCsvReport.getExternalAssetHoldingsValuation( params );
        assertThat( "holdingsValuation not null", holdingsValuation, notNullValue() );
        assertThat( "holdingsValuation size", holdingsValuation.size(), equalTo( 1 ) );

        keys = holdingsValuation.keySet();
        assertThat( "holdingsValuation key", keys.toArray( new String[keys.size()] )[0], equalTo( MAP_KEY_HOLDING ) );

        dto = holdingsValuation.get( MAP_KEY_HOLDING );
        assertThat( "DTO not null", dto, notNullValue() );
        classValuationDtoList = dto.getValuationByAssetClass();
        assertThat( "class valuation DTOs not null", classValuationDtoList, notNullValue() );
        assertThat( "class valuation DTOs not empty", classValuationDtoList.size(), equalTo( 1 ) );

        assetList = classValuationDtoList.get( 0 ).getAssetList();
        assertThat( "asset list not null", assetList, notNullValue() );
        assertThat( "asset list empty", assetList.size(), equalTo( 1 ) );

        asset = assetList.get( 0 );
        assertThat( "marketValue", asset.getMarketValue(), equalTo( "48,000.00" ) );
        assertThat( "valueDate", asset.getValueDate(), equalTo( valueDate.toString( dateTimeFormatter ) ) );
    }

    @Test
    public void getExternalAssetHoldingsValuationOneNonPanoramaAsset() {
        final DateTime valueDate = new DateTime();
        final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern( "dd MMM yyyy" );
        final Map<String, String> params = new HashMap();
        final Map<String, ExternalAssetHoldingsValuationDto> holdingsValuation;
        final Set<String> keys;
        final ExternalAssetHoldingsValuationDto dto;
        final List<ExternalAssetClassValuationDto> classValuationDtoList;
        List<ExternalAssetDto> assetList;
        ExternalAssetDto asset;

        params.put( ACCOUNT_ID_URI_MAPPING, EncodedString.fromPlainText( ACCOUNT_ID ).toString() );
        when( externalAssetIntegrationService.getExternalAssets( anyList(), any( DateTime.class ) ) ).thenReturn( getAssetHoldings( getNonPanoramaAsset( valueDate ) ) );

        holdingsValuation = externalAssetCsvReport.getExternalAssetHoldingsValuation( params );
        assertThat( "holdingsValuation not null", holdingsValuation, notNullValue() );
        assertThat( "holdingsValuation size", holdingsValuation.size(), equalTo( 1 ) );

        keys = holdingsValuation.keySet();
        assertThat( "holdingsValuation key", keys.toArray( new String[keys.size()] )[0], equalTo( MAP_KEY_HOLDING ) );

        dto = holdingsValuation.get( MAP_KEY_HOLDING );
        assertThat( "DTO not null", dto, notNullValue() );
        classValuationDtoList = dto.getValuationByAssetClass();
        assertThat( "class valuation DTOs not null", classValuationDtoList, notNullValue() );
        assertThat( "class valuation DTOs not empty", classValuationDtoList.size(), equalTo( 1 ) );

        assetList = classValuationDtoList.get( 0 ).getAssetList();
        assertThat( "asset list not null", assetList, notNullValue() );
        assertThat( "asset list empty", assetList.size(), equalTo( 1 ) );

        asset = assetList.get( 0 );
        assertThat( "propertyType", asset.getPropertyType(), notNullValue() );
        assertThat( "propertyType value", asset.getPropertyType(), equalTo( "Residential" ) );
        assertThat( "marketValue", asset.getMarketValue(), equalTo( "48,000.00" ) );
        assertThat( "valueDate", asset.getValueDate(), equalTo( valueDate.toString( dateTimeFormatter ) ) );
    }

    private AssetClassValuation getNonPanoramaAsset(DateTime valueDate) {
        final AssetClassValuation valuation = new AssetClassValuationImpl( AssetClass.AUSTRALIAN_REAL_ESTATE );
        final OffPlatformExternalAssetImpl asset = new OffPlatformExternalAssetImpl();

        asset.setPositionCode( "EST1156ER" );
        asset.setAssetType( AssetType.MANAGED_PORTFOLIO );
        asset.setPositionName( "151 Clarence Street, Sydney, NSW 2000" );
        asset.setAssetClass( AssetClass.AUSTRALIAN_REAL_ESTATE );
        asset.setMarketValue( new BigDecimal( 48000 ) );
        asset.setValueDate( valueDate );
        asset.setPositionIdentifier( new PositionIdentifierImpl( "29999" ) );
        asset.setPropertyType( PropertyType.getByCode( "res" ) );

        valuation.setAssets( Arrays.<ExternalAsset>asList( asset ) );

        return valuation;
    }


    private AssetClassValuation getListedSecurityAsset(DateTime valueDate) {
        final AssetClassValuation valuation = new AssetClassValuationImpl( AssetClass.AUSTRALIAN_LISTED_SECURITIES );
        final ExternalAsset listedSecurityAsset = new OnPlatformExternalAssetImpl();

        listedSecurityAsset.setAssetName( "BHP" );
        listedSecurityAsset.setPositionCode( "BHP" );
        listedSecurityAsset.setAssetClass( AssetClass.AUSTRALIAN_LISTED_SECURITIES );
        listedSecurityAsset.setSource( "ETRADE" );
        listedSecurityAsset.setMarketValue( new BigDecimal( 48000 ) );
        listedSecurityAsset.setValueDate( valueDate );
        listedSecurityAsset.setPositionIdentifier( new PositionIdentifierImpl( "12345" ) );
        listedSecurityAsset.setQuantity( new BigDecimal( 150 ) );
        listedSecurityAsset.setAssetType( AssetType.AUSTRALIAN_LISTED_SECURITIES );
        listedSecurityAsset.setAssetClass( AssetClass.CASH );

        valuation.setAssets( Arrays.asList( listedSecurityAsset ) );

        return valuation;
    }

    private AssetHoldings getAssetHoldings(AssetClassValuation... valuations) {
        final AssetHoldings holdings = new AssetHoldingsImpl();

        holdings.setAssetClassValuations( Arrays.asList( valuations ) );

        return holdings;
    }

}
