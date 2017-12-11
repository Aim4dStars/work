package com.bt.nextgen.api.corporateaction.v1.model;

import com.fasterxml.jackson.annotation.JsonView;

import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

public class CorporateActionOptionDto extends BaseDto implements KeyedDto<Integer> {
	@JsonView(JsonViews.Write.class)
	private Integer id;

	private String title;

	@JsonView(JsonViews.Write.class)
	private String summary;

	private Boolean isNoAction;
	private Boolean isDefault;

	public CorporateActionOptionDto() {
		super();
	}

	/**
	 * The main constructor
	 *
	 * @param id        the id of the option, which correspond to the index in Avaloq options
	 * @param title     the title of the option (e.g Option A, Option B)
	 * @param summary   the summary of the option
	 * @param isDefault default flag true/false
	 */
	public CorporateActionOptionDto(Integer id, String title, String summary, Boolean isDefault) {
		super();
		this.id = id;
		this.title = title;
		this.summary = summary;
		this.isNoAction = Boolean.FALSE;
		this.isDefault = isDefault;
	}

	/**
	 * The main constructor
	 *
	 * @param id         the id of the option, which correspond to the index in Avaloq options
	 * @param title      the title of the option (e.g Option A, Option B)
	 * @param summary    the summary of the option
	 * @param isNoAction flag for no-action option
	 * @param isDefault  default flag true/false
	 */
	public CorporateActionOptionDto(Integer id, String title, String summary, Boolean isNoAction, Boolean isDefault) {
		super();
		this.id = id;
		this.title = title;
		this.summary = summary;
		this.isNoAction = isNoAction;
		this.isDefault = isDefault;
	}

	/**
	 * This method is used by the JSON mapper - do not remove!
	 *
	 * @param id        the id of option
	 * @param summary   the summary text of option
	 * @param isDefault true/false flag
	 */
	public CorporateActionOptionDto(Integer id, String summary, Boolean isDefault) {
		super();
		this.id = id;
		this.summary = summary;
		this.isDefault = isDefault;
	}

	/**
	 * The id of the option, which correspond to the index in Avaloq options
	 *
	 * @return the option ID
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * The title of the option (e.g Option A, Option B)
	 *
	 * @return string title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * The summary of the option
	 *
	 * @return summary of option
	 */
	public String getSummary() {
		return summary;
	}

	/**
	 * Default/non-default flag
	 *
	 * @return default/non-default
	 */
	public Boolean getIsDefault() {
		return isDefault;
	}

	/**
	 * Flag to denote whether this is the no-action option
	 *
	 * @return true if it is a no-action (NOAC) option
	 */
	public Boolean getIsNoAction() {
		return isNoAction;
	}

	@Override
	public Integer getKey() {
		return id;
	}
}
