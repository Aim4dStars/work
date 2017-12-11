package com.bt.nextgen.core.api.model;


/*
 {
  "apiVersion": "2.0",
  "status": 1,
  "id": {
  			clientId: "xksdjf==",
  			portfolioId: "xkasdf3="
  		}
  ,
  "error": {
    "code": 404,
    "message": "Calendar not found",
    "errors": [{
      "domain": "Calendar",
      "reason": "ResourceNotFoundException",
      "message": "File Not Found
    }],
  "data": {
    "items": [
      {
        "kind": "photo",
        "title": "My First Photo"
      }
    ]
  }
  "paging":

  }
}
 */
public class KeyedApiResponse <K> extends ApiResponse
{
	private K id;

	public KeyedApiResponse(String apiVersion, K id, KeyedDto <K> data)
	{
		this(apiVersion, 1, id, data, null, null);
	}

	public KeyedApiResponse(String apiVersion, K id, KeyedDto <K> data, PagingResponse paging)
	{
		this(apiVersion, 1, id, data, null, paging);
	}

	public KeyedApiResponse(String apiVersion, Integer status, K id, KeyedDto <K> data, ApiError error, PagingResponse paging)
	{
		super(apiVersion, status, data, error, paging);
		this.id = id;
	}

	public K getId()
	{
		return id;
	}
}
