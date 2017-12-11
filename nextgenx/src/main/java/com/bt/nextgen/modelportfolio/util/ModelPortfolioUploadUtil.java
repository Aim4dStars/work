package com.bt.nextgen.modelportfolio.util;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioUploadDto;
import org.springframework.web.multipart.MultipartFile;

public interface ModelPortfolioUploadUtil
{
	public ModelPortfolioUploadDto parseFile(String modelId, MultipartFile file);
}
