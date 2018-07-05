package ru.argustelecom.box.env.billing.subscription;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.billing.provision.ProvisionTermsDto;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionState;
import ru.argustelecom.box.env.dto.IdentifiableDto;
import ru.argustelecom.box.env.service.ServiceDto;
import ru.argustelecom.box.env.stl.Money;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class SubscriptionDto implements IdentifiableDto {

	private Long id;
	private String productName;
	private ProvisionTermsDto provisionTerms;
	private SubjectCauseDto subjectCause;
	private Money cost;
	private Long pricelistId;
	private String costCauseName;
	private SubscriptionState state;
	private Date validFrom;
	private Date validTo;
	private List<String> locations;
	private List<ServiceDto> services;

	/**
	 * Информация о {@linkplain ru.argustelecom.box.env.privilege.model.Privilege привилегии} предоставляемой для
	 * подписки.
	 */
	private String privilegeInfo;

	@Builder
	public SubscriptionDto(Long id, String productName, ProvisionTermsDto provisionTerms, SubjectCauseDto subjectCause,
			Money cost, Long pricelistId, String costCauseName, SubscriptionState state, Date validFrom, Date validTo,
			List<String> locations, List<ServiceDto> services, String privilegeInfo) {
		this.id = id;
		this.productName = productName;
		this.provisionTerms = provisionTerms;
		this.subjectCause = subjectCause;
		this.cost = cost;
		this.pricelistId = pricelistId;
		this.costCauseName = costCauseName;
		this.state = state;
		this.validFrom = validFrom;
		this.validTo = validTo;
		this.locations = locations;
		this.services = services;
		this.privilegeInfo = privilegeInfo;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Class<Subscription> getEntityClass() {
		return Subscription.class;
	}

	public boolean hasOneService() {
		return !CollectionUtils.isEmpty(getServices()) && getServices().size() == 1;
	}

	public boolean hasSeveralServices() {
		return !CollectionUtils.isEmpty(getServices()) && getServices().size() > 1;
	}

	public ServiceDto getFirstService() {
		return !CollectionUtils.isEmpty(getServices()) ? getServices().get(0) : null;
	}

	public boolean hasPrivilege() {
		return privilegeInfo != null;
	}

}