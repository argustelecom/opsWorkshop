package ru.argustelecom.box.env.measure.model;

import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import com.google.common.base.Strings;

import ru.argustelecom.box.inf.modelbase.BusinessDirectory;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryLogicalFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;

/**
 * Единица измерения - определяет в каких единицах исчисляется Номенклатурная позиция
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "measure_unit", uniqueConstraints = {
		@UniqueConstraint(name = "unq_measure_unit_keyword", columnNames = { "keyword" }),
		@UniqueConstraint(name = "unq_measure_unit_symbol", columnNames = { "symbol" }) })
public abstract class MeasureUnit extends BusinessDirectory {
	private static final long serialVersionUID = -8109681377353211751L;

	@Column(length = 16)
	private String code;

	@Column(length = 64)
	private String name;

	@Column(length = 8)
	private String symbol;

	@Column(length = 32)
	private String keyword;

	@Version
	private Long version;

	private boolean isSys = false;

	protected MeasureUnit() {
	}

	public MeasureUnit(Long id) {
		super(id);
	}

	/**
	 * Базовая единица (она же описание группы). Определяется конкретными потомками
	 */
	@Transient
	public abstract BaseMeasureUnit getGroup();

	/**
	 * Пересчитывает указанное количество в базовые единицы измерения
	 * 
	 * @param value
	 *            количество ресурса, указанное в данных единицах измерения
	 */
	public abstract long toBase(long value);

	/**
	 * Пересчитывает указанное количество в базовые единицы измерения
	 *
	 * @param value
	 *            количество ресурса, указанное в данных единицах измерения
	 */
	public abstract long toBase(double value);

	/**
	 * Пересчитывает указанное количество (в базовых единицах) в собственные единицы измерения
	 * 
	 * @param value
	 *            количество ресурса, указанное в базовых единицах измерения
	 */
	public abstract double fromBase(long value);

	/**
	 * Пересчитывает указанное количество (в базовых единицах) в собственные единицы измерения, представляя в виде
	 * строки. Округление до 2ух знаков после запятой.
	 *
	 * @param value
	 *            количество ресурса, указанное в базовых единицах измерения
	 */
	public abstract String fromBaseAsString(long value);

	/**
	 * Пересчитывает указанное количество (в базовых единицах), в собственные единицы измерения, и округляет до
	 * минимального целого значения
	 * 
	 * @param value
	 *            количество ресурса, указанное в базовых единицах измерения
	 */
	public abstract long fromBaseFloor(long value);

	public boolean isConvertibleFrom(MeasureUnit other) {
		return Objects.equals(getGroup(), other.getGroup());
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String value) {
		this.name = value;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getKeyword() {
		return keyword;
	}

	@Override
	public String getObjectName() {
		return symbol;
	}

	@Override
	public Boolean getIsSys() {
		return isSys || !Strings.isNullOrEmpty(keyword);
	}

	public void setIsSys(boolean isSys) {
		this.isSys = isSys;
	}

	public static class MeasureUnitQuery extends EntityQuery<MeasureUnit> {

		private EntityQueryStringFilter<MeasureUnit> code = createStringFilter(MeasureUnit_.code);
		private EntityQueryStringFilter<MeasureUnit> name = createStringFilter(MeasureUnit_.name);
		private EntityQueryStringFilter<MeasureUnit> keyword = createStringFilter(MeasureUnit_.keyword);
		private EntityQueryStringFilter<MeasureUnit> symbol = createStringFilter(MeasureUnit_.symbol);
		private EntityQueryLogicalFilter<MeasureUnit> isSys = createLogicalFilter(MeasureUnit_.isSys);

		public MeasureUnitQuery() {
			super(MeasureUnit.class);
		}

		public EntityQueryStringFilter<MeasureUnit> code() {
			return code;
		}

		public EntityQueryStringFilter<MeasureUnit> name() {
			return name;
		}

		public EntityQueryStringFilter<MeasureUnit> keyword() {
			return keyword;
		}

		public EntityQueryStringFilter<MeasureUnit> symbol() {
			return symbol;
		}

		public EntityQueryLogicalFilter<MeasureUnit> isSys() {
			return isSys;
		}
	}
}
