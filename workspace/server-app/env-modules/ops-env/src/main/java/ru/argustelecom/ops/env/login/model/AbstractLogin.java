package ru.argustelecom.ops.env.login.model;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.criteria.Predicate;

import ru.argustelecom.ops.inf.modelbase.BusinessObject;
import ru.argustelecom.ops.inf.modelbase.SequenceDefinition;
import ru.argustelecom.system.inf.chrono.TZ;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryDateFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;

@MappedSuperclass
@Access(AccessType.FIELD)
@SequenceDefinition(name = "system.gen_login_id")
public class AbstractLogin extends BusinessObject {

	@Column(nullable = false)
	private String username;

	@Column
	private String description;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(length = 64)
	private String timeZone;

	@Column(length = 64)
	private String locale;

	protected AbstractLogin() {
	}

	public AbstractLogin(Long id) {
		super(id);
		this.created = new Date();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreatedDate() {
		return created;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public TimeZone getTZ() {
		return TZ.getTimeZone(timeZone);
	}

	public Locale getLocale() {
		Locale locale = Locale.forLanguageTag(this.locale);
		return locale != null ? locale : Locale.getDefault();
	}

	public void setLocale(Locale locale) {
		this.locale = locale.toLanguageTag();
	}

	public static abstract class AbstractLoginQuery<T extends AbstractLogin> extends EntityQuery<T> {

		private EntityQueryStringFilter<T> username = createStringFilter(AbstractLogin_.username);
		private EntityQueryStringFilter<T> timeZone = createStringFilter(AbstractLogin_.timeZone);
		private EntityQueryStringFilter<T> locale = createStringFilter(AbstractLogin_.locale);
		private EntityQueryDateFilter<T> created = createDateFilter(AbstractLogin_.created);

		public AbstractLoginQuery(Class<T> entityClass) {
			super(entityClass);
		}

		public EntityQueryStringFilter<T> username() {
			return username;
		}

		public EntityQueryStringFilter<T> timeZone() {
			return timeZone;
		}

		public Predicate timeZone(TimeZone tz) {
			return tz != null ? timeZone.equal(tz.getID()) : null;
		}

		public EntityQueryStringFilter<T> locale() {
			return locale;
		}

		public Predicate locale(Locale locale) {
			return locale != null ? this.locale.equal(locale.toLanguageTag()) : null;
		}

		public EntityQueryDateFilter<T> created() {
			return created;
		}

	}

	private static final long serialVersionUID = 8286810077763670856L;
}
