package ru.argustelecom.box.nri.logicalresources.phone;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.dto2.ConvertibleDto;
import ru.argustelecom.box.nri.logicalresources.phone.lifecycle.PhoneNumberState;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumber;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecificationInstance;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDto;
import ru.argustelecom.system.inf.modelbase.NamedObject;

import java.io.Serializable;
import java.util.Date;

/**
 * ДТО логического ресурса телефонный номер
 * Created by b.bazarov on 31.10.2017.
 */
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@Builder
public class PhoneNumberDto extends ConvertibleDto implements Serializable, NamedObject {

	private static final long serialVersionUID = 1L;

	/**
	 * Идентификтор
	 */
	private Long id;

	/**
	 * Имя номера
	 */
	@Setter
	private String name;

	/**
	 * Статус номера
	 */
	@Setter
	private PhoneNumberState state = PhoneNumberState.defaultStatus();

	@Setter
	private PhoneNumberPoolDto pool;

	/**
	 * Ресурс к которому привязан номер
	 */
	@Setter
	private ResourceInstanceDto resource;
	/**
	 * Время послденего изменения статуса
	 */
	@Setter
	private Date stateChangeDate;

	@Setter
	private PhoneNumberSpecificationInstance specification;

	@Override
	public Class<PhoneNumberDtoTranslator> getTranslatorClass() {
		return PhoneNumberDtoTranslator.class;
	}

	@Override
	public Class<PhoneNumber> getEntityClass() {
		return PhoneNumber.class;
	}

	@Override
	public String getObjectName() {
		return name;
	}
}
