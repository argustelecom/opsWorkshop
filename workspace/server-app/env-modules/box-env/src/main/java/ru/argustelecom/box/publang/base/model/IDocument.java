package ru.argustelecom.box.publang.base.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = IDocument.TYPE_NAME, namespace = "")
public abstract class IDocument extends IEntity {

	public static final String TYPE_NAME = "iDocument";

	@XmlElement
	private String documentNumber;

	@XmlElement
	private Date documentDate;

	@XmlElement
	private Date creationDate;

	public IDocument(Long id, String objectName, String documentNumber, Date documentDate, Date creationDate) {
		super(id, objectName);
		this.documentNumber = documentNumber;
		this.documentDate = documentDate;
		this.creationDate = creationDate;
	}

	private static final long serialVersionUID = 596076055951751035L;

}