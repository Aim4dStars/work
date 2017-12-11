package com.bt.nextgen.service.hpx;

import java.awt.*;

public interface TagMappingService {
	String getTextDecorationName(boolean bold, boolean italic, boolean underline);

	String getFontName(String family, String size);

	String getParagraphStyleName(String tagName);

	String getTabName(int indentLevel);

	String getColourName(Color colour);
}
