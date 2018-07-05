package ru.argustelecom.box.nri.logicalresources;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberDto;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberPoolDto;
import ru.argustelecom.box.nri.logicalresources.phone.lifecycle.PhoneNumberState;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumber;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberSpecificationInstance;
import ru.argustelecom.box.nri.resources.inst.ResourceInstanceDto;
import ru.argustelecom.system.inf.modelbase.Identifiable;
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
public class PhoneNumberDtoTmp extends ConvertibleDto implements Serializable, NamedObject {

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
	private PhoneNumberState state;

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
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return PhoneNumberDtoTranslatorTmp.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return PhoneNumber.class;
	}

	@Override
	public String getObjectName() {
		return name;
	}

	/**
	 * Конвертация в ДТО уровня приложения
	 * Требуется тк вся логика строится на работе с другим дто, который невозможно испольщовать в ленивых
	 * инициализаторах списков и прочих платформенных доработках, тк они работают только с ДТО из уровня представления
	 * TODO удалить, когда наконец-то платформа будет целиком поддерэивать ДТО из приложения
	 * @return базовый дто
	 */
	public PhoneNumberDto convertToBaseDto() {
		return PhoneNumberDto.builder()
				.id(this.id)
				.name(this.name)
				.pool(this.pool)
				.resource(this.resource)
				.specification(this.specification)
				.stateChangeDate(this.stateChangeDate)
				.state(this.state)
				.build();
	}
}
