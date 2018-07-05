package ru.argustelecom.box.env.measure.model;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;

import ru.argustelecom.box.env.measure.nls.MeasureMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.BusinessException;

import com.google.common.base.Strings;

/**
 * Производная единица измерения. Для ее использования должна быть определена базовая единица (т.е. группа, к которой
 * принадлежит данная единица) и коэффициент пересчета.
 */
@Entity
@Access(AccessType.FIELD)
public class DerivedMeasureUnit extends MeasureUnit {

	private static final long serialVersionUID = 3490794446384205278L;

	@ManyToOne(fetch = FetchType.EAGER)
	private BaseMeasureUnit group;

	@Min(1)
	private Long factor;

	protected DerivedMeasureUnit() {
		super();
	}

	public DerivedMeasureUnit(Long id) {
		super(id);
	}

	@Override
	public long toBase(long value) {
		checkFactor();
		return value * factor;
	}

	@Override
	public long toBase(double value) {
		checkFactor();
		return (long) (value * factor);
	}

	@Override
	public double fromBase(long value) throws BusinessException {
		checkFactor();
		return ((double) value) / factor;
	}

	@Override
	public long fromBaseFloor(long value) {
		return (long) fromBase(value);
	}

	@Override
	public String fromBaseAsString(long value) {
		byte countSymbolAfterPoint = (byte) (String.valueOf(factor).length());
		DecimalFormat myFormatter = new DecimalFormat("#.".concat(Strings.repeat("#", countSymbolAfterPoint)));
		return myFormatter.format(fromBase(value));
	}

	/**
	 * Коэффициент для пересчёта количества в базовую единицу измерения
	 */
	public Long getFactor() {
		return factor;
	}

	public void setFactor(Long factor) {
		this.factor = factor;
	}

	public boolean isInvalid() {
		return factor == null || group == null;
	}

	@Override
	public BaseMeasureUnit getGroup() {
		return group;
	}

	public void setGroup(BaseMeasureUnit group) {
		if (!Objects.equals(this.group, group)) {
			if (this.group != null)
				this.group.derivedUnits().remove(this);

			this.group = group;

			if (this.group != null) {
				this.group.derivedUnits().add(this);
				Collections.sort(this.group.derivedUnits(), new Comparator<DerivedMeasureUnit>() {
					@Override
					public int compare(DerivedMeasureUnit o1, DerivedMeasureUnit o2) {
						return Long.compare(o1.getFactor(), o2.getFactor());
					}
				});
			}
		}
	}

	private void checkFactor() {
		if (factor == null) {
			MeasureMessagesBundle messages = LocaleUtils.getMessages(MeasureMessagesBundle.class);
			throw new BusinessException(messages.coefficientNotSpecified());
		}
	}
}
