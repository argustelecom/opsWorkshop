package ru.argustelecom.box.env.report.impl.font.descriptor.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Strings;

import lombok.Getter;
import lombok.ToString;

@ToString(of = { "items" })
@XmlRootElement(name = "fonts")
@XmlAccessorType(XmlAccessType.FIELD)
public class Fonts implements Iterable<Font> {

	private static final String FILENAME_REGEX = "^([/|\\w:\\\\]?.+[\\\\|/])*(.+[\\.ttf|\\.otf])$";
	private static final Pattern FILENAME_PATTERN = Pattern.compile(FILENAME_REGEX, Pattern.CASE_INSENSITIVE);

	@Getter
	@XmlElement(name = "font", required = true)
	private List<Font> items = new ArrayList<>();

	@Override
	public Iterator<Font> iterator() {
		return items.iterator();
	}

	public Font findByPath(String fontPath) {
		String fontName = getFontName(fontPath);
		if (!Strings.isNullOrEmpty(fontName)) {
			return findByName(fontName);
		}
		return null;
	}

	public Font findByName(String fontName) {
		for (Font font : items) {
			if (!Strings.isNullOrEmpty(font.getName()) && Objects.equals(fontName, font.getName().toLowerCase())) {
				return font;
			}
		}
		return null;
	}

	public static String getFontName(String fontPath) {
		if (!Strings.isNullOrEmpty(fontPath)) {
			Matcher matcher = FILENAME_PATTERN.matcher(fontPath);
			if (matcher.matches()) {
				return matcher.group(2).toLowerCase();
			}
		}
		return null;
	}
}
