package ru.argustelecom.box.env.components;

import static java.util.Collections.sort;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.ensure;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.faces.context.FacesContext;

import com.google.common.base.Strings;

import ru.argustelecom.box.env.measure.model.BaseMeasureUnit;
import ru.argustelecom.box.env.measure.model.DerivedMeasureUnit;
import ru.argustelecom.box.env.measure.model.Measurable;
import ru.argustelecom.box.env.measure.model.MeasureUnit;
import ru.argustelecom.box.env.measure.model.MeasureUnit.MeasureUnitQuery;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;

public abstract class AbstractInputMeasuredValue extends AbstractCompositeInput {

	private EntityConverter converter = new EntityConverter();

	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		Measurable value = getValue();
		BaseMeasureUnit baseMeasure = (BaseMeasureUnit) getAttributes().get(AttributeKeys.baseMeasure.toString());
		MeasureUnit defaultMeasure = (MeasureUnit) getAttributes().get(AttributeKeys.defaultMeasure.toString());

		initMeasurableValue(value, baseMeasure, defaultMeasure);
		super.encodeBegin(context);
	}

	protected void initMeasurableValue(Measurable value, BaseMeasureUnit baseMeasure, MeasureUnit defaultMeasure) {
		MeasureUnit rawMeasure = null;

		if (value != null) {
			rawMeasure = value.getMeasureUnit();
		}

		// Все указанные в компоненте единицы измерения должны быть совместимы между собой, т.е. принадлежать одной
		// группе
		checkMeasuresConsistency(rawMeasure, baseMeasure, defaultMeasure);

		// Если изменилась базовая единица измерения или список возможных единиц измерения еще не проинициализирован
		if (baseMeasureChanged(baseMeasure) || getPossibleMeasures() == null) {
			if (baseMeasure == null && defaultMeasure == null) {
				// В варианте, когда не указано ничего, то выбор ничем не ограничивается, поэтому пользователь может
				// выбирать из любых единиц измерения
				setPossibleMeasures(queryAllMeasures());
			} else if (baseMeasure != null) {
				// Если указана базовая единица измерения, то пользователь может выбирать только в пределах этой базовой
				// единицы измерения
				setPossibleMeasures(queryGrouppedMeasures(baseMeasure));
			} else {
				// Во всех остальных случаях пользователь ничего не может выбирать, будет использована жестко
				// фиксированная единица измерения по умолчанию
				setPossibleMeasures(Collections.emptyList());
			}
			setBaseMeasure(baseMeasure);
		}

		if (defaultMeasureChanged(defaultMeasure)) {
			setDefaultMeasure(defaultMeasure);
		}

		// Если не указана единица измерения в самом value, но указана дефолтная единица измерения и мы не можем
		// выбирать, то сразу и навсегда инициализируем rawMeasure
		if (rawMeasure == null && defaultMeasure != null && !canSelectMeasure()) {
			rawMeasure = defaultMeasure;
		}

		// Здесь умышленно не вызывается сеттер, так как это может привесит к передергиванию компонента при
		// инициализации
		if (rawMeasureChanged(rawMeasure)) {
			setPrivateState(PropertyKeys.rawMeasure, rawMeasure);
		}
	}

	protected List<MeasureUnit> queryGrouppedMeasures(BaseMeasureUnit baseMeasure) {
		List<MeasureUnit> possibleMeasures = baseMeasure.getDerivedUnits().stream().map(u -> (MeasureUnit) u)
				.collect(Collectors.toList());
		possibleMeasures.add(0, baseMeasure);
		return possibleMeasures;
	}

	protected List<MeasureUnit> queryAllMeasures() {
		MeasureUnitQuery query = new MeasureUnitQuery();
		List<MeasureUnit> possibleMeasures = query.createTypedQuery(ensure(null)).getResultList();

		sort(possibleMeasures, (MeasureUnit u1, MeasureUnit u2) -> {
			String groupName1 = Strings.nullToEmpty(u1.getGroup().getObjectName());
			String groupName2 = Strings.nullToEmpty(u2.getGroup().getObjectName());

			int groupComparison = groupName1.compareTo(groupName2);
			if (groupComparison == 0) {
				if (u1 instanceof BaseMeasureUnit) {
					return -1;
				}
				if (u2 instanceof BaseMeasureUnit) {
					return 1;
				}

				DerivedMeasureUnit d1 = (DerivedMeasureUnit) u1;
				DerivedMeasureUnit d2 = (DerivedMeasureUnit) u2;
				return Long.compare(d1.getFactor(), d2.getFactor());
			}
			return groupComparison;
		});

		return possibleMeasures;
	}

	protected void checkMeasuresConsistency(MeasureUnit... units) {
		BaseMeasureUnit group = null;
		for (MeasureUnit unit : units) {
			if (unit == null) {
				continue;
			}
			if (group == null) {
				group = unit.getGroup();
			}
			if (!Objects.equals(group, unit.getGroup())) {
				throw new SystemException("Passed measures inconsistent");
			}
		}
	}

	protected abstract void updateValue();

	protected EntityConverter getEntityConverter() {
		return converter;
	}

	// ****************************************************************************************************************

	@Override
	public Measurable getValue() {
		return (Measurable) super.getValue();
	}

	public boolean canSelectMeasure() {
		List<MeasureUnit> possibleMeasures = getPossibleMeasures();
		return possibleMeasures != null && !possibleMeasures.isEmpty();
	}

	public boolean rawMeasureChanged(MeasureUnit rawMeasure) {
		return !Objects.equals(rawMeasure, getRawMeasure());
	}

	public MeasureUnit getRawMeasure() {
		return getPrivateState(PropertyKeys.rawMeasure, MeasureUnit.class);
	}

	public void setRawMeasure(MeasureUnit rawMeasure) {
		setPrivateState(PropertyKeys.rawMeasure, rawMeasure);
		updateValue();
	}

	public boolean defaultMeasureChanged(MeasureUnit defaultMeasure) {
		return !Objects.equals(defaultMeasure, getDefaultMeasure());
	}

	public MeasureUnit getDefaultMeasure() {
		return getPrivateState(PropertyKeys.defaultMeasure, MeasureUnit.class);
	}

	public void setDefaultMeasure(MeasureUnit rawMeasure) {
		setPrivateState(PropertyKeys.defaultMeasure, rawMeasure);
	}

	public boolean baseMeasureChanged(BaseMeasureUnit baseMeasure) {
		return !Objects.equals(baseMeasure, getBaseMeasure());
	}

	public BaseMeasureUnit getBaseMeasure() {
		return getPrivateState(PropertyKeys.baseMeasure, BaseMeasureUnit.class);
	}

	public void setBaseMeasure(BaseMeasureUnit baseMeasure) {
		setPrivateState(PropertyKeys.baseMeasure, baseMeasure);
	}

	@SuppressWarnings("unchecked")
	public List<MeasureUnit> getPossibleMeasures() {
		return getPrivateState(PropertyKeys.possibleMeasures, List.class);
	}

	public void setPossibleMeasures(List<MeasureUnit> possibleMeasures) {
		setPrivateState(PropertyKeys.possibleMeasures, possibleMeasures);
	}

	static enum AttributeKeys {
		defaultMeasure, baseMeasure
	}

	static enum PropertyKeys {
		rawMeasure, defaultMeasure, baseMeasure, possibleMeasures
	}
}
