package ru.argustelecom.box.env.report.impl.yarg;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.haulmont.yarg.formatters.factory.FormatterFactoryInput;
import com.haulmont.yarg.formatters.impl.HtmlFormatter;
import com.lowagie.text.DocumentException;

import ru.argustelecom.box.env.report.api.font.ReportFont;
import ru.argustelecom.box.env.report.api.font.ReportFontCache;

public class HtmlFormatterWithFontSupport extends HtmlFormatter {

	private ReportFontCache fontCache;

	public HtmlFormatterWithFontSupport(ReportFontCache fontCache, FormatterFactoryInput formatterFactoryInput) {
		super(formatterFactoryInput);
		this.fontCache = fontCache;
	}

	@Override
	protected void renderPdfDocument(String htmlContent, OutputStream outputStream) {
		ITextRenderer renderer = new ITextRenderer();
		File temporaryFile = null;
		try {
			addFonts(renderer);

			temporaryFile = createTemporaryFile(htmlContent);
			String url = temporaryFile.toURI().toURL().toString();

			renderer.setDocument(url);
			renderer.layout();
			renderer.createPDF(outputStream);
		} catch (Exception e) {
			throw wrapWithReportingException("", e);
		} finally {
			FileUtils.deleteQuietly(temporaryFile);
		}
	}

	@Override
	protected Template getTemplate() {
		try {
			String templateContent = IOUtils.toString(reportTemplate.getDocumentContent());
			StringTemplateLoader stringLoader = new StringTemplateLoader();
			stringLoader.putTemplate(reportTemplate.getDocumentName(), templateContent);

			Configuration fmConfiguration = new Configuration();
			fmConfiguration.setTemplateLoader(stringLoader);
			fmConfiguration.setDefaultEncoding("UTF-8");
			fmConfiguration.setNumberFormat("0.00");

			Template htmlTemplate = fmConfiguration.getTemplate(reportTemplate.getDocumentName());
			htmlTemplate.setObjectWrapper(objectWrapper);
			return htmlTemplate;
		} catch (Exception e) {
			throw wrapWithReportingException("An error occurred while creating freemarker template", e);
		}
	}

	private File createTemporaryFile(String htmlContent) throws IOException {
		File temporaryFile = File.createTempFile("htmlReport", ".htm");
		try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(temporaryFile))) {
			dataOutputStream.write(htmlContent.getBytes(Charset.forName("UTF-8")));
		}
		return temporaryFile;
	}

	private void addFonts(ITextRenderer renderer) throws DocumentException, IOException {
		final ITextFontResolver fontResolver = renderer.getFontResolver();
		for (ReportFont font : fontCache.getFonts()) {
			fontResolver.addFont(font.getPath(), font.getEncoding(), font.isEmbedded());
		}
	}
}
