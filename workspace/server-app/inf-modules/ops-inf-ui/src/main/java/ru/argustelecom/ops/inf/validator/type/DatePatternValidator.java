package ru.argustelecom.ops.inf.validator.type;

import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("ru.argustelecom.ops.inf.validator.type.DatePatternValidator")
public class DatePatternValidator implements Validator {

	private final static Pattern PATTERN = Pattern
			.compile("(?:(((yyyy|yy)|MM|dd)[/.\\-]?)(?!.*\\1)){1,3}+( (?:((HH|mm|ss)[:]?)(?!.*\\1)){1,3}+)?");

	@Override
	public void validate(FacesContext facesContext, UIComponent uiComponent, Object o) throws ValidatorException {
		if (o != null && !PATTERN.matcher((String) o).matches()) {
			throw new ValidatorException(
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "Введен некорректный шаблон даты"));
		}
	}
}