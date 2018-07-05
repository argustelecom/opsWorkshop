package ru.argustelecom.box.env.billing.reason.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;

import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;

@Entity
@Access(AccessType.FIELD)
public class UserReasonType extends BusinessObject {

	private static final long serialVersionUID = 4617073324061793890L;

	private String name;

	protected UserReasonType() {
	}

	public UserReasonType(Long id, String name) {
		super(id);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static class UserReasonTypeQuery extends EntityQuery<UserReasonType> {

		private EntityQueryStringFilter<UserReasonType> name = createStringFilter(UserReasonType_.name);

		public UserReasonTypeQuery() {
			super(UserReasonType.class);
		}

		public EntityQueryStringFilter<UserReasonType> name() {
			return name;
		}
	}
}
