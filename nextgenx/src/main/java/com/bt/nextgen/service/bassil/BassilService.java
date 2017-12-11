package com.bt.nextgen.service.bassil;

import ns.btfin_com.sharedservices.bpm.image.imageservice.imagereply.v1_0.SearchImagesResponseMsgType;

/**
 * Service to call the BASSIL operations
 * @author L055011
 *
 */
public interface BassilService
{
	/**
	 * Method to load the Images
	 * @return
	 */
	public SearchImagesResponseMsgType loadImages(String accountId);
}
