package ru.argustelecom.box.env.lifecycle.api.history.model;

import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.EqualsAndHashCode;

/**
 * Инициатор изменения состояния какого-либо объекта жизненного цикла. В зависимости от типа инициатора может быть либо
 * пользователем, залогиненными в системе (вариант, при котором используется общий UI для управления жизненным циклом),
 * либо планировщиком (вариант, когда жизненный цикл объекта меняется вследствие выполнения задачи планировщика,
 * например, закрытие инвойса очередью)
 */
@Embeddable
@Access(AccessType.FIELD)
@EqualsAndHashCode(of = { "id", "type", "name" })
public class Initiator implements Serializable {

	private static final long serialVersionUID = 3851098449023024770L;

	@Column(updatable = false)
	private Long id;

	@Column(nullable = false, updatable = false)
	private InitiatorType type;

	@Column(nullable = false, updatable = false)
	private String name;

	protected Initiator() {
	}

	protected Initiator(InitiatorType type, String name) {
		this.type = checkRequiredArgument(type, "type");
		this.name = checkRequiredArgument(name, "name");
	}

	protected Initiator(Long id, InitiatorType type, String name) {
		this(type, name);
		this.id = id;
	}

	/**
	 * Создает экземпляр инициатора, используя указанный тип и имя инициатора. Предназначен для варианта создания
	 * инициатора, при котором он не может быть однозначно идентифицирован, например, если инициатором является
	 * планировщик, который на текущий момент времени не связан с пользователем и выполняет действия не авторизованно в
	 * системе
	 * 
	 * @param type
	 *            - тип инициатора, обязательный параметр
	 * @param name
	 *            - имя инициатора, обязательный параметр
	 * 
	 * @return экземпляр созданного инициатора
	 */
	public static Initiator of(InitiatorType type, String name) {
		return new Initiator(type, name);
	}

	/**
	 * Создает экземпляр инициатора, используя указанные параметры, в том числе и идентификатор. Предназначен для
	 * варианта создания инициатора, при котором он может быть однозначно идентифицирован, например, если инициатором
	 * является пользователь, который менял состояние бизнес-объекта через специализированный UI или путем вызова любого
	 * другого прикладного кода, выполняющегося в контексте авторизованного пользователя
	 * 
	 * @param id
	 *            - идентификатор инициатора
	 * @param type
	 *            - тип инициатора, обязательный параметр
	 * @param name
	 *            - имя инициатора, обязательный параметр
	 * 
	 * @return экземпляр созданного инициатора
	 */
	public static Initiator of(Long id, InitiatorType type, String name) {
		return new Initiator(id, type, name);
	}

	/**
	 * Возвращает идентификатор инициатора изменения состояния бизнес-объекта. Может отсутствовать
	 */
	public Long id() {
		return id;
	}

	/**
	 * Возвращает тип инициатора изменения состояния бизнес-объекта. Всегда определен
	 */
	public InitiatorType type() {
		return type;
	}

	/**
	 * Возвращает имя инициатора изменения состояния бизнес-объекта. Всегда определено
	 */
	public String name() {
		return name;
	}
}