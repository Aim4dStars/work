package com.bt.nextgen.core.api.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static java.util.Collections.unmodifiableList;
import static org.springframework.util.StringUtils.hasText;

//"error": {
//    "code": 404,
//    "message": "Calendar not found",
//    "errors": [{
//      "domain": "Calendar",
//      "reason": "ResourceNotFoundException",
//      "message": "File Not Found
//    }],
public class ApiError
{
	private final Integer code;
	private final String reference;
	private final String message;
	private final List <DomainApiErrorDto> errors;

	public ApiError(Integer code, String message)
	{
		this(code, message, null);
	}

	public ApiError(Integer code, String message, String reference)
	{
		this(code, message, reference, Collections.<DomainApiErrorDto>emptyList());
	}

	public ApiError(Integer code, String message, String reference, List <DomainApiErrorDto> errors)
	{
		this.code = code;
		this.message = message;
		this.reference = reference;
		this.errors = new ArrayList<>(errors);
	}

	public Integer getCode()
	{
		return code;
	}

	public String getReference()
	{
		return reference;
	}

	public String getMessage()
	{
		return message;
	}

	public List <DomainApiErrorDto> getErrors()
	{
		return unmodifiableList(errors);
	}

	/**
	 * Builder class to consolidate potentially multiple {@code ApiError} instances into a single instance.
	 */
	public static class Builder {
		private Integer code = null;
		private final StringBuilder message = new StringBuilder();
		private final StringBuilder reference = new StringBuilder();
		private final List<DomainApiErrorDto> errors = new LinkedList<>();
		private int count = 0;

		/**
		 * Add the details of a new error to this builder.
		 * @param error the errors whose details are to be added.
		 */
		public void add(ApiError error) {
			count++;
			if (code == null) {
				code = error.getCode();
			}
			concatenate(error.getMessage(), message);
			concatenate(error.getReference(), reference);
			errors.addAll(error.getErrors());
		}

		private static void concatenate(String text, StringBuilder builder) {
			if (hasText(text)) {
				if (builder.length() > 0) {
					builder.append(" ; ");
				}
				builder.append(text);
			}
		}

		/**
		 * Build a new error with the consolidated details of all added errors.
		 * @return an error with amalgamated details from all added errors.
		 * @see #add(ApiError)
		 */
		public ApiError build() {
			return count > 0 ? new ApiError(code, message.toString(), reference.toString(), errors) : null;
		}
	}
}
