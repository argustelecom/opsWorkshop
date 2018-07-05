package ru.argustelecom.box.nri.logicalresources;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.filter.FilterMapEntry;
import ru.argustelecom.box.env.filter.FilterViewState;
import ru.argustelecom.box.nri.logicalresources.phone.lifecycle.PhoneNumberState;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberPool;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecification;
import ru.argustelecom.system.inf.page.PresentationState;

import static ru.argustelecom.box.nri.logicalresources.PhoneNumbersViewState.PhoneNumbersFilter.DIGITS;
import static ru.argustelecom.box.nri.logicalresources.PhoneNumbersViewState.PhoneNumbersFilter.POOL;
import static ru.argustelecom.box.nri.logicalresources.PhoneNumbersViewState.PhoneNumbersFilter.SPEC;
import static ru.argustelecom.box.nri.logicalresources.PhoneNumbersViewState.PhoneNumbersFilter.STATE;

/**
 * Состояние страницы поиска телефонных номеров
 * @author d.khekk
 * @since 23.11.2017
 */
@PresentationState
@Getter
@Setter
public class PhoneNumbersViewState extends FilterViewState {

	private static final long serialVersionUID = 1L;

	@FilterMapEntry(DIGITS)
	private String digits;

	@FilterMapEntry(STATE)
	private PhoneNumberState state;

	@FilterMapEntry(POOL)
	private PhoneNumberPool pool;

	@FilterMapEntry(SPEC)
	private PhoneNumberSpecification specification;

	class PhoneNumbersFilter {
		static final String DIGITS = "DIGITS";
		static final String POOL = "POOL";
		static final String STATE = "STATE";
		static final String SPEC = "SPEC";

		/**
		 * Приватный конструктор
		 */
		private PhoneNumbersFilter() {
		}
	}
}
