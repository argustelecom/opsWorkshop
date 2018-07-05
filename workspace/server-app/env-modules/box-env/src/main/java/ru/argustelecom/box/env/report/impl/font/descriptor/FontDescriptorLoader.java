package ru.argustelecom.box.env.report.impl.font.descriptor;

import static org.apache.commons.lang3.StringUtils.join;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;

import com.google.common.base.Strings;

import ru.argustelecom.box.env.report.impl.font.descriptor.model.Font;
import ru.argustelecom.box.env.report.impl.font.descriptor.model.Fonts;
import ru.argustelecom.system.inf.exception.SystemException;

public class FontDescriptorLoader {

	private static final String DEFAULT_FONTS_LOCATION = "ru/argustelecom/box/env/report/fonts/";
	private static final String FONTS_DESCRIPTOR_NAME = "fonts.xml";

	private static final Object $LOCK = new Object();
	private static volatile String systemFontsDir;

	private static final JAXBContext context;
	static {
		try {
			context = JAXBContext.newInstance(Fonts.class, Font.class);
		} catch (JAXBException e) {
			throw new SystemException(e);
		}
	}

	private FontDescriptorLoader() {
	}

	public static String getResourceFontPath(Font descriptor) {
		InputStream resourceFontContent = getResourceAsStream(DEFAULT_FONTS_LOCATION + descriptor.getName());
		if (resourceFontContent != null) {
			File resourceFontFile = new File(getSystemFontsDirectory(), descriptor.getName());
			if (!resourceFontFile.exists()) {
				try (InputStream bufferedContent = IOUtils.toBufferedInputStream(resourceFontContent)) {
					IOUtils.copy(bufferedContent, new FileOutputStream(resourceFontFile));
				} catch (IOException e) {
					throw new SystemException(e);
				}
			}
			return resourceFontFile.getAbsolutePath();
		}
		return null;
	}

	public static Fonts load() {
		URL defaultFonts = getResource(DEFAULT_FONTS_LOCATION + FONTS_DESCRIPTOR_NAME);
		if (defaultFonts != null) {
			try {
				return (Fonts) context.createUnmarshaller().unmarshal(defaultFonts);
			} catch (JAXBException e) {
				throw new SystemException(e);
			}
		}
		return null;
	}

	public static Fonts load(File fontsFile) {
		if (fontsFile.exists()) {
			File descriptorFile = fontsFile.isDirectory() ? new File(fontsFile, FONTS_DESCRIPTOR_NAME) : fontsFile;
			if (descriptorFile.exists() && descriptorFile.isFile()) {
				try {
					return (Fonts) context.createUnmarshaller().unmarshal(descriptorFile);
				} catch (JAXBException e) {
					throw new SystemException(e);
				}
			}
		}
		return null;
	}

	private static URL getResource(String name) {
		return FontDescriptorLoader.class.getClassLoader().getResource(name);
	}

	private static InputStream getResourceAsStream(String name) {
		return FontDescriptorLoader.class.getClassLoader().getResourceAsStream(name);
	}

	private static File getSystemFontsDirectory() {
		if (systemFontsDir == null) {
			synchronized ($LOCK) {
				if (systemFontsDir == null) {
					String fileSeparator = File.separator;
					String tempDirectory = System.getProperty("jboss.server.data.dir");
					if (Strings.isNullOrEmpty(tempDirectory)) {
						tempDirectory = System.getProperty("java.io.tmpdir");
					}
					File fontsDirectory = new File(join(tempDirectory, fileSeparator, "report", fileSeparator, "fonts"));
					if (!fontsDirectory.exists()) {
						fontsDirectory.mkdirs();
					}
					systemFontsDir = fontsDirectory.getAbsolutePath();
				}
			}
		}
		return new File(systemFontsDir);
	}

	public static void main(String[] args) {
		Fonts fonts = load(new File("c:\\"));
		System.out.println(fonts);
	}
}
