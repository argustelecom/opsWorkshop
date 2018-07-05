package ru.argustelecom.box.nri.schema.requirements.ip;

import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.box.nri.logicalresources.ip.address.lifecycle.IPAddressState;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddressPurpose;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IpTransferType;
import ru.argustelecom.box.nri.schema.requirements.model.ResourceRequirement;
import ru.argustelecom.box.nri.schema.requirements.ip.model.IpAddressBookingRequirement;
import ru.argustelecom.box.nri.schema.model.ResourceSchema;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;

/**
 * Created by s.kolyada on 22.12.2017.
 */
@Repository
public class IpAddressBookingRequirementRepository implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Сервис генерации ID
	 */
	@Inject
	private IdSequenceService idSequenceService;
	/**
	 * Сервис доступа в БД
	 */
	@PersistenceContext
	private EntityManager em;

	public IpAddressBookingRequirement create(String name, Boolean shouldBePrivate, IPAddressState shouldHaveState,
											  Boolean shouldHaveBooking, Boolean shouldBeStatic, IpTransferType shouldHaveTransferType,
											  IPAddressPurpose shouldHavePurpose, Long schemaId) {
		ResourceSchema schema =  em.find(ResourceSchema.class, schemaId);

		IpAddressBookingRequirement requirement = IpAddressBookingRequirement.builder()
				.id(idSequenceService.nextValue(ResourceRequirement.class))
				.name(name)
				.schema(schema)
				.shouldBePrivate(shouldBePrivate)
				.shouldHaveBooking(shouldHaveBooking)
				.shouldHaveState(shouldHaveState)
				.shouldBeStatic(shouldBeStatic)
				.shouldHaveTransferType(shouldHaveTransferType)
				.shouldHavePurpose(shouldHavePurpose)
				.build();

		schema.getBookings().add(requirement);

		em.persist(requirement);
		em.merge(schema);

		return requirement;
	}

	public IpAddressBookingRequirement findById(Long id) {
		return em.find(IpAddressBookingRequirement.class, id);
	}

	public Boolean remove(Long id) {
		IpAddressBookingRequirement requirement = findById(id);
		if (requirement == null) {
			return false;
		}

		ResourceSchema schema = requirement.getSchema();
		if (schema == null) {
			return false;
		}

		schema.getBookings().remove(requirement);
		em.merge(schema);

		em.remove(requirement);

		return true;
	}
}
