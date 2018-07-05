package ru.argustelecom.box.env.saldo.imp.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class RegisterContext {

	private Register register;
	private List<RegisterItem> items = new ArrayList<>();

	public RegisterContext(Register register) {
		this.register = register;
	}

}