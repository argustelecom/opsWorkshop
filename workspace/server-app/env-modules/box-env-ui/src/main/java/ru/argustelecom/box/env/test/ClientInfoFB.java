package ru.argustelecom.box.env.test;

import java.io.Serializable;

import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class ClientInfoFB implements Serializable {

	private static final long serialVersionUID = 4408649074611972031L;

	public String getState() {
		return "Действующий";
	}
	
	public String getClientType() {
		return "Физическое";
	}
	
	public String getSegment() {
		return "b2b";
	}

}
