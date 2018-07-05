package ru.argustelecom.box.env.contract.model;

import static java.util.Collections.unmodifiableList;
import static ru.argustelecom.box.env.contract.model.ContractExtension.ContractExtensionReportContextBands.ContractExtension;
import static ru.argustelecom.box.env.contract.model.ContractExtension.ContractExtensionReportContextBands.ExcludedEntries;
import static ru.argustelecom.box.env.contract.model.ContractExtension.ContractExtensionReportContextBands.IncludedEntries;
import static ru.argustelecom.box.env.contract.model.ContractExtension.ContractExtensionReportContextBands.Owner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.party.OwnerRepository;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.party.model.role.OwnerRdo;
import ru.argustelecom.box.env.report.api.ReportContext;
import ru.argustelecom.box.env.report.api.data.ReportDataList;
import ru.argustelecom.box.env.type.model.Type;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapperDef;
import ru.argustelecom.box.publang.crm.model.IContractExtension;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.utils.CDIHelper;

/**
 * Дополнительное соглаение к договору
 * <p>
 *
 */
@Entity
@Access(AccessType.FIELD)
@EntityWrapperDef(name = IContractExtension.WRAPPER_NAME)
public class ContractExtension extends AbstractContract<ContractExtensionType> {

	private static final long serialVersionUID = 2644718702959384890L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contract_id")
	private Contract contract;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(schema = "system", name = "contract_excluded_entries", joinColumns = @JoinColumn(name = "contract_extension_id"), inverseJoinColumns = @JoinColumn(name = "contract_entry_id"))
	private List<ContractEntry> excludedEntries = new ArrayList<>();

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected ContractExtension() {
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
	 * @see Type#createInstance(Class, Long)
	 */
	protected ContractExtension(Long id) {
		super(id);
	}

	@Override
	public Location getFirstAddressFromEntries() {
		return contract.getFirstAddressFromEntries();
	}

	/**
	 * Возвращает договор, для которого зарегистрировано текущее дополнительное соглашение
	 * 
	 * @return основной договор текущего дополнительного соглашения
	 */
	public Contract getContract() {
		return contract;
	}

	/**
	 * Устанавливает основной договор для текущего дополнительного соглашения. Только для внутреннего пользования. Для
	 * того, чтобы связать договор и дополнительное соглашение пользуйся методами
	 * {@link Contract#addExtension(ContractExtension)} или {@link Contract#removeExtension(ContractExtension)}
	 * 
	 * @param contract
	 */
	protected void setContract(Contract contract) {
		this.contract = contract;
	}

	@Override
	public Date getValidFrom() {
		return getDocumentDate();
	}

	@Override
	public Date getValidTo() {
		return contract.getValidTo();
	}

	/**
	 * Проверяет, что позиция содержится в коллекции исключаемых соглашений.
	 * 
	 * @param entry
	 *            позиция договора или предыдущих дополнительных соглашений
	 * 
	 * @return true, если в текущем дополнительном соглашении позиция исключается
	 */
	public boolean hasExcludedEntry(ContractEntry entry) {
		return excludedEntries.contains(entry);
	}

	/**
	 * Возвращает немодифицируемую коллекцию исключаемых позиций договора
	 * 
	 * @return коллекцию исключаемых позиций или пустую коллекцию, если исключаемые позиции не указаны
	 */
	public List<ContractEntry> getExcludedEntries() {
		return unmodifiableList(excludedEntries);
	}

	/**
	 * Возвращает модифицируемую коллекцию исключаемых позиций договора. Только для внутреннего пользования
	 * 
	 * @return
	 */
	protected List<ContractEntry> excludedEntries() {
		return excludedEntries;
	}

	/**
	 * Добавляет позицию договора в коллекцию исключаемых. Если исключаемая позиция добавлена в этом же дополнительном
	 * соглашении или уже исключена, то выполнение операции невозможно. Исключаемая позиция должна быть в списке
	 * итоговых позиций текущего договора и предыдущих дополнительных соглашений.
	 * 
	 * @param entry
	 *            - позиция для исключения
	 * 
	 * @return true, если позицию удалось успешно исключить
	 */
	public boolean addExcludedEntry(ContractEntry entry) {
		if (hasEntry(entry) || hasExcludedEntry(entry))
			return false;

		if (!contract.hasFinalEntry(entry)) {
			// TODO поругаться
		}

		excludedEntries.add(entry);
		contract.escapeFinalEntriesCache();
		return true;
	}

	/**
	 * Удаляет позицию договора из списка исключаемых
	 * 
	 * @param entry
	 *            - исключенная ранее позиция договора
	 * 
	 * @return true, если позицию удалось успешно удалить из списка исключаемых
	 */
	public boolean removeExcludedEntry(ContractEntry entry) {
		if (!hasExcludedEntry(entry))
			return false;

		excludedEntries.remove(entry);
		contract.escapeFinalEntriesCache();
		return true;
	}

	@Override
	public void fillReportContext(ReportContext reportContext) {
		ReportDataList<ContractEntryRdo> includedEntryRdoList = new ReportDataList<>();
		getEntries().forEach(entry -> includedEntryRdoList.add(entry.createReportData()));

		ReportDataList<ContractEntryRdo> excludedEntryRdoList = new ReportDataList<>();
		getExcludedEntries().forEach(entry -> excludedEntryRdoList.add(entry.createReportData()));
		
		Owner principal = CDIHelper.lookupCDIBean(OwnerRepository.class).findPrincipal();
		OwnerRdo ownerRdo = principal != null ? principal.createReportData() : null;

		reportContext.put(ContractExtension.toString(), createReportData());
		reportContext.put(IncludedEntries.toString(), includedEntryRdoList);
		reportContext.put(ExcludedEntries.toString(), excludedEntryRdoList);
		reportContext.put(Owner.toString(), ownerRdo);
	}

	@Override
	public ContractExtensionRdo createReportData() {
		//@formatter:off
		return ContractExtensionRdo.builder()
					.id(getId())
					.validFrom(getValidFrom())
					.validTo(getValidTo())
					.documentNumber(getDocumentNumber())
					.customer(getCustomer().createReportData())
					.address(getFirstAddressFromEntries().createReportData())
					.contract(getContract().createReportData())
					.properties(getPropertyValueMap())
				.build();
		//@formatter:on
	}

	public enum ContractExtensionReportContextBands {

		ContractExtension, IncludedEntries, ExcludedEntries, Owner

	}

	public static class ContractExtensionQuery<I extends ContractExtension>
			extends AbstractContractQuery<ContractExtensionType, I> {

		private EntityQueryEntityFilter<I, Contract> contract;

		public ContractExtensionQuery(Class<I> entityClass) {
			super(entityClass);
			contract = createEntityFilter(ContractExtension_.contract);
		}

		public EntityQueryEntityFilter<I, Contract> contract() {
			return contract;
		}
	}

}
