package ru.argustelecom.box.env.saldo.imp.model;

import ru.argustelecom.box.env.saldo.nls.SaldoImportMessagesBundle;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.inf.nls.LocaleUtils;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static ru.argustelecom.box.env.saldo.imp.model.DefaultItemError.TRYING_TO_RE_IMPORT;
import static ru.argustelecom.box.env.saldo.imp.model.ResultType.NOT_SUITABLE;
import static ru.argustelecom.box.env.saldo.imp.model.ResultType.REQUIRED_CORRECTION;
import static ru.argustelecom.box.env.saldo.imp.model.ResultType.SUITABLE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class Register {

	public abstract String getCharset();

	public abstract void setCharset(String charset);

	public abstract String getNumber();

	public abstract BigDecimal getSum();

	public abstract Date getCreationDate();

	public abstract Date getStartDate();

	public abstract Date getEndDate();

	private List<Container> containers = new ArrayList<>();

	public void fillContainers(List<RegisterItem> items) {
		Map<Set<ItemError>, List<RegisterItem>> itemsGroupByErrors = items.stream()
				.collect(Collectors.groupingBy(RegisterItem::getErrors));

		itemsGroupByErrors.keySet().forEach(errors -> {
			if (errors.isEmpty()) {
				putToSuitableContainer(itemsGroupByErrors.get(errors), errors);
			} else if (errors.contains(TRYING_TO_RE_IMPORT)) {
				putToNotSuitableContainer(itemsGroupByErrors.get(errors), errors);
			} else {
				putToCorrectionContainer(itemsGroupByErrors.get(errors), errors);
			}
		});
	}

	public void moveToSuitableContainer(RegisterItem item) {
		Container currentContainer = findCurrentContainer(item);
		if (currentContainer != null)
			currentContainer.removeItem(item);
		findOrPut(SUITABLE, Collections.emptySet()).addItem(item);
		sumOfSuitableItems = null;
	}

	public String checkParams() {
		SaldoImportMessagesBundle messages = LocaleUtils.getMessages(SaldoImportMessagesBundle.class);
		StringBuilder aggregateErrorsMsg = new StringBuilder(messages.headerHasInvalidData());
		aggregateErrorsMsg.append(checkNumber());

		return messages.headerHasInvalidData().length() == aggregateErrorsMsg.length() ? EMPTY
				: aggregateErrorsMsg.toString();
	}

	public List<Container> getContainers() {
		return Collections.unmodifiableList(containers);
	}

	public List<RegisterItem> getSuitableItems() {
		Container suitableContainer = find(SUITABLE, Collections.emptySet());
		return suitableContainer != null ? suitableContainer.getItems() : Collections.emptyList();
	}

	private Money sumOfSuitableItems;

	public Money getSumOfSuitableItems() {
		if (sumOfSuitableItems == null)
			sumOfSuitableItems = getSuitableItems().stream().map(RegisterItem::getSum).reduce(Money.ZERO,
					Money::add);
		return sumOfSuitableItems;
	}

	private Container find(ResultType type, Set<ItemError> errors) {
		return containers.stream().filter(c -> c.getType().equals(type) && c.getErrors().equals(errors)).findFirst()
				.orElse(null);
	}

	private Container findOrPut(ResultType type, Set<ItemError> errors) {
		Container container = find(type, errors);
		if (container == null) {
			container = new Container(type, errors);
			containers.add(container);
		}
		return container;
	}

	private Container findCurrentContainer(RegisterItem item) {
		for (Container container : containers) {
			for (RegisterItem i : container.getItems()) {
				if (i.equals(item))
					return container;

			}
		}
		return null;
	}

	private void putToSuitableContainer(List<RegisterItem> items, Set<ItemError> errors) {
		containers.add(new Container(SUITABLE, items, errors));
	}

	private void putToNotSuitableContainer(List<RegisterItem> items, Set<ItemError> errors) {
		containers.add(new Container(NOT_SUITABLE, items, errors));
	}

	private void putToCorrectionContainer(List<RegisterItem> items, Set<ItemError> errors) {
		containers.add(new Container(REQUIRED_CORRECTION, items, errors));
	}

	private String checkNumber() {
		if (getNumber() == null || getNumber().isEmpty()) {
			SaldoImportMessagesBundle messages = LocaleUtils.getMessages(SaldoImportMessagesBundle.class);
			return messages.registerNumber() + ". ";
		}
		return EMPTY;
	}

}