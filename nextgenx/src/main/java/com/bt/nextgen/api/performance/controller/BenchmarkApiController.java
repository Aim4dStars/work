package com.bt.nextgen.api.performance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.performance.service.BenchmarkDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindAll;

@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class BenchmarkApiController
{

	@Autowired
	private BenchmarkDtoService benchmarkService;

	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.BENCHMARKS)
	@PreAuthorize("isAuthenticated()")
	public @ResponseBody
	ApiResponse getBenchmarks()
	{
		return new FindAll <>(ApiVersion.CURRENT_VERSION, benchmarkService).performOperation();
	}
}
