package ru.argustelecom.box.env.billing.klto1c;

import static ru.argustelecom.box.env.billing.klto1c.KLto1CLexer.LexemeType.DOCUMENT_END;
import static ru.argustelecom.box.env.billing.klto1c.KLto1CLexer.LexemeType.KEY;
import static ru.argustelecom.box.env.billing.klto1c.KLto1CLexer.LexemeType.SECTION_END;
import static ru.argustelecom.box.env.billing.klto1c.KLto1CLexer.LexemeType.VALUE;
import static ru.argustelecom.system.inf.utils.ReflectionUtils.setFieldValue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.argustelecom.box.env.billing.klto1c.model.CheckingAccount;
import ru.argustelecom.box.env.billing.klto1c.model.Element;
import ru.argustelecom.box.env.billing.klto1c.model.ExportData;
import ru.argustelecom.box.env.billing.klto1c.model.KLto1CDataObject;
import ru.argustelecom.box.env.billing.klto1c.model.PaymentOrder;
import ru.argustelecom.system.inf.utils.ReflectionUtils;

/**
 * Парсер, обрабатывает результат {@linkplain KLto1CLexer лексера}, на основе которого формирует выгрузку в виде
 * заполненного {@link ru.argustelecom.box.env.billing.klto1c.model.ExportData}.
 */
public class KLto1CParser {

	private Map<Class, ClassProperty> inspectMap = new HashMap<>();

	private Iterator<Lexeme> lexemes;
	private ExportData exportData;

	private Lexeme currentLexeme;

	public void initLexemes(List<Lexeme> lexemes) {
		this.lexemes = lexemes.iterator();
	}

	public ExportData parse() throws InvocationTargetException, IllegalAccessException {
		initBindMap();
		while (lexemes.hasNext() && !(currentLexeme = lexemes.next()).getType().equals(DOCUMENT_END)) {
			switch (currentLexeme.getType()) {
			case DOCUMENT_START:
				exportData = createExportData();
				break;
			case KEY:
				bindFieldValue(exportData, currentLexeme);
				break;
			case SECTION_START:
				if (currentLexeme.getValue().equals("СекцияРасчСчет"))
					exportData.addCheckingAccount(createCheckingAccount());
				if (currentLexeme.getValue().equals("СекцияДокумент")
						&& (currentLexeme = lexemes.next()).getValue().equals("Платежное поручение"))
					exportData.addPaymentOrder(createPaymentOrder());
				break;
			}
		}
		return exportData;
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private ExportData createExportData() {
		return new ExportData();
	}

	private CheckingAccount createCheckingAccount() throws InvocationTargetException, IllegalAccessException {
		CheckingAccount checkingAccount = new CheckingAccount();
		while (lexemes.hasNext() && !(currentLexeme = lexemes.next()).getType().equals(SECTION_END)) {
			bindFieldValue(checkingAccount, currentLexeme);
		}
		return checkingAccount;
	}

	private PaymentOrder createPaymentOrder() throws InvocationTargetException, IllegalAccessException {
		PaymentOrder paymentOrder = new PaymentOrder();
		while (lexemes.hasNext() && !(currentLexeme = lexemes.next()).getType().equals(SECTION_END)) {
			bindFieldValue(paymentOrder, currentLexeme);
		}
		return paymentOrder;
	}

	private void bindFieldValue(KLto1CDataObject object, Lexeme field)
			throws InvocationTargetException, IllegalAccessException {

		Lexeme value = lexemes.next();

		if (!field.getType().equals(KEY)) {
			object.addWarning(String.format("Лексема(%s) не является ключём", field.getValue()));
			return;
		}
		if (!value.getType().equals(VALUE)) {
			object.addWarning(
					String.format("Лексема(%s) не является значением, для (%s)", value.getValue(), field.getValue()));
			return;
		}

		FieldProperty fieldProperty = inspectMap.get(object.getClass()).find(field.getValue());

		if (fieldProperty == null) {
			object.addWarning(String.format("Неизвестный элемент (%s)", field.getValue()));
			return;
		}
		if (fieldProperty.getElement().requiredInFormat() && (value.getValue() == null || value.getValue().isEmpty())) {
			object.addWarning(String.format("Элемент (%s) является обязательным в формате 1С", field.getValue()));
			return;
		}

		if (Set.class.isAssignableFrom(fieldProperty.getField().getType())) {
			((Set) fieldProperty.getField().get(object)).add(value.getValue());
			return;
		}

		try {
			if (!fieldProperty.getElement().dateFormat().isEmpty()) {
				setFieldValue(object.getClass(), fieldProperty.getField().getName(), object,
						new SimpleDateFormat(fieldProperty.getElement().dateFormat()).parse(value.getValue()));
				return;
			}
		} catch (ParseException pe) {
			object.addWarning(String.format("Невозможно преобразовать значение даты '%s' к формату '%s'",
					field.getValue(), fieldProperty.getElement().dateFormat()));
			return;
		}

		setFieldValue(object.getClass(), fieldProperty.getField().getName(), object, value.getValue());
	}

	private void initBindMap() {
		inspectMap.put(ExportData.class, new ClassProperty(ExportData.class));
		inspectMap.put(CheckingAccount.class, new ClassProperty(CheckingAccount.class));
		inspectMap.put(PaymentOrder.class, new ClassProperty(PaymentOrder.class));
	}

	// *****************************************************************************************************************
	// Inner classes
	// *****************************************************************************************************************

	private class ClassProperty {

		Map<String, FieldProperty> withRegexpMapping = new HashMap<>();
		Map<String, FieldProperty> simpleMapping = new HashMap<>();

		public ClassProperty(Class clazz) {
			Field[] fields = clazz.getDeclaredFields();

			for (Field field : fields) {
				try {
					Element element = field.getAnnotation(Element.class);

					if (element.simpleField())
						simpleMapping.put(element.name(),
								new FieldProperty(ReflectionUtils.getField(clazz, field.getName()), element));
					else
						withRegexpMapping.put(element.name(),
								new FieldProperty(ReflectionUtils.getField(clazz, field.getName()), element));
				} catch (NullPointerException ignored) {
				}
			}
		}

		public FieldProperty find(String keyword) {
			FieldProperty fieldProperty = simpleMapping.get(keyword);
			if (fieldProperty == null)
				fieldProperty = withRegexpMapping
						.get(withRegexpMapping.keySet().stream().filter(keyword::matches).findFirst().orElse(""));
			return fieldProperty;
		}

	}

	private class FieldProperty {

		private Field field;
		private Element element;

		FieldProperty(Field field, Element element) {
			this.field = field;
			this.element = element;
		}

		public Field getField() {
			return field;
		}

		public Element getElement() {
			return element;
		}

	}

}