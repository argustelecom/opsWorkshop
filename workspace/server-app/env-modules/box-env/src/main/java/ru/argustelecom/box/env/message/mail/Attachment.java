package ru.argustelecom.box.env.message.mail;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.InputStream;

@Getter
@AllArgsConstructor
public class Attachment {

	private String fileName;
	private InputStream data;
	private String mimeType;

}