package com.bt.nextgen.core.api.model;

import com.bt.nextgen.config.JsonViews;
import com.fasterxml.jackson.annotation.JsonView;

//	  {
//      "errId": "err.1234",
//      "domain": "Calendar",
//      "reason": "ResourceNotFoundException",
//      "message": "File Not Found",
//      "type": "Warning"
//    }],
public class DomainApiErrorDto extends BaseDto
{
	public enum ErrorType
	{
		ERROR("error"), WARNING("warning");

        @JsonView(JsonViews.Write.class)
		private String code;

		private ErrorType(String type)
		{
			this.code = type;
		}

		public static ErrorType forCode(String code)
		{
			for (ErrorType type : ErrorType.values())
			{
				if (type.code.equals(code))
				{
					return type;
				}
			}

			return null;
		}

		@Override
		public String toString()
		{
			return code;
		}
	};

    @JsonView(JsonViews.Write.class)
	private String errorId;

    @JsonView(JsonViews.Write.class)
    private String domain;

    @JsonView(JsonViews.Write.class)
    private String message;

    @JsonView(JsonViews.Write.class)
    private ErrorType errorType;

    private String reason;

	public DomainApiErrorDto()
	{
		super();
	}

	public DomainApiErrorDto(String domain, String reason, String message)
	{
		this(null, domain, reason, message, ErrorType.ERROR);
	}

	public DomainApiErrorDto(String domain, String reason, String message, ErrorType errorType)
	{
		this(null, domain, reason, message, errorType);
	}

	public DomainApiErrorDto(String errorId, String domain, String reason, String message, ErrorType errorType)
	{
		super();
		this.errorId = errorId;
		this.domain = domain;
		this.reason = reason;
		this.message = message;
		this.errorType = errorType;
	}

	public String getErrorId()
	{
		return errorId;
	}

	public String getDomain()
	{
		return domain;
	}

	public String getReason()
	{
		return reason;
	}

	public String getMessage()
	{
		return message;
	}

	public String getErrorType()
	{
		return errorType == null ? null : errorType.toString();
	}

	public void setErrorId(String errorId)
	{
		this.errorId = errorId;
	}

	public void setDomain(String domain)
	{
		this.domain = domain;
	}

	public void setReason(String reason)
	{
		this.reason = reason;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public void setErrorType(String errorType)
	{
		this.errorType = ErrorType.forCode(errorType);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((domain == null) ? 0 : domain.hashCode());
		result = prime * result + ((errorId == null) ? 0 : errorId.hashCode());
		result = prime * result + ((errorType == null) ? 0 : errorType.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((reason == null) ? 0 : reason.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DomainApiErrorDto other = (DomainApiErrorDto)obj;
		if (domain == null)
		{
			if (other.domain != null)
				return false;
		}
		else if (!domain.equals(other.domain))
			return false;
		if (errorId == null)
		{
			if (other.errorId != null)
				return false;
		}
		else if (!errorId.equals(other.errorId))
			return false;
		if (errorType != other.errorType)
			return false;
		if (message == null)
		{
			if (other.message != null)
				return false;
		}
		else if (!message.equals(other.message))
			return false;
		if (reason == null)
		{
			if (other.reason != null)
				return false;
		}
		else if (!reason.equals(other.reason))
			return false;
		return true;
	}
}
