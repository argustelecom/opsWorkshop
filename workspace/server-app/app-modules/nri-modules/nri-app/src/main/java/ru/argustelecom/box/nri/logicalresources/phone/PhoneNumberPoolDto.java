package ru.argustelecom.box.nri.logicalresources.phone;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.dto2.ConvertibleDto;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumberPool;
import ru.argustelecom.system.inf.modelbase.NamedObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ДТО пула телефонных номеров
 * Created by s.kolyada on 31.10.2017.
 */
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
public class PhoneNumberPoolDto extends ConvertibleDto implements Serializable, NamedObject {

	private static final long serialVersionUID = 1L;

	/**
	 * Идентификтор
	 */
	private Long id;

	/**
	 * Имя
	 */
	@Setter
	private String name;

	/**
	 * комментарий
	 */
	@Setter
	private String comment;
	/**
	 * Список телефонныхъ номеров входящих в пул
	 */
	@Setter
	private List<PhoneNumberDto> phoneNumbers = new ArrayList<>();

	/**
	 * Конструктор
	 *
	 * @param id           id
	 * @param name         имя
	 * @param phoneNumbers номера телефонов
	 * @param comment      комментарий
	 */
	@Builder
	public PhoneNumberPoolDto(Long id, String name, List<PhoneNumberDto> phoneNumbers, String comment) {
		this.id = id;
		this.name = name;
		this.phoneNumbers = Optional.ofNullable(phoneNumbers).orElse(new ArrayList<>());
		this.comment = comment;
	}

	@Override
	public Class<PhoneNumberPool> getEntityClass() {
		return PhoneNumberPool.class;
	}

	@Override
	public Class<PhoneNumberPoolDtoTranslator> getTranslatorClass() {
		return PhoneNumberPoolDtoTranslator.class;
	}

	@Override
	public String getObjectName() {
		return name;
	}
}

