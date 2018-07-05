package ru.argustelecom.box.env.report.impl.font;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.logging.Logger;

import com.google.common.base.Strings;

import lombok.Synchronized;
import ru.argustelecom.box.env.report.api.font.ReportFont;
import ru.argustelecom.box.env.report.api.font.ReportFontCache;
import ru.argustelecom.box.env.report.impl.font.descriptor.FontDescriptorLoader;
import ru.argustelecom.box.env.report.impl.font.descriptor.model.Font;
import ru.argustelecom.box.env.report.impl.font.descriptor.model.Fonts;
import ru.argustelecom.system.inf.configuration.ServerRuntimeProperties;

@ApplicationScoped
public class ReportFontCacheImpl implements ReportFontCache {

	private static final long serialVersionUID = -5741624192281542637L;
	private static final Logger log = Logger.getLogger(ReportFontCacheImpl.class);
	private static final String PROP_REPORT_FONT_DIRECTORY = "box.reporting.fonts";

	private List<ReportFont> fonts = new ArrayList<>();

	@Override
	public List<ReportFont> getFonts() {
		if (fonts == null) {
			refresh();
		}
		return fonts;
	}

	@Override
	@Synchronized
	public void refresh() {
		log.info("Initializing report fonts...");
		List<ReportFont> reportFonts = new ArrayList<>();
		initResourceFonts(reportFonts);
		initFilesystemFonts(reportFonts);
		fonts = Collections.unmodifiableList(reportFonts);
	}

	@PostConstruct
	protected void postConstruct() {
		refresh();
	}

	private void initResourceFonts(List<ReportFont> reportFonts) {
		log.info("Loading default fonts");

		Fonts descriptors = FontDescriptorLoader.load();
		log.infov("Default fonts descriptor: {0}", descriptors);

		descriptors.forEach(descriptor -> {
			String fontPath = FontDescriptorLoader.getResourceFontPath(descriptor);
			if (fontPath != null) {
				addReportFont(reportFonts, fontPath, descriptor);
			}
		});

	}

	private void initFilesystemFonts(List<ReportFont> reportFonts) {
		log.info("Loading user fonts");

		String fontDirectory = ServerRuntimeProperties.instance().getProperties().getProperty(PROP_REPORT_FONT_DIRECTORY);
		if (!Strings.isNullOrEmpty(fontDirectory)) {
			File fontDir = new File(fontDirectory);
			if (fontDir.exists() && fontDir.isDirectory()) {
				Fonts descriptors = FontDescriptorLoader.load(fontDir);
				log.infov("User fonts descriptor: {0}", descriptors);

				List<File> files = loadFontFiles(fontDir);
				files.forEach(fontFile -> addReportFont(reportFonts, fontFile.getAbsolutePath(), descriptors));
				return;
			}
		}

		log.info("User fonts are not defined");
	}

	private List<File> loadFontFiles(File fontDir) {
		File[] result = fontDir.listFiles((dir, name) -> {
			String lower = name.toLowerCase();
			return lower.endsWith(".otf") || lower.endsWith(".ttf");
		});
		return Arrays.asList(result);
	}

	private void addReportFont(List<ReportFont> reportFonts, String fontPath, Fonts descriptors) {
		Font descriptor = null;
		if (descriptors != null) {
			descriptor = descriptors.findByPath(fontPath);
		}
		addReportFont(reportFonts, fontPath, descriptor);
	}

	private void addReportFont(List<ReportFont> reportFonts, String fontPath, Font descriptor) {
		String encoding = descriptor != null ? descriptor.getEncoding() : "Identity-H";
		boolean embedded = descriptor != null ? descriptor.isEmbedded() : true;

		ReportFontImpl fontImpl = new ReportFontImpl(fontPath, encoding, embedded);
		reportFonts.add(fontImpl);

		log.info(fontImpl);
	}
}
