package ru.argustelecom.box.env.report.model;

import static ru.argustelecom.box.env.type.model.TypePropertyRef.*;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.util.Collections;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.type.model.SupportedProperties;
import ru.argustelecom.box.env.type.model.Type;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;

/**
 * Тип отчета
 */
@Entity
@Table(schema = "system", uniqueConstraints = {
		@UniqueConstraint(name = "uc_report_type_name", columnNames = { "name", "group_id" }) })
@Access(AccessType.FIELD)
@SupportedProperties({DOUBLE, LONG, TEXT, DATE, LOGICAL})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportType extends Type implements LifecycleObject<ReportTypeState>, HasTemplates {

	/**
	 * Группа, к которой принадлежит данный тип
	 */
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id", nullable = false)
	private ReportTypeGroup group;

	/**
	 * Связанные с данными типом отчета шаблоны
	 */
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	//@formatter:off
	@JoinTable(name = "report_type_report_model_template", schema = "system",
			joinColumns = @JoinColumn(name = "report_type_id"),
			inverseJoinColumns = @JoinColumn(name = "report_model_template_id"))
	//@formatter:on
	private List<ReportModelTemplate> templates = Lists.newArrayList();

	/**
	 * Корневая системная полоса
	 */
	@Getter
	@Setter
	@OneToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "root_band_id")
	private ReportBandModel rootBand;

	@Enumerated(EnumType.STRING)
	private ReportTypeState state;

	public ReportType(Long id) {
		super(id);
	}

	public List<ReportModelTemplate> getTemplates() {
		return Collections.unmodifiableList(templates);
	}

	public boolean addTemplate(ReportModelTemplate template) {
		checkRequiredArgument(template, "template");

		boolean contains = templates.contains(template);
		if (!contains) {
			templates.add(template);
		}

		return !contains;
	}

	public boolean removeTemplate(ReportModelTemplate template) {
		checkRequiredArgument(template, "template");
		return templates.remove(template);
	}

	@Override
	public ReportTypeState getState() {
		return state;
	}

	@Override
	public void setState(ReportTypeState state) {
		this.state = state;
	}

	public static class ReportTypeQuery extends TypeQuery<ReportType> {

		private final EntityQueryEntityFilter<ReportType, ReportTypeGroup> group;

		public ReportTypeQuery() {
			super(ReportType.class);
			group = createEntityFilter(ReportType_.group);
		}

		public EntityQueryEntityFilter<ReportType, ReportTypeGroup> group() {
			return group;
		}
	}

	private static final long serialVersionUID = -6658096286859103629L;
}
