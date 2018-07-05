package ru.argustelecom.box.env.contract.model;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.address.model.AddressRdo;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.commodity.telephony.model.Option;
import ru.argustelecom.box.env.report.api.Printable;
import ru.argustelecom.box.env.report.api.data.ReportDataList;
import ru.argustelecom.box.env.type.model.Type;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.inf.modelbase.SequenceDefinition;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapperDef;
import ru.argustelecom.box.publang.crm.model.IContractEntry;
import ru.argustelecom.system.inf.modelbase.SuperClass;

/**
 * Абстрактное понятние - позиция договора.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "contract_entry")
@SequenceDefinition
@EntityWrapperDef(name = IContractEntry.WRAPPER_NAME)
public abstract class ContractEntry extends SuperClass implements Printable {

	@Getter
	@Setter
	@ManyToOne(targetEntity = AbstractContract.class, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "contract_id")
	private AbstractContract<?> contract;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "personal_account_id")
	private PersonalAccount personalAccount;

	/**
	 * Список опций.
	 * <p>
	 * Для {@linkplain ProductOfferingContractEntry позиции договора на основании продуктового предложения}. В список
	 * опций должны попрасть опции всех услуг, которые входят в продуктовое предложение. Если продуктовое предложение на
	 * основании {@linkplain ru.argustelecom.box.env.product.model.ProductTypeComposite состовного продукта}, то в
	 * данном списке должны быть опции всех услуг по данному составному продукту.
	 * </p>
	 * <p>
	 * Для {@linkplain OptionContractEntry позции договора на основании опции}. В списке должна быть только одна опция.
	 * </p>
	 */
	//@formatter:off
	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(
		schema = "system", 
		name = "contract_entry_locations", 
		joinColumns = @JoinColumn(name = "contract_entry_id"), 
		inverseJoinColumns = @JoinColumn(name = "location_id")
	)//@formatter:on
	private List<Location> locations = new ArrayList<>();

	@OneToMany(targetEntity = Option.class, mappedBy = "subject")
	private List<Option> options = new ArrayList<>();

	protected ContractEntry() {
	}

	public ContractEntry(Long id) {
		super(id, SuperClass.ENT_SUPER_CLASS);
	}

	public abstract Type getContractItem();

	public boolean isRecurrentProduct() {
		return false;
	}

	@Override
	public ContractEntryRdo createReportData() {

		ReportDataList<AddressRdo> addresses = new ReportDataList<>();
		getLocations().forEach(address -> addresses.add(address.createReportData()));
		Optional<Location> minLocation = getLocations().stream().min(Comparator.comparing(BusinessObject::getId));

		return new ContractEntryRdo(getId(), getObjectName(), minLocation.map(Location::createReportData).orElse(null),
				addresses);
	}

	@Id
	@Override
	@Access(AccessType.PROPERTY)
	public Long getId() {
		return super.getId();
	}

	public boolean hasLocation(Location location) {
		return locations.contains(location);
	}

	public List<Location> getLocations() {
		return unmodifiableList(locations);
	}

	public boolean addLocation(Location location) {
		if (!hasLocation(location)) {
			locations.add(location);
			return true;
		}
		return false;
	}

	public List<Option> getOptions() {
		return Collections.unmodifiableList(options);
	}

	public void addOption(Option option) {
		if (!options.contains(option)) {
			options.add(option);
		}
	}

	public void removeOption(Option option) {
		if (options.contains(option)) {
			options.remove(option);
		}
	}

	public boolean removeLocation(Location location) {
		return locations.remove(location);
	}

	private static final long serialVersionUID = -9141368919575586674L;
}
