package ru.argustelecom.box.nri.logicalresources.phone.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Пул телефонных номеров
 * Created by s.kolyada on 27.10.2017.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "nri", name = "phone_number_pool")
@Getter
@Setter
public class PhoneNumberPool extends BusinessObject implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "name", nullable = false)
	private String name;

	/**
	 * Список телефонныхъ номеров входящих в пул
	 */
	@OneToMany(mappedBy = "pool", cascade = CascadeType.ALL)
	private List<PhoneNumber> phoneNumbers = new ArrayList<>();

	/**
	 * коментарий
	 */
	@Column(name = "comment")
	private String comment;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected PhoneNumberPool() {
		super();
	}

	/**
	 * Конструктор по id
	 *
	 * @param id идентификатор
	 */
	public PhoneNumberPool(Long id) {
		super(id);
	}

	/**
	 * Конструктор
	 *
	 * @param id           id
	 * @param name         имя
	 * @param comment      комментарий
	 * @param phoneNumbers список номеров телефонов
	 */
	@Builder
	public PhoneNumberPool(Long id, String name, String comment, List<PhoneNumber> phoneNumbers) {
		super(id);
		this.name = name;
		this.comment = comment;
		this.phoneNumbers = Optional.ofNullable(phoneNumbers).orElse(new ArrayList<>());
	}

	/**
	 * Добавить номер телефона к пулу
	 *
	 * @param number номер телефона
	 * @return true, если номер был добавлен, иначе false
	 */
	public boolean addPhoneNumber(PhoneNumber number) {
		return phoneNumbers.add(number);
	}

	/**
	 * Удалить номер телефона из пула
	 *
	 * @param number номер для удаления
	 * @return true, если номер был удален, иначе false
	 */
	public boolean removePhoneNumber(PhoneNumber number) {
		return phoneNumbers.remove(number);
	}

	/**
	 * Класс для создания criteriaQuery c фильтрами по полям
	 */
	public static class PhoneNumberPoolQuery extends EntityQuery<PhoneNumberPool> {

		/**
		 * Фильтр имени
		 */
		private EntityQueryStringFilter<PhoneNumberPool> name;

		/**
		 * Констурктор запроса
		 *
		 * @param entityClass класс сущности
		 */
		public PhoneNumberPoolQuery(Class<PhoneNumberPool> entityClass) {
			super(entityClass);
			name = createStringFilter(PhoneNumberPool_.name);
		}
	}
}
