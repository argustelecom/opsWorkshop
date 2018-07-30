package ru.argustelecom.ops.inf.converter;

import static java.util.Optional.ofNullable;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParsePosition;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.faces.convert.NumberConverter;

import org.apache.commons.lang3.StringUtils;
import ru.argustelecom.ops.inf.util.ParseUtils;

@FacesConverter("ru.argustelecom.ops.inf.converter.DynamicDoubleNumberConverter")
public class DynamicDoubleNumberConverter extends NumberConverter {

	private static final String PRECISION_ATTR = "precision";

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String o) {



		Consumer<String> parser = (value) -> {
			//Некорректно работает с точкой, поэтому заменим все точки на запятые
			value = value.replace(".", ",");
			DecimalFormat formatter = new DecimalFormat();
			formatter.setDecimalFormatSymbols(new DecimalFormatSymbols(context.getViewRoot().getLocale()));
			ParsePosition parsePosition = new ParsePosition(0);
			formatter.parse(value, parsePosition);
			// DecimalFormat распарсит значение "123.123" при ru_RU кодировке как 123, поэтому необходимо проверить
			// позицию, на которой разбор остановился и сравнить его с размером исходной строки
			if (parsePosition.getIndex() != value.length()) {
				throw new ConverterException(
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Невалидное значение", "Невалидное значение"));
			}
		};

		if(o != null) {
			o = o.replace(".", ",");
		}

		ofNullable(o).ifPresent(parser);

		createAndSetPattern(component);

		Object resultObj = super.getAsObject(context, component, o);

		if (resultObj instanceof Number) {
			return ((Number) resultObj).doubleValue();
		}

		return resultObj;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		createAndSetPattern(component);
		return super.getAsString(context, component, value);
	}

	private void createAndSetPattern(UIComponent component) {
		Function<Integer, String> patternCreator = precision -> String.format("0%s%s",
				!Objects.equals(precision, 0) ? "." : "", StringUtils.repeat('0', precision));
		ofNullable(component.getAttributes().get(PRECISION_ATTR)).map(ParseUtils::intValue)
				.filter(precision -> precision >= 0)
				.ifPresent(precision -> setPattern(patternCreator.apply(precision)));
	}
}
