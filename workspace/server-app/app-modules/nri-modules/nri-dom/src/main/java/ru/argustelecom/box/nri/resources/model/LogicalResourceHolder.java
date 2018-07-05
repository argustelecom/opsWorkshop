package ru.argustelecom.box.nri.resources.model;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.nri.logicalresources.model.LogicalResource;
import ru.argustelecom.box.nri.logicalresources.model.LogicalResourceType;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumber;
import ru.argustelecom.box.nri.resources.model.nls.LogicalResourceHolderMessagesBundle;
import ru.argustelecom.box.nri.resources.spec.model.ResourceSpecification;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Абстрактный класс, добавляющий поддержку связей с логическими ресурсами
 * Created by s.kolyada on 27.10.2017.
 */
@MappedSuperclass
@Access(AccessType.FIELD)
@Getter
@Setter
public abstract class LogicalResourceHolder extends BusinessObject {

	private static final long serialVersionUID = 7574314496682972129L;

	/**
	 * Подключенные телефонные номера
	 */
	@OneToMany(mappedBy = "resource")
	private List<LogicalResource> logicalResources = new ArrayList<>();

	/**
	 * Спецификация ресруса
	 */
	@ManyToOne
	@JoinColumn(name = "specification_id", nullable = false)
	protected ResourceSpecification specification;

	/**
	 * Конструктор по умолчанию
	 */
	public LogicalResourceHolder() {
	}

	/**
	 * Конструктор с идентификатором объекта
	 * @param id идентификатор объекта
	 */
	public LogicalResourceHolder(Long id) {
		super(id);
	}

	/**
	 * Проверить может ли ресурс сожержать логические ресурсы с типом
	 * @param logicalResourceType тип логического ресурса
	 * @return истина если может, иначе ложь
	 */
	public boolean canContainLogicalResource(LogicalResourceType logicalResourceType) {
		return specification.supportsLogicalResource(logicalResourceType);
	}

	/**
	 * Добавить теоефонный номер
	 * Проверяет, поддерживает ли ресурс согласно спецификации связи с телефонными номерами,
	 * если поддерживает добавляет номер
	 * @param phoneNumber телефонный номер
	 * @return истина, если удалось добавить, иначе ложь
	 */
	public boolean addPhoneNumber(PhoneNumber phoneNumber) {
		if (!canContainLogicalResource(LogicalResourceType.PHONE_NUMBER)) {
			throw new IllegalStateException(LocaleUtils.getMessages(LogicalResourceHolderMessagesBundle.class).addedLogicalResourceTypeUnsupportedByThisSpecification());
		}

		return logicalResources.add(phoneNumber);
	}

	/**
	 * Добавить теоефонные номера
	 * Проверяет, поддерживает ли ресурс согласно спецификации связи с телефонными номерами,
	 * если поддерживает добавляет номера
	 * @param phoneNumbers телефонные номера
	 * @return истина, если удалось добавить, иначе ложь
	 */
	public boolean addPhoneNumbers(Collection<PhoneNumber> phoneNumbers) {
		if (!canContainLogicalResource(LogicalResourceType.PHONE_NUMBER)) {
			throw new IllegalStateException(LocaleUtils.getMessages(LogicalResourceHolderMessagesBundle.class).addedLogicalResourceTypeUnsupportedByThisSpecification());
		}

		return phoneNumbers.addAll(phoneNumbers);
	}

	public List<LogicalResource> getLogicalResources() {
		return Collections.unmodifiableList(logicalResources);

	}
}
