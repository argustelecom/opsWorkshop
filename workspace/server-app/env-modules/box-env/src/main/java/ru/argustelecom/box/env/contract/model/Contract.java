package ru.argustelecom.box.env.contract.model;

import static java.util.Collections.unmodifiableList;
import static ru.argustelecom.box.env.contract.ContractUtils.checkExtensionForContract;
import static ru.argustelecom.box.env.contract.model.Contract.ContractReportContextBands.Contract;
import static ru.argustelecom.box.env.contract.model.Contract.ContractReportContextBands.ContractEntries;
import static ru.argustelecom.box.env.contract.model.Contract.ContractReportContextBands.ContractExtensions;
import static ru.argustelecom.box.env.contract.model.Contract.ContractReportContextBands.ContractServiceTypes;
import static ru.argustelecom.box.env.contract.model.Contract.ContractReportContextBands.Owner;
import static ru.argustelecom.box.env.contract.model.ContractState.INFORCE;
import static ru.argustelecom.box.env.contract.model.ContractState.REGISTRATION;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.commodity.model.CommodityGroupRdo;
import ru.argustelecom.box.env.commodity.model.CommodityRdo;
import ru.argustelecom.box.env.commodity.model.CommoditySpec;
import ru.argustelecom.box.env.commodity.model.CommodityTypeGroup;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.party.OwnerRepository;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.party.model.role.OwnerRdo;
import ru.argustelecom.box.env.report.api.Printable;
import ru.argustelecom.box.env.report.api.ReportContext;
import ru.argustelecom.box.env.report.api.data.ReportDataList;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapperDef;
import ru.argustelecom.box.publang.crm.model.IContract;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryDateFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuerySimpleFilter;
import ru.argustelecom.system.inf.exception.BusinessException;
import ru.argustelecom.system.inf.utils.CDIHelper;

/**
 * Договор
 * <p>
 *
 */
@Entity
@Access(AccessType.FIELD)
@EntityWrapperDef(name = IContract.WRAPPER_NAME)
public class Contract extends AbstractContract<ContractType> implements Printable {

	private static final long serialVersionUID = 6446918410348194846L;

	@OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ContractExtension> extensions = new ArrayList<>();

	@Transient
	private List<ContractEntry> finalEntries;

	@Temporal(TemporalType.DATE)
	private Date validFrom;

	@Temporal(TemporalType.DATE)
	private Date validTo;

	private Long currentExtCounter;

	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private PaymentCondition paymentCondition;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "broker_id")
	private PartyRole broker;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected Contract() {
		super();
	}

	/**
	 * Конструктор предназначен для инстанцирования спецификацией. Не делай этот конструктор публичным. Не делай других
	 * публичных конструкторов. Экземпляры спецификаций должны инстанцироваться сугубо спецификацией для обеспечения
	 * корректной инициализации пользовательских свойств или отношений между спецификацией и ее экземпляром.
	 * 
	 * @param id
	 *            - идентификатор экземпляра спецификации, должен быть получен при помощи соответствующего генератора
	 *            через сервис {@link IdSequenceService}
	 * 
	 * @see IdSequenceService
	 */
	protected Contract(Long id) {
		super(id);
	}

	public static boolean checkValidDates(Date validFrom, Date validTo) {
		if (validFrom == null || validTo == null) {
			return false;
		}
		if (validFrom.after(validTo)) {
			throw new BusinessException("Дата начала действия договора больше даты окончания");
		}
		if (validTo.before(validFrom)) {
			throw new BusinessException("Дата окончания действия договора меньше даты начала");
		}
		return true;
	}

	@Override
	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		checkValidDates(validFrom, validTo);
		this.validFrom = validFrom;
	}

	@Override
	public Date getValidTo() {
		return validTo;
	}

	public void setValidTo(Date validTo) {
		checkValidDates(validFrom, validTo);
		this.validTo = validTo;
	}

	/**
	 * Возвращает коллекцию всех позиций текущего договора и всех его <b>действующих (BOX-1153)</b> дополнительных
	 * соглашений, за исключением исключенных в дополниетльном соглашении. Т.к. операция перебирает все известные для
	 * данного договора дополнительные соглашения, то ее результат целесообразно кешировать. Т.е. допускается
	 * множественное обращение к этому методу, например, в циклах.
	 * 
	 * @return сформированную коллекцию всех позиций договора и его дополниетльных соглашений
	 */
	public List<ContractEntry> getFinalEntries() {
		if (finalEntries == null) {
			Set<ContractEntry> collectedEntries = new HashSet<>();
			Set<ContractEntry> excludedEntries = new HashSet<>();

			collectedEntries.addAll(entries());
			extensions.stream().filter(ce -> ce.getState().equals(INFORCE)).forEach(ce -> {
				collectedEntries.addAll(ce.entries());
				excludedEntries.addAll(ce.excludedEntries());
			});

			collectedEntries.removeAll(excludedEntries);
			finalEntries = unmodifiableList(new ArrayList<>(collectedEntries));
		}
		return finalEntries;
	}

	public List<ContractEntry> getFinalRecurrentEntries() {
		return getFinalEntries().stream().filter(ContractEntry::isRecurrentProduct).collect(Collectors.toList());
	}

	/**
	 * Сбрасывает кэш позиций договора, предназначен для внутреннего пользования для поддержания согласованности
	 * состояний договора, его дополнительных соглашений и их совокупных позиций
	 */
	protected void escapeFinalEntriesCache() {
		finalEntries = null;
	}

	/**
	 * Проверяет, есть ли указанная позиция в самом договоре или в одном из его дополнительных соглашений. Метод
	 * последовательно перебирает договор и все его дополнительные соглашения, если находит позицию в списке исключенных
	 * в одном из дополнительных соглашений, то результат будет однозначным -- указанная позиция больше не содержится в
	 * договоре. Подразумевается, что единожды исключенная позиция исключена навсегда и если ее нужно будет включить
	 * вновь, то для этого нужно будет составить новое дополнительное соглашение
	 * 
	 * @param entry
	 *            - проверяемая позиция
	 * 
	 * @return true если указанная позиция содержится в списке финальных позиций договора
	 */
	public boolean hasFinalEntry(ContractEntry entry) {
		boolean result = this.hasEntry(entry);
		for (ContractExtension extension : extensions) {
			result = result || extension.hasEntry(entry);
			if (extension.hasExcludedEntry(entry)) {
				return false;
			}
		}
		return result;
	}

	@Override
	public boolean addEntry(ContractEntry entry) {
		boolean result = super.addEntry(entry);
		if (result) {
			escapeFinalEntriesCache();
		}
		return result;
	}

	@Override
	public boolean removeEntry(ContractEntry entry) {
		boolean result = super.removeEntry(entry);
		if (result) {
			escapeFinalEntriesCache();
		}
		return result;
	}

	public Long nextExtensionNumber() {
		if (currentExtCounter == null) {
			currentExtCounter = 1L;
		}
		return currentExtCounter++;
	}

	/**
	 * Доп. соглашение запрещено создавать в случае если:
	 * <ul>
	 * <li>договор в статусе "Оформление" или "Закрыт"</li>
	 * <li>у договора есть доп. соглашение в статусе "Оформление"</li>
	 * </ul>
	 */
	public boolean canCreateExtension() {
		boolean stateIsInforce = getState().equals(INFORCE);
		boolean haveExtensionInRegistrationState = getExtensions().stream()
				.anyMatch(extension -> extension.getState().equals(REGISTRATION));
		return stateIsInforce && !haveExtensionInRegistrationState;
	}

	/**
	 * Возвращает немодифицируемую коллекцию всех дополнительных соглашений текущего договора
	 * 
	 * @return коллекцию дополнительных соглашений или пустую коллекцию, если дополнительных соглашений для текущего
	 *         договора нет
	 */
	public List<ContractExtension> getExtensions() {
		return unmodifiableList(extensions);
	}

	/**
	 * Проверяет, содержит ли текущий договор указанное дополнительное соглашение
	 * 
	 * @param extension
	 *            - проверяемое дополнительное соглашение
	 * 
	 * @return true, если указанное соглашение содержится в текущем договоре
	 */
	public boolean hasExtension(ContractExtension extension) {
		return Objects.equals(this, extension.getContract());
	}

	/**
	 * Добавляет дополнительное соглашение к текущему договору. При добавлении выполнят следующие проверки:
	 * допонительное соглашение еще не добавлено к договору, дополнительное соглашение предназачено для клиентов той же
	 * спецификации, для которых предназначен текущий договор.
	 * 
	 * @param extension
	 *            - дополнительное соглашение договора
	 * 
	 * @return true, если дополнительное соглашение успешно добавлено в договор
	 */
	public boolean addExtension(ContractExtension extension) {
		if (!hasExtension(extension)) {

			checkExtensionForContract(this, extension);
			if (extension.getContract() != null) {
				extension.getContract().removeExtension(extension);
			}

			if (extension.getCustomer() == null) {
				extension.setCustomer(this.getCustomer());
			}

			extension.setContract(this);
			extensions.add(extension);
			escapeFinalEntriesCache();
			return true;
		}
		return false;
	}

	/**
	 * Удаляет дополниетльное соглашение из текущего договора
	 * 
	 * @param extension
	 *            - дополнительное соглашение договора
	 * 
	 * @return true, если дополнительное соглашение успешно удалено из договора
	 */
	public boolean removeExtension(ContractExtension extension) {
		if (hasExtension(extension)) {
			extension.setContract(null);
			extensions.remove(extension);
			escapeFinalEntriesCache();
			return true;
		}
		return false;
	}

	@Override
	public void fillReportContext(ReportContext reportContext) {
		ReportDataList<ContractEntryRdo> entryRdoList = new ReportDataList<>();
		getEntries().forEach(entry -> entryRdoList.add(entry.createReportData()));

		ReportDataList<ContractExtensionRdo> extensionRdoList = new ReportDataList<>();
		getExtensions().forEach(extension -> extensionRdoList.add(extension.createReportData()));

		Owner principal = CDIHelper.lookupCDIBean(OwnerRepository.class).findPrincipal();
		OwnerRdo ownerRdo = principal != null ? principal.createReportData() : null;

		reportContext.put(Contract.toString(), createReportData());
		reportContext.put(ContractEntries.toString(), entryRdoList);
		reportContext.put(ContractExtensions.toString(), entryRdoList);
		reportContext.put(Owner.toString(), ownerRdo);
		putServiceTypesTo(reportContext);

	}

	private void putServiceTypesTo(ReportContext reportContext) {
		// информация по услугам, с группировкой по их категориям
		ContractUnits contractUnits = CDIHelper.lookupCDIBean(ContractUnits.class);
		Map<CommodityTypeGroup, List<CommoditySpec<?>>> commoditiesMap = contractUnits
				.getCommoditiesMap(getProductOfferingEntries());

		ReportDataList<CommodityGroupRdo> servicesTypes = new ReportDataList<>();
		commoditiesMap.keySet().forEach(category -> {
			ReportDataList<CommodityRdo> commodityRdoList = wrapToRdo(commoditiesMap.get(category));
			//@formatter:off
			servicesTypes.add(
				CommodityGroupRdo.builder()
					.id(category.getId())
					.name(category.getObjectName())
					.commodities(commodityRdoList)
				.build()
			);
			//@formatter:on
		});

		reportContext.put(ContractServiceTypes.toString(), servicesTypes);
	}

	private ReportDataList<CommodityRdo> wrapToRdo(List<CommoditySpec<?>> commodities) {
		ReportDataList<CommodityRdo> commodityRdoList = new ReportDataList<>();

		commodities.forEach(commodity -> {
			//@formatter:off
			commodityRdoList.add(
				CommodityRdo.builder()
					.id(commodity.getId())
					.categoryName(commodity.getType().getGroup().getObjectName())
					.properties(commodity.getPropertyValueMap())
				.build()
			);
			//@formatter:on
		});
		return commodityRdoList;
	}

	@Override
	public ContractRdo createReportData() {
		//@formatter:off
		return ContractRdo.builder()
					.id(getId())
					.validFrom(getValidFrom())
					.validTo(getValidTo())
					.documentNumber(getDocumentNumber())
					.customer(getCustomer().createReportData())
					.address(getFirstAddressFromEntries() != null ? getFirstAddressFromEntries().createReportData() : null)
					.properties(getPropertyValueMap())
					.provider(getType().getProvider().createReportData())
					.broker(getBroker() != null ? getBroker().createReportData() : null)
				.build();
		//@formatter:on
	}

	public enum ContractReportContextBands {

		Contract, ContractEntries, ContractExtensions, ContractServiceTypes, Owner

	}

	public static class ContractQuery<I extends Contract> extends AbstractContractQuery<ContractType, I> {

		private EntityQueryDateFilter<I> validFrom;
		private EntityQueryDateFilter<I> validTo;
		private EntityQueryEntityFilter<I, PartyRole> broker;
		private Join<I, ContractType> typeJoin;
		private EntityQuerySimpleFilter<I, ContractCategory> category;

		public ContractQuery(Class<I> entityClass) {
			super(entityClass);
			validFrom = createDateFilter(Contract_.validFrom);
			validTo = createDateFilter(Contract_.validTo);
			broker = createEntityFilter(Contract_.broker);
			category = createFilter(typeJoin().get(ContractType_.contractCategory), ContractType_.contractCategory);
		}

		public EntityQueryDateFilter<I> validFrom() {
			return validFrom;
		}

		public EntityQueryDateFilter<I> validTo() {
			return validTo;
		}

		public EntityQueryEntityFilter<I, PartyRole> broker() {
			return broker;
		}

		public Predicate byProvider(PartyRole provider) {
			return criteriaBuilder().equal(contractTypeJoin().get(ContractType_.provider),
					createParam(ContractType_.provider, provider));
		}

		public EntityQuerySimpleFilter<I, ContractCategory> category() {
			return category;
		}

		private Join<I, ContractType> typeJoin() {
			if (typeJoin == null) {
				typeJoin = root().join(Contract_.type.getName());
			}
			return typeJoin;
		}
	}
}