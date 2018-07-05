package ru.argustelecom.box.env.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;

import ru.argustelecom.box.env.report.api.font.ReportFont;
import ru.argustelecom.box.env.report.impl.font.ReportFontCacheImpl;

public class YargTestService {

	ReportFontCacheImpl fontCache;

	public YargTestService() {
		fontCache = new ReportFontCacheImpl();
		fontCache.refresh();
	}

	public void test() throws Exception {
		 ITextRenderer renderer = new ITextRenderer();
		 addFonts(renderer);
		
		 File temporaryFile = new File("d:\\work\\temp\\html.htm");
		 String url = temporaryFile.toURI().toURL().toString();
		
		 renderer.setDocument(url);
		 renderer.layout();
		 renderer.createPDF(new FileOutputStream("d:\\work\\temp\\html.pdf"));
	}

	private void addFonts(ITextRenderer renderer) throws DocumentException, IOException {
		final ITextFontResolver fontResolver = renderer.getFontResolver();
		for (ReportFont font : fontCache.getFonts()) {
			fontResolver.addFont(font.getPath(), font.getEncoding(), font.isEmbedded());
		}
	}

	public static void main(String[] args) throws Exception {
		YargTestService svc = new YargTestService();
		svc.test();
	}

}
