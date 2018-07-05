package ru.argustelecom.box.env.telephony.tariff;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.disjoint;
import static ru.argustelecom.box.env.telephony.tariff.TariffEntryResultValidator.Validator.createInstance;
import static ru.argustelecom.system.inf.validation.ValidationResult.success;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.system.inf.validation.ValidationResult;

@ApplicationService
public class TariffEntryResultValidator {
	public <T extends TariffEntryBaseResult> ValidationResult<T> name(T entryArg, String arg, String errorMessage) {
		//@formatter:off
		Validator<T, String> instance = createInstance(
				(entry, name) -> !Objects.equals(entry.getName(), name),
				(entry, message) -> result -> result.warn(entry, message)
		);
		//@formatter:on
		return validate(instance, entryArg, arg, errorMessage);
	}

	public <T extends TariffEntryBaseResult> ValidationResult<T> prefixes(T entryArg, List<Integer> arg,
			String errorMessage) {
		//@formatter:off
		Validator<T, List<Integer>> instance = createInstance(
				(entry, prefixes) -> !disjoint(entry.getPrefixes(), prefixes),
				(entry, message) -> result -> result.error(entry, message)
		);
		//@formatter:on
		return validate(instance, entryArg, arg, errorMessage);
	}

	public <T extends TariffEntryBaseResult> ValidationResult<T> zoneName(T entryArg, String arg, String errorMessage) {
		//@formatter:off
		Validator<T, String> instance = createInstance(
				(entry, zoneName) -> !Objects.equals(entry.getZoneName(), zoneName),
				(entry, message) -> result -> result.warn(entry, message)
		);
		//@formatter:on
		return validate(instance, entryArg, arg, errorMessage);
	}

	public <T extends TariffEntryBaseResult> ValidationResult<T> chargePerUnit(T entryArg, Money arg,
			String errorMessage) {
		//@formatter:off
		Validator<T, Money> instance = createInstance(
				(entry, chargePerUnit) -> !Objects.equals(entry.getChargePerUnit(), chargePerUnit),
				(entry, message) -> result -> result.warn(entry, message)
		);
		//@formatter:on
		return validate(instance, entryArg, arg, errorMessage);
	}

	public ValidationResult<TariffEntryImportResult> invalid(TariffEntryImportResult entryArg, String errorMessage) {
		//@formatter:off
		Validator<TariffEntryImportResult, Boolean> instance = createInstance(
				(entry, isValid) -> !entry.isValid(),
				(entry, message) -> result -> result.error(entry, message)
		);
		//@formatter:on
		return validate(instance, entryArg, TRUE, errorMessage);
	}

	public static <T extends TariffEntryBaseResult> ValidationResult<T> validateAll(List<T> entries,
			Function<T, ValidationResult<T>> validateEntry) {
		//@formatter:off
		return entries.stream()
				.map(validateEntry)
				.collect(ValidationResult::success, ValidationResult::add, ValidationResult::add);
		//@formatter:on
	}

	private <T extends TariffEntryBaseResult, A> ValidationResult<T> validate(Validator<T, A> validator, T entry,
			A testAgainst, String message) {
		return validator.validate(checkNotNull(entry), checkNotNull(testAgainst), checkNotNull(message));
	}

	/**
	 * Является валидатором для {@link TariffEntryBaseResult} и его потомков
	 *
	 * @param <T>
	 *            потомок {@link TariffEntryBaseResult}
	 * @param <A>
	 *            аргумент, который необходимо проверить относительно поля экземпляра {@link TariffEntryBaseResult} или
	 *            его потомка
	 */
	public static class Validator<T extends TariffEntryBaseResult, A> {
		private final BiPredicate<T, A> predicate;
		private final BiFunction<T, String, Consumer<ValidationResult<T>>> executeIfTrue;

		private Validator(BiPredicate<T, A> predicate,
				BiFunction<T, String, Consumer<ValidationResult<T>>> executeIfTrue) {
			this.predicate = checkNotNull(predicate);
			this.executeIfTrue = checkNotNull(executeIfTrue);
		}

		public ValidationResult<T> validate(T entry, A testAgainst, String message) {
			ValidationResult<T> result = success();
			if (predicate.test(entry, testAgainst)) {
				executeIfTrue.apply(entry, message).accept(result);
			}
			return result;
		}

		public static <T extends TariffEntryBaseResult, A> Validator<T, A> createInstance(BiPredicate<T, A> predicate,
				BiFunction<T, String, Consumer<ValidationResult<T>>> executeIfTrue) {
			return new Validator<>(predicate, executeIfTrue);
		}
	}
}
