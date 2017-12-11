package com.bt.nextgen.service.avaloq.transaction;

public enum TransactionFrequency {

	/**
	 * This enum maps the UI transaction Frequency directly to the Avaloq user_id
	 *
	 * @param UI selected frequency
	 * @return Avaloq user_id
	 */

	Fortnightly("weekly_2", "Fortnightly"),
	Monthly("rm", "Monthly"),
	Once("btfg$once", "Once"),
	Quarterly("rq", "Quarterly"),
	Weekly("weekly", "Weekly"),
	HalfYearly("rs", "Half-yearly"),
	Yearly("ry", "Yearly");

	private String frequency;
	private String description;

	TransactionFrequency(String frequency, String description) {
		this.frequency = frequency;
		this.description = description;
	}

	public String getFrequency() {
		return frequency;
	}

	public String getDescription() {
		return description;
	}

	public String toString() {
		return frequency;
	}
}
