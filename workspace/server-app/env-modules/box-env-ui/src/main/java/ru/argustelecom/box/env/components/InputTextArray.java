package ru.argustelecom.box.env.components;

import javax.faces.component.FacesComponent;

import ru.argustelecom.box.env.components.nls.InputTextArrayMessageBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;

@FacesComponent("inputTextArray")
public class InputTextArray extends AbstractCompositeInput {
	private InputTextArrayMessageBundle messages;

	public InputTextArrayMessageBundle getMessages() {
		if (messages == null) {
			messages = LocaleUtils.getMessages(InputTextArrayMessageBundle.class);
		}
		return messages;
	}
}
