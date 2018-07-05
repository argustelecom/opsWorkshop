package ru.argustelecom.box.env.type.model;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static ru.argustelecom.box.env.type.model.TypeCreationalContext.creationalContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import ru.argustelecom.box.env.measure.model.BaseMeasureUnit;
import ru.argustelecom.box.env.measure.model.MeasureUnit;
import ru.argustelecom.box.env.type.model.lookup.LookupCategory;
import ru.argustelecom.box.env.type.model.lookup.LookupEntry;
import ru.argustelecom.box.env.type.model.properties.DateIntervalProperty;
import ru.argustelecom.box.env.type.model.properties.DateProperty;
import ru.argustelecom.box.env.type.model.properties.DoubleProperty;
import ru.argustelecom.box.env.type.model.properties.LogicalProperty;
import ru.argustelecom.box.env.type.model.properties.LongProperty;
import ru.argustelecom.box.env.type.model.properties.LookupProperty;
import ru.argustelecom.box.env.type.model.properties.MeasuredIntervalProperty;
import ru.argustelecom.box.env.type.model.properties.MeasuredProperty;
import ru.argustelecom.box.env.type.model.properties.TextProperty;
import ru.argustelecom.system.inf.modelbase.Identifiable;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractEavTest {

	private static AtomicLong typeCounter = new AtomicLong(1L);
	private static AtomicLong typeInstanceCounter = new AtomicLong(1L);
	private static TypeCreationalContext<TestType> ctx = creationalContext(TestType.class);

	protected EntityConverterStub ec;

	protected TestType type;
	protected TestTypeInstance instance;

	protected DateProperty dateProperty;
	protected DateIntervalProperty dateIntervalProperty;
	protected DoubleProperty doubleProperty;
	protected LongProperty longProperty;
	protected LogicalProperty logicalProperty;
	protected LookupProperty lookupProperty;
	protected MeasuredProperty measuredProperty;
	protected MeasuredIntervalProperty measuredIntervalProperty;
	protected TextProperty textProperty;

	protected LookupEntry entry1;
	protected LookupEntry entry2;
	protected MeasureUnit measureUnit;

	@Before
	public void setup() {
		ec = new EntityConverterStub();

		measureUnit = createMeasureUnit(1L, "Тестовые единицы", "Т.Е.");

		LookupCategory category = createLookupCategory(10L, "Тестовая категория");
		entry1 = createLookupEntry(category, 100L, "Тестовое значение 1");
		entry2 = createLookupEntry(category, 200L, "Тестовое значение 2");

		doReturn(Arrays.asList(entry1, entry2)).when(category).getPossibleValues(Mockito.any());

		// when(category.getPossibleValues(Mockito.any())).thenReturn(Arrays.asList(entry1, entry2));

		type = createType();
		instance = createInstance(type);

		dateProperty = createProperty(10L, type, DateProperty.class);
		dateIntervalProperty = createProperty(20L, type, DateIntervalProperty.class);
		doubleProperty = createProperty(30L, type, DoubleProperty.class);
		longProperty = createProperty(40L, type, LongProperty.class);
		logicalProperty = createProperty(50L, type, LogicalProperty.class);
		textProperty = createProperty(60L, type, TextProperty.class);

		lookupProperty = createProperty(70L, type, LookupProperty.class);
		lookupProperty.setCategory(category);

		measuredProperty = createProperty(80L, type, MeasuredProperty.class);
		measuredProperty.setMeasureUnit(measureUnit);

		measuredIntervalProperty = createProperty(90L, type, MeasuredIntervalProperty.class);
		measuredIntervalProperty.setMeasureUnit(measureUnit);
	}

	@After
	public void cleanup() {
	}

	protected TestType createType() {
		TestType result = spy(ctx.createType(typeCounter.getAndIncrement()));
		addToConverter(result, "TestType-");
		return result;
	}

	protected TestTypeInstance createInstance(TestType instanceType) {
		TestTypeInstance result = spy(
				ctx.createInstance(instanceType, TestTypeInstance.class, typeInstanceCounter.getAndIncrement()));

		addToConverter(result, "TestTypeInstance-");
		return result;
	}

	protected <P extends TypeProperty<?>> P createProperty(Long id, TestType propertyOwner, Class<P> propertyClass) {
		String propertyKeyword = "prop" + id.toString();
		P result = spy(ctx.createProperty(propertyOwner, propertyClass, propertyKeyword, id));
		addToConverter(result, propertyClass.getSimpleName() + "-");
		doReturn(ec).when(result).getEntityConverter();
		// when(result.getEntityConverter()).thenReturn(ec);
		return result;
	}

	protected LookupCategory createLookupCategory(long categoryId, String categoryName) {
		LookupCategory category = spy(new LookupCategory(categoryId));
		category.setObjectName(categoryName);
		addToConverter(category, "LookuptCategory-");
		return category;
	}

	protected LookupEntry createLookupEntry(LookupCategory category, long entryId, String entryName) {
		LookupEntry entry = spy(new LookupEntry(entryId));
		entry.setCategory(category);
		entry.setObjectName(entryName);
		addToConverter(entry, "LookupEntry-");
		return entry;
	}

	protected MeasureUnit createMeasureUnit(long unitId, String unitName, String unitSymbol) {
		BaseMeasureUnit measureUnit = spy(new BaseMeasureUnit(unitId));
		measureUnit.setName(unitName);
		measureUnit.setGroupName(unitName);
		measureUnit.setSymbol(unitSymbol);
		addToConverter(measureUnit, "MeasureUnit-");
		return measureUnit;
	}

	protected void addToConverter(Identifiable entity, String prefix) {
		ec.put(prefix + entity.getId().toString(), entity);
	}

	public static class TestType extends Type {

		protected TestType() {
		}

		protected TestType(Long id) {
			super(id);
		}

	}

	public static class TestTypeInstance extends TypeInstance<TestType> {

		protected TestTypeInstance() {
		}

		protected TestTypeInstance(Long id) {
			super(id);
		}
	}

	public static class EntityConverterStub extends EntityConverter {

		private Map<String, Object> refToObj = new HashMap<>();
		private Map<Object, String> objToRef = new HashMap<>();

		public void put(String ref, Object obj) {
			refToObj.put(ref, obj);
			objToRef.put(obj, ref);
		}

		@Override
		protected Object doConvertToObject(String value) throws Exception {
			return refToObj.get(value);
		}

		@Override
		protected String doConvertToString(Object value) throws Exception {
			return objToRef.get(value);
		}
	}

}
