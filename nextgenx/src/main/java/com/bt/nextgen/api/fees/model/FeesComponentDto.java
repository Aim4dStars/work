package com.bt.nextgen.api.fees.model;


//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@class")
/*@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "class")
@JsonSubTypes(
{

	@JsonSubTypes.Type(value = DollarFeeDto.class, name = "dollar"),

	@JsonSubTypes.Type(value = SlidingScaleFeeDto.class, name = "sliding"),

	@JsonSubTypes.Type(value = PercentageFeeDto.class, name = "percentage")

})*/
public interface FeesComponentDto
{
	String getLabel();

	void setLabel(String label);
}
