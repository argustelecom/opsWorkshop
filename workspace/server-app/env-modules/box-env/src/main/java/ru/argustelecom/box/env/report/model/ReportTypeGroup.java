package ru.argustelecom.box.env.report.model;

import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.util.HasParent;
import ru.argustelecom.box.inf.modelbase.BusinessDirectory;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;

/**
 * Сущность определяет группу типов отчетов
 */
@Entity
@Table(schema = "system", uniqueConstraints = {
		@UniqueConstraint(name = "uc_report_type_group", columnNames = { "keyword", "parent_id" }),
		@UniqueConstraint(name = "uc_report_type_group_name", columnNames = { "name" }) })
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportTypeGroup extends BusinessDirectory implements HasParent<ReportTypeGroup> {

	@Getter
	@Setter
	@Column(length = 64, unique = true)
	private String keyword;

	/**
	 * Родительская группа
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private ReportTypeGroup parent;

	public ReportTypeGroup(Long id) {
		super(id);
	}

	@Override
	@Access(AccessType.PROPERTY)
	@Column(name = "name", length = 128, unique = true, nullable = false)
	public String getObjectName() {
		return super.getObjectName();
	}

	@Override
	public ReportTypeGroup getParent() {
		return parent;
	}

	public void changeParent(ReportTypeGroup newParent) {
		if (!Objects.equals(parent, newParent)) {
			checkCircularDependency(newParent);
			this.parent = newParent;
		}
	}

	public static class ReportTypeGroupQuery extends EntityQuery<ReportTypeGroup> {

		private final EntityQueryEntityFilter<ReportTypeGroup, ReportTypeGroup> parent;
		private final EntityQueryStringFilter<ReportTypeGroup> name;
		private final EntityQueryStringFilter<ReportTypeGroup> keyword;

		public ReportTypeGroupQuery() {
			super(ReportTypeGroup.class);
			keyword = createStringFilter(ReportTypeGroup_.keyword);
			name = createStringFilter(ReportTypeGroup_.objectName);
			parent = createEntityFilter(ReportTypeGroup_.parent);
		}

		public EntityQueryEntityFilter<ReportTypeGroup, ReportTypeGroup> parent() {
			return parent;
		}

		public EntityQueryStringFilter<ReportTypeGroup> name() {
			return name;
		}

		public EntityQueryStringFilter<ReportTypeGroup> keyword() {
			return keyword;
		}
	}

	private static final long serialVersionUID = -8891858537205014480L;
}
