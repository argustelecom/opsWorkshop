package ru.argustelecom.box.env.type;

import java.util.Date;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.datetime.model.DateIntervalValue;
import ru.argustelecom.box.env.measure.model.MeasureUnit;
import ru.argustelecom.box.env.measure.model.MeasuredIntervalValue;
import ru.argustelecom.box.env.measure.model.MeasuredValue;
import ru.argustelecom.box.env.type.model.Type;
import ru.argustelecom.box.env.type.model.TypePropertyGroup;
import ru.argustelecom.box.env.type.model.TypePropertyRef;
import ru.argustelecom.box.env.type.model.lookup.LookupCategory;
import ru.argustelecom.box.env.type.model.lookup.LookupEntry;
import ru.argustelecom.system.inf.chrono.DateUtils;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class TypePropertyDto {
	private Long id;
	private String keyword;
	private String name;
	private String hint;
	private Integer ordinalNumber;
	private TypePropertyGroup group;
	private TypePropertyRef propertyType;
	private Type type;

	private boolean required;
	private boolean secured;
	private boolean indexed;
	private boolean filtered;
	private boolean unique;

	private String datePattern = DateUtils.DATETIME_WITH_SECONDS_PATTERN;
	private Date dateDefaultValue;
	private DateIntervalValue dateIntervalDefaultValue;

	private MeasureUnit measure;
	private MeasuredValue defaultMeasuredValue;
	private MeasuredIntervalValue defaultMeasuredIntervalValue;

	private boolean logicalDefaultValue;

	private Double numberDefaultValue;
	private Double numberMinValue;
	private Double numberMaxValue;
	private int numberPrecision;

	private LookupCategory lookupCategory;
	private LookupEntry lookupDefaultValue;

	private String defaultValueText;
	private String textPattern;

	private List<String> defaultValueTextArray;

	private List<LookupEntry> defaultValueLookupArray;

	private int maxOrdinalNumber;

	public void setLookupCategory(LookupCategory lookupCategory) {
		this.lookupCategory = lookupCategory;
		lookupDefaultValue = null;
	}
}
