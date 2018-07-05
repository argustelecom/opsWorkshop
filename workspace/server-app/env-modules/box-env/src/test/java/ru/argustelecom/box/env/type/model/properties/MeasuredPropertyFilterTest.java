package ru.argustelecom.box.env.type.model.properties;

import ru.argustelecom.box.env.measure.model.MeasuredValue;
import ru.argustelecom.box.env.type.model.filter.AbstractSingleNumericValuePropertyFilterTest;

public class MeasuredPropertyFilterTest
		extends AbstractSingleNumericValuePropertyFilterTest<MeasuredValue, MeasuredProperty, MeasuredPropertyFilter> {

	@Override
	protected void init() {
		filter = new MeasuredPropertyFilter(measuredProperty);

		value1 = new MeasuredValue(100L, measureUnit);
		value2 = new MeasuredValue(300L, measureUnit);

		matchedValue1 = "100";
		matchedValue2 = "300";

		valueQName = "MeasuredProperty-80.storedValue";
		propertyQName = "MeasuredProperty-80";
	}

}
