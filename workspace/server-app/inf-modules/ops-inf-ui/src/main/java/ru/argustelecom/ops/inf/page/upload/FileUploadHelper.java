package ru.argustelecom.ops.inf.page.upload;

import java.io.IOException;
import java.io.InputStream;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@RequestScoped
public class FileUploadHelper {

	private static final int FILE_NAME_MAX_SIZE = 128;
	private static final int FILE_PATH_MAX_SIZE = 256;

	@Inject
	private HttpServletRequest httpRequest;

	public UploadedFileInfo getUploadedFileInfo(FileUploadEvent event) throws IOException, ServletException {
		String uploadingFileName = event.getFile().getFileName();
		String fileName = StringUtils.abbreviate(uploadingFileName, FILE_NAME_MAX_SIZE);
		String fileSource = StringUtils.abbreviate(uploadingFileName, FILE_PATH_MAX_SIZE);
		String mimeType = getMimeType(fileSource);

		byte[] body;
		UploadedFile file = event.getFile();
		try (InputStream fileStream = file.getInputstream()) {
			body = IOUtils.toByteArray(fileStream, file.getSize());
		}

		return new UploadedFileInfo(fileName, fileSource, mimeType, body);
	}

	public String getMimeType(String fileName) {
		return httpRequest.getServletContext().getMimeType(fileName);
	}

	@Getter
	@AllArgsConstructor(access = AccessLevel.PACKAGE)
	public static class UploadedFileInfo {
		private String fileName;
		private String fileSource;
		private String mimeType;
		private byte[] bytes;

	}
}
