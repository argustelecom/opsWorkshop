package ru.argustelecom.box.env.report.impl.font;

import lombok.AllArgsConstructor;
import lombok.ToString;
import ru.argustelecom.box.env.report.api.font.ReportFont;
import ru.argustelecom.box.env.report.impl.font.descriptor.model.Fonts;

@AllArgsConstructor
@ToString(of = { "path", "encoding", "embedded" })
public class ReportFontImpl implements ReportFont {

	private static final long serialVersionUID = -3745297075497496821L;

	private String path;
	private String encoding;
	private boolean embedded;

	@Override
	public String getName() {
		return Fonts.getFontName(path);
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public String getEncoding() {
		return encoding;
	}

	@Override
	public boolean isEmbedded() {
		return embedded;
	}

}
