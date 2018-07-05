package ru.argustelecom.box.env.report.api.data;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.argustelecom.system.inf.exception.SystemException;

@EqualsAndHashCode(of = { "format", "content" })
public class ReportDataImage {

	private static final String RFC2397_URI_FORMAT = "data:%s;base64,%s";
	private static final String RFC2397_URI_REGEXP = "data:([a-z/]+);base64,(.*)";
	private static final Pattern RFC2397_URI_PATTERN = Pattern.compile(RFC2397_URI_REGEXP);

	@Getter
	private int width;

	@Getter
	private int height;

	@Getter
	private ImageFormat format;

	@Getter
	private byte[] content;

	protected ReportDataImage(ImageFormat format, int width, int height, byte[] content) {
		checkArgument(format != null);
		checkArgument(content != null);
		this.format = format;
		this.width = width;
		this.height = height;
		this.content = content;
	}

	protected ReportDataImage(ImageFormat format, int width, int height, InputStream contentStream) {
		this(format, width, height, toByteArray(contentStream));
	}

	private static byte[] toByteArray(InputStream contentStream) {
		checkArgument(contentStream != null);
		try {
			return IOUtils.toByteArray(contentStream);
		} catch (IOException e) {
			throw new SystemException(e);
		}
	}

	public static boolean isRFC2397Uri(String rfc2397UriCandidate) {
		return RFC2397_URI_PATTERN.matcher(rfc2397UriCandidate).matches();
	}

	public static ImageFormat getFormatFromRFC2397Uri(String rfc2397Uri) {
		Matcher m = RFC2397_URI_PATTERN.matcher(rfc2397Uri);
		checkState(m.matches());
		return ImageFormat.fromMime(m.group(1));
	}

	public static byte[] getContentFromRFC2397Uri(String rfc2397Uri) {
		Matcher m = RFC2397_URI_PATTERN.matcher(rfc2397Uri);
		checkState(m.matches());
		return Base64.getDecoder().decode(m.group(2));
	}

	public static ReportDataImage of(String rfc2397Uri, int width, int height) {
		Matcher m = RFC2397_URI_PATTERN.matcher(rfc2397Uri);
		checkState(m.matches());
		byte[] content = Base64.getDecoder().decode(m.group(2));
		return new ReportDataImage(ImageFormat.fromMime(m.group(1)), width, height, content);
	}

	public static ReportDataImage of(ImageFormat format, int width, int height, byte[] content) {
		return new ReportDataImage(format, width, height, content);
	}

	public static ReportDataImage of(ImageFormat format, int width, int height, InputStream contentStream) {
		return new ReportDataImage(format, width, height, contentStream);
	}

	public InputStream getContentStream() {
		return new ByteArrayInputStream(content);
	}

	public String toBase64String() {
		return Base64.getEncoder().encodeToString(content);
	}

	public String toRFC2397Uri() {
		String base64String = toBase64String();
		return String.format(RFC2397_URI_FORMAT, format.mime(), base64String);
	}

	@Override
	public String toString() {
		return toRFC2397Uri();
	}

	public enum ImageFormat {

		PNG("image/png"), JPEG("image/jpeg"), GIF("image/gif");

		private String mime;

		private ImageFormat(String mime) {
			this.mime = mime;
		}

		public String mime() {
			return mime;
		}

		public static ImageFormat fromMime(String mime) {
			for (ImageFormat format : values()) {
				if (Objects.equals(format.mime(), mime)) {
					return format;
				}
			}
			return null;
		}
	}
}
