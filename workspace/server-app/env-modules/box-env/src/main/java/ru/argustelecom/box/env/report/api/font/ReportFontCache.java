package ru.argustelecom.box.env.report.api.font;

import java.io.Serializable;
import java.util.List;

public interface ReportFontCache extends Serializable {

	List<ReportFont> getFonts();

	void refresh();

}
