package ru.argustelecom.ops.inf.validator.type;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.DoubleRangeValidator;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang3.StringUtils;

import ru.argustelecom.ops.inf.util.ParseUtils;

@FacesValidator("ru.argustelecom.ops.inf.validator.type.RangeAndScaleValidator")
public class RangeAndScaleValidator extends DoubleRangeValidator implements HasAttribute {

	private final static String MIN_ATTR = "minimum";
	private final static String MAX_ATTR = "maximum";
	private final static String PRECISION_ATTR = "precision";

	@Override
	public void validate(FacesContext context, UIComponent component, Object o) throws ValidatorException {
		// EditableSection использует один и тот же экземпляр валидатора при ui:repeat, поэтому явно обнуляем состояние
		resetState();

		Function<String, Optional<Double>> setter = (attr) -> getAttribute(attr, component).map(ParseUtils::doubleValue);

		setter.apply(MIN_ATTR).ifPresent(this::setMinimum);
		setter.apply(MAX_ATTR).ifPresent(this::setMaximum);

		Optional<Integer> p = getAttribute(PRECISION_ATTR, component).map(ParseUtils::intValue);
		Optional<Double> v = ofNullable(o).map(ParseUtils::doubleValue);

		BiConsumer<Integer, Double> condition = (precision, value) -> {
			Function<Double, Integer> fractionPart = (number) -> {
				DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(context.getViewRoot().getLocale());
				DecimalFormat formatter = new DecimalFormat();
				formatter.setMaximumIntegerDigits(0);
				formatter.setDecimalFormatSymbols(decimalFormatSymbols);
				String result = formatter.format(number);
				int indexOf = result.indexOf(Character.toString(decimalFormatSymbols.getDecimalSeparator()));
				return indexOf != -1 ? result.substring(indexOf + 1).length() : null;
			};

			boolean hasFractionNotEqPrecision = Math.abs(value - value.longValue()) > 0.0
					&& !Objects.equals(precision, fractionPart.apply(value));

			if (hasFractionNotEqPrecision) {

				Supplier<String> genFullErrorMsg = () -> format("Число должно быть формата  #%s",
						!Objects.equals(precision, 0) ? format(".%s", StringUtils.repeat('0', precision)) : "");

				throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", genFullErrorMsg.get()));
			}
		};

		v.ifPresent(value -> p.ifPresent(precision -> condition.accept(precision, value)));

		super.validate(context, component, o);
	}

	private void resetState() {
		setMinimum(Double.NEGATIVE_INFINITY);
		setMaximum(Double.POSITIVE_INFINITY);
		markInitialState();
	}
}