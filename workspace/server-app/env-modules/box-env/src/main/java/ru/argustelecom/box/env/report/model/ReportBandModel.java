package ru.argustelecom.box.env.report.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.haulmont.yarg.structure.BandOrientation.HORIZONTAL;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static ru.argustelecom.box.env.type.model.Ordinal.normalize;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import com.haulmont.yarg.structure.BandOrientation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.type.model.Ordinal;
import ru.argustelecom.box.env.util.HasParent;
import ru.argustelecom.box.inf.modelbase.MetadataUnit;
import ru.argustelecom.system.inf.modelbase.Identifiable;

/**
 * Сущность определяет модель для полосы отчета
 */
@Entity
@Table(schema = "system", uniqueConstraints = {
		@UniqueConstraint(name = "uc_report_band_model_keyword", columnNames = { "keyword", "parent_id" }) })
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportBandModel extends MetadataUnit<Long> implements Identifiable, HasParent<ReportBandModel>, Ordinal {

	public static final String ROOT_BAND_KEYWORD = "root";
	public static final String EMPTY_GROOVY_SCRIPT = "return []";
	public static final String EMPTY_SQL_SCRIPT = "";
	public static final Integer INITIAL_ORDINAL_NUMBER = 0;

	/**
	 * Родительская полоса
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private ReportBandModel parent;

	/**
	 * Дочерние полосы
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
	private List<ReportBandModel> children = Lists.newArrayList();

	/**
	 * Ориентация полосы
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private BandOrientation orientation = HORIZONTAL;

	/**
	 * Способ получения данных (Groovy, Sql).
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 64)
	private DataLoaderType dataLoaderType;

	/**
	 * Текст запроса для данной полосы. Для Groovy скрипта значение не должно быть пустым, вместо этого оно должно
	 * равняться {@link #EMPTY_GROOVY_SCRIPT}. Для Sql данное значение может быть пустып если {@linkplain ReportType тип
	 * отчёта} ещё не активирован.
	 */
	@Getter
	@Size(max = 5000)
	@Column(columnDefinition = "query_text")
	private String query;

	/**
	 * Порядковый номер
	 */
	@Getter
	@Setter
	private Integer ordinalNumber;

	public ReportBandModel(Long id) {
		super(id);
	}

	@Id
	@Access(AccessType.PROPERTY)
	@Override
	public Long getId() {
		return super.getId();
	}

	@Override
	public ReportBandModel getParent() {
		return parent;
	}

	public List<ReportBandModel> getChildren() {
		return unmodifiableList(children);
	}

	@Override
	public void changeParent(ReportBandModel newParent) {

		BiConsumer<ReportBandModel, Consumer<ReportBandModel>> fun = (parent, consumer) -> ofNullable(parent)
				.ifPresent(consumer.andThen(p -> normalize(p.group())));

		if (!Objects.equals(parent, newParent)) {
			checkCircularDependency(newParent);

			if (isNull(ordinalNumber)) {
				setOrdinalNumber(ofNullable(newParent).map(p -> p.getChildren().size()).orElse(initialOrdinalNumber()));
			}

			fun.accept(parent, parent -> parent.getModifiableChildren().remove(this));
			fun.accept(newParent, parent -> parent.getModifiableChildren().add(this));
			parent = newParent;
		}
	}

	@Override
	public List<? extends Ordinal> group() {
		return ofNullable(parent).map(ReportBandModel::getChildren).orElse(getChildren());
	}

	@Override
	public Integer initialOrdinalNumber() {
		return INITIAL_ORDINAL_NUMBER;
	}

	@Override
	public String getObjectName() {
		return getKeyword();
	}

	public void setQuery(String query) {
		if (dataLoaderType.equals(DataLoaderType.GROOVY) && StringUtils.isEmpty(query)) {
			this.query = EMPTY_GROOVY_SCRIPT;
		} else if (dataLoaderType.equals(DataLoaderType.SQL) && isNull(query)) {
			this.query = EMPTY_SQL_SCRIPT;
		} else {
			this.query = query;
		}
	}

	@Override
	protected Long checkId(Long id) {
		return checkNotNull(id);
	}

	private List<ReportBandModel> getModifiableChildren() {
		return children;
	}

	private static final long serialVersionUID = 1873047252418186367L;
}