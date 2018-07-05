package ru.argustelecom.box.inf.modelbase;

import static ru.argustelecom.system.inf.utils.CheckUtils.isValidId;
import static ru.argustelecom.system.inf.validation.ValidationIssue.createError;

import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.BusinessException;
import ru.argustelecom.system.inf.modelbase.SuperClass;
import ru.argustelecom.system.inf.validation.ValidationResult;

// FIXME Экспериментально, не использовать
public abstract class SuperClassBuilder<O extends SuperClass, B extends SuperClassBuilder<O, B>> {

	private ValidationResult<O> validationResult = new ValidationResult<>();

	protected Long id;

	@SuppressWarnings("unchecked")
	public B withId(Long id) {
		this.id = id;
		return (B) this;
	}

	public final O build() {
		validate();
		if (validationResult.hasErrors()) {
			throw new BusinessException(validationResult.explain());
		}
		return buildBusinessObject();
	}

	protected void validate() {
		validateState(isValidId(id), "Некорректный идентификатор бизнес-объекта: {0}", id);
	}

	protected void validateNotNull(Object value, String valueDesc) {
		validateState(value != null, "Не указан обязательный параметр: {0}", valueDesc);
	}

	protected void validateState(boolean stateCondition, String message) {
		if (!stateCondition) {
			validationResult.add(createError(null, message));
		}
	}

	protected void validateState(boolean stateCondition, String pattern, Object... args) {
		if (!stateCondition) {
			validationResult.add(createError(null, LocaleUtils.format(pattern, args)));
		}
	}

	protected abstract O buildBusinessObject();
}