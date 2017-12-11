package com.bt.nextgen.api.adviser.controller;

import com.bt.nextgen.api.asset.controller.AssetApiController;
import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.ResultListDto;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ShareAssetApiControllerIntegrationTest extends BaseSecureIntegrationTest {

    @Autowired
    private AssetApiController assetApiController;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testShareListForAShare() throws Exception {

        ApiResponse response = assetApiController.searchAssets("westpac", "SHARE", null);
        assertThat(response, is(notNullValue()));
        ResultListDto<AssetDto> shareDtoList = (ResultListDto<AssetDto>) response.getData();
        List<AssetDto> listDto = shareDtoList.getResultList();
        for (AssetDto shareDto : listDto) {
            assertThat(shareDto.getAssetId(), is("110521"));
            assertThat(shareDto.getAssetName(), is("Westpac Banking Corporation"));
            assertThat(shareDto.getAssetType(), is("shs"));
        }
    }

    @Test
    public void testShareListAString() throws Exception {

        ApiResponse response = assetApiController.searchAssets("Common", "SHARE", null);
        assertThat(response, is(notNullValue()));
        ResultListDto<AssetDto> shareDtoList = (ResultListDto<AssetDto>) response.getData();
        List<AssetDto> listDto = shareDtoList.getResultList();
        assertThat(listDto.size(), is(2));

    }

    @Test
    public void testShareListSortByAssetCode() throws Exception {

        ApiResponse response = assetApiController.searchAssets("Common", "SHARE", "assetCode");
        assertThat(response, is(notNullValue()));
        ResultListDto<AssetDto> shareDtoList = (ResultListDto<AssetDto>) response.getData();
        List<AssetDto> listDto = shareDtoList.getResultList();
        assertThat(listDto.size(), is(2));
        assertThat(listDto.get(0).getAssetId(), is("110320"));
        assertThat(listDto.get(0).getAssetName(), is("Commonwealth Bank of Australia"));
        assertThat(listDto.get(0).getAssetType(), is("shs"));
    }
}

