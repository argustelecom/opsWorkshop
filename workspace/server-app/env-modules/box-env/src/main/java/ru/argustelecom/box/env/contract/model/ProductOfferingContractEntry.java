package ru.argustelecom.box.env.contract.model;

import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.initializeAndUnproxy;

import java.util.Comparator;
import java.util.Optional;

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
import ru.argustelecom.box.env.address.model.AddressRdo;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.pricing.model.ProductOffering;
import ru.argustelecom.box.env.pricing.model.ProductOfferingRdo;
import ru.argustelecom.box.env.report.api.data.ReportDataList;
import ru.argustelecom.box.env.type.model.Type;
import ru.argustelecom.box.inf.modelbase.BusinessObject;

@Entity
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOfferingContractEntry extends ContractEntry {

	@Getter
	@Setter
	@ManyToOne(targetEntity = ProductOffering.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "product_offering_id")
	private ProductOffering productOffering;

	public ProductOfferingContractEntry(Long id) {
		super(id);
	}

	@Override
	public boolean isRecurrentProduct() {
		return productOffering.isRecurrentProduct();
	}

	@Override
	public ContractEntryRdo createReportData() {
		ReportDataList<AddressRdo> addresses = new ReportDataList<>();
		getLocations().forEach(address -> addresses.add(address.createReportData()));
		Optional<Location> minLocation = getLocations().stream().min(Comparator.comparing(BusinessObject::getId));

		ProductOffering productOffering = initializeAndUnproxy(getProductOffering());
		ProductOfferingRdo productOfferingRdo = productOffering.createReportData();

		//@formatter:off
		ProductOfferingContractEntryRdo rdo = ProductOfferingContractEntryRdo.builder()
				.id(getId())
				.name(productOffering.getObjectName())
				.addresses(addresses)
				.address(minLocation.map(Location::createReportData).orElse(null))
				.productOffering(productOfferingRdo)
			.build();
		//@formatter:on

		return rdo;
	}

	@Override
	public Type getContractItem() {
		return productOffering != null ? productOffering.getProductType() : null;
	}

	@Override
	public String getObjectName() {
		return String.format("%s (%s)", productOffering.getObjectName(), getContract().getDocumentNumber());
	}

	private static final long serialVersionUID = -6330745737744651637L;

}