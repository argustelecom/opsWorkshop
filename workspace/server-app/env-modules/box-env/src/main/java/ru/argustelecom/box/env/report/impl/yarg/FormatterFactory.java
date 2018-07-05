package ru.argustelecom.box.env.report.impl.yarg;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.haulmont.yarg.formatters.ReportFormatter;
import com.haulmont.yarg.formatters.factory.DefaultFormatterFactory;
import com.haulmont.yarg.formatters.factory.FormatterFactoryInput;
import com.haulmont.yarg.formatters.impl.AbstractFormatter;
import com.haulmont.yarg.formatters.impl.HtmlFormatter;
import com.haulmont.yarg.formatters.impl.inline.ContentInliner;
import com.haulmont.yarg.formatters.impl.inline.HtmlContentInliner;
import com.haulmont.yarg.formatters.impl.inline.ImageContentInliner;

import ru.argustelecom.box.env.report.api.font.ReportFontCache;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class FormatterFactory extends DefaultFormatterFactory {

	@Inject
	private ReportFontCache fontCache;

	public FormatterFactory() {
		FormatterCreator freemarkerCreator = factoryInput -> {
			HtmlFormatter htmlFormatter = new HtmlFormatterWithFontSupport(fontCache, factoryInput);
			htmlFormatter.setDefaultFormatProvider(defaultFormatProvider);
			return htmlFormatter;
		};

		formattersMap.remove("flt");
		formattersMap.remove("html");
		formattersMap.put("flt", freemarkerCreator);
		formattersMap.put("html", freemarkerCreator);
	}

	@Override
	public ReportFormatter createFormatter(FormatterFactoryInput factoryInput) {
		ReportFormatter formatter = super.createFormatter(factoryInput);

		checkState(formatter instanceof AbstractFormatter);
		((AbstractFormatter) formatter).setContentInliners(createContentInliners());

		return formatter;
	}

	public boolean isPdfConverterPresent() {
		return nonNull(documentConverter);
	}

	private List<ContentInliner> createContentInliners() {
		List<ContentInliner> contentInliners = new ArrayList<>(3);
		contentInliners.add(new JsonBitmapContentInliner());
		contentInliners.add(new ImageContentInliner());
		contentInliners.add(new HtmlContentInliner());
		return contentInliners;
	}

}
