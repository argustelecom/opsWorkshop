package ru.argustelecom.box.env.commodity.telephony.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.commodity.model.Commodity;
import ru.argustelecom.box.env.commodity.model.OptionSpec;
import ru.argustelecom.box.env.commodity.model.OptionType;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.contract.model.ContractEntry;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;

/**
 * Экземпляр опции.
 *
 * <p>
 * <a href="http://boxwiki.argustelecom.ru:10753/pages/viewpage.action?pageId=6717460">Описание в Confluence</a>
 * </p>
 */
@Entity
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Option<T extends OptionType, S extends OptionSpec<T>> extends Commodity<T, S> {

	/**
	 * Услуга, в рамках которой предоставляется опция.
	 */
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Service.class)
	@JoinColumn(name = "service_id", updatable = false, nullable = false)
	private Service service;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "option_subject_id", updatable = false, nullable = false)
	private ContractEntry subject;

	public Option(Long id) {
		super(id);
	}

	public static class OptionQuery<T extends OptionType, S extends OptionSpec<T>, I extends Option<T, S>>
			extends CommodityQuery<T, S, I> {

		private EntityQueryEntityFilter<I, Service> service;
		private EntityQueryEntityFilter<I, ContractEntry> subject;

		public OptionQuery(Class<I> entityClass) {
			super(entityClass);
			service = createEntityFilter(Option_.service);
			subject = createEntityFilter(Option_.subject);
		}

		public EntityQueryEntityFilter<I, Service> service() {
			return service;
		}

		public EntityQueryEntityFilter<I, ContractEntry> subject() {
			return subject;
		}

	}

	private static final long serialVersionUID = -5923784220360001291L;

}