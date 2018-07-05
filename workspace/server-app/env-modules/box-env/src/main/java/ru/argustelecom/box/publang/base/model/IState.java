package ru.argustelecom.box.publang.base.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode(of = "keyword")
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = IState.TYPE_NAME, namespace = "")
public class IState implements Serializable {

	public static final String TYPE_NAME = "iState";

	private String keyword;
	private String name;

	private static final long serialVersionUID = -3215187134432671132L;

}