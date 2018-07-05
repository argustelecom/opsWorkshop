package ru.argustelecom.box.env.type;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.type.model.TypeProperty;
import ru.argustelecom.box.env.type.model.properties.DateIntervalProperty;
import ru.argustelecom.box.env.type.model.properties.DateProperty;
import ru.argustelecom.box.env.type.model.properties.DoubleProperty;
import ru.argustelecom.box.env.type.model.properties.LogicalProperty;
import ru.argustelecom.box.env.type.model.properties.LongProperty;
import ru.argustelecom.box.env.type.model.properties.LookupArrayProperty;
import ru.argustelecom.box.env.type.model.properties.LookupProperty;
import ru.argustelecom.box.env.type.model.properties.MeasuredIntervalProperty;
import ru.argustelecom.box.env.type.model.properties.MeasuredProperty;
import ru.argustelecom.box.env.type.model.properties.TextArrayProperty;
import ru.argustelecom.box.env.type.model.properties.TextProperty;

@Named("typePropertyTranslator")
@RequestScoped
public class TypePropertyTranslator implements Serializable {

	@Inject
	private TypeFactory typeFactory;

	public TypeProperty<?> translate(TypePropertyDto dto) {
		checkNotNull(dto);

		TypeProperty<?> newProperty;

		boolean hasKeyword = dto.getKeyword() != null;
		boolean hasGroup = dto.getGroup() != null;

		if (hasKeyword && hasGroup) {
			newProperty = typeFactory.createProperty(dto.getType(), dto.getGroup(),
					dto.getPropertyType().getPropertyClass(), dto.getKeyword());
		} else if (!hasKeyword && !hasGroup) {
			newProperty = typeFactory.createProperty(dto.getType(), dto.getPropertyType().getPropertyClass());
		} else if (hasKeyword) {
			newProperty = typeFactory.createProperty(dto.getType(), dto.getPropertyType().getPropertyClass(),
					dto.getKeyword());
		} else {
			newProperty = typeFactory.createProperty(dto.getType(), dto.getGroup(),
					dto.getPropertyType().getPropertyClass());
		}

		fill(dto, newProperty);
		return newProperty;
	}

	private void fill(TypePropertyDto dto, TypeProperty<?> newProperty) {
		newProperty.setName(dto.getName());
		newProperty.setHint(dto.getHint());
		newProperty.setRequired(dto.isRequired());
		newProperty.setSecured(dto.isSecured());
		newProperty.setFiltered(dto.isFiltered());
		newProperty.setIndexed(dto.isIndexed());
		if (dto.getOrdinalNumber() != null) {
			newProperty.changeOrdinalNumber(dto.getOrdinalNumber());
		}

		switch (dto.getPropertyType()) {
		case DATE:
			((DateProperty) newProperty).setPattern(dto.getDatePattern());
			((DateProperty) newProperty).setDefaultValue(dto.getDateDefaultValue());
			break;
		case DATE_INTERVAL:
			((DateIntervalProperty) newProperty).setPattern(dto.getDatePattern());
			((DateIntervalProperty) newProperty).setDefaultValue(dto.getDateIntervalDefaultValue());
			break;
		case MEASURED:
			((MeasuredProperty) newProperty).setMeasureUnit(dto.getMeasure());
			((MeasuredProperty) newProperty).setDefaultValue(dto.getDefaultMeasuredValue());
			break;
		case MEASURED_INTERVAL:
			((MeasuredIntervalProperty) newProperty).setMeasureUnit(dto.getMeasure());
			((MeasuredIntervalProperty) newProperty).setDefaultValue(dto.getDefaultMeasuredIntervalValue());
			break;
		case LOGICAL:
			((LogicalProperty) newProperty).setDefaultValue(dto.isLogicalDefaultValue());
			break;
		case DOUBLE:
			if (dto.isUnique()) {
				typeFactory.makePropertyUnique(dto.getType().getClass(), newProperty);
			} else {
				((DoubleProperty) newProperty).setDefaultValue(dto.getNumberDefaultValue());
			}
			((DoubleProperty) newProperty).setMinValue(dto.getNumberMinValue());
			((DoubleProperty) newProperty).setMaxValue(dto.getNumberMaxValue());
			((DoubleProperty) newProperty).setPrecision(dto.getNumberPrecision());
			break;
		case LONG:
			if (dto.isUnique()) {
				typeFactory.makePropertyUnique(dto.getType().getClass(), newProperty);
			} else {
				((LongProperty) newProperty).setDefaultValue(
						dto.getNumberDefaultValue() != null ? dto.getNumberDefaultValue().longValue() : null);
			}
			((LongProperty) newProperty)
					.setMinValue(dto.getNumberMinValue() != null ? dto.getNumberMinValue().longValue() : null);
			((LongProperty) newProperty)
					.setMaxValue(dto.getNumberMaxValue() != null ? dto.getNumberMaxValue().longValue() : null);
			break;
		case LOOKUP:
			((LookupProperty) newProperty).setCategory(dto.getLookupCategory());
			((LookupProperty) newProperty).setDefaultValue(dto.getLookupDefaultValue());
			break;
		case TEXT:
			if (dto.isUnique()) {
				typeFactory.makePropertyUnique(dto.getType().getClass(), newProperty);
			}
			((TextProperty) newProperty).setPattern(dto.getTextPattern());
			((TextProperty) newProperty).setDefaultValue(dto.getDefaultValueText());
			break;
		case TEXT_ARRAY:
			((TextArrayProperty) newProperty).setDefaultValue(dto.getDefaultValueTextArray());
			break;
		case LOOKUP_ARRAY:
			((LookupArrayProperty) newProperty).setCategory(dto.getLookupCategory());
			((LookupArrayProperty) newProperty).setDefaultValue(dto.getDefaultValueLookupArray());
		}
	}

	private static final long serialVersionUID = 5652418989547462172L;
}
