package ru.argustelecom.box.env.filter;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.hibernate.proxy.HibernateProxy;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.dto.IdentifiableDto;
import ru.argustelecom.box.env.filter.model.FilterParam;
import ru.argustelecom.box.env.util.ReflectionUtils;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.modelbase.Identifiable;
import ru.argustelecom.system.inf.modelbase.NamedObject;
import ru.argustelecom.system.inf.utils.CDIHelper;

@NoArgsConstructor
@AllArgsConstructor
public class FilterViewState implements Serializable {

	private static final String WELD_PROXY_INTERFACE_NAME = "org.jboss.weld.bean.proxy.ProxyObject";

	private BiConsumer<Field, Object> afterAssign;

	public Map<String, Object> getFilterMap() {
		List<Field> fields = ReflectionUtils.getFields(this.getClass());
		Map<String, Object> filterMap = new HashMap<>();
		for (Field field : fields) {
			FilterMapEntry anInst = field.getAnnotation(FilterMapEntry.class);
			if (anInst != null) {
				try {
					Object value = field.get(this);
					if (value != null) {
						filterMap.put(anInst.value(), value);
					}
				} catch (IllegalAccessException e) {
					throw new SystemException(e);
				}
			}
		}
		return filterMap;
	}

	public void clearParamsJSF() {
		clearParams();
	}

	public void clearParams(String... except) {
		List<Field> fields = ReflectionUtils.getFields(this.getClass());
		List<String> exceptList = Arrays.asList(except);
		//@formatter:off
		fields.stream()
				.filter(field -> field.getAnnotation(FilterMapEntry.class) != null
				&& !exceptList.contains(field.getAnnotation(FilterMapEntry.class).value()))
				.forEach(field -> {
					try {
						if (List.class.isAssignableFrom(field.getType())) {
							field.set(this, new ArrayList<>());
						} else field.set(this, null);					
					} catch (IllegalAccessException e) {
						throw new SystemException(e);
					}
				});
		//@formatter:on
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void applyFilterParams(Collection<FilterParam> filterParams) {
		clearParams();
		Map<String, Object> paramsAsMap = filterParams.stream()
				.collect(Collectors.toMap(FilterParam::getName, FilterParam::getValue));
		List<Field> fields = ReflectionUtils.getFields(this.getClass());
		fields.forEach(field -> {
			FilterMapEntry entry = field.getAnnotation(FilterMapEntry.class);
			if (entry != null && paramsAsMap.containsKey(entry.value())) {
				Object filterValue = paramsAsMap.get(entry.value());
				// TODO пофиксить, когда будут рефакторинг BusinessObjectDto
				if (entry.isBusinessObjectDto()) {
					filterValue = EntityManagerUtils.initializeAndUnproxy(filterValue);
					filterValue = CDIHelper.lookupCDIBean(BusinessObjectDtoTranslator.class)
							.translate((Identifiable & NamedObject) filterValue);
				}
				for (Class<? extends DefaultDtoTranslator> translatorClass : entry.translator()) {
					if (!DefaultDtoTranslator.class.getName().equals(translatorClass.getName())) {
						if (filterValue instanceof HibernateProxy) {
							filterValue = EntityManagerUtils.initializeAndUnproxy(filterValue);
						}
						DefaultDtoTranslator defaultDtoTr = CDIHelper.lookupCDIBean(translatorClass);
						Class genericParameterClass = ru.argustelecom.box.inf.utils.ReflectionUtils
								.getGenericParameterClass(unProxyClass(defaultDtoTr.getClass()),
										DefaultDtoTranslator.class, 1);
						if (genericParameterClass.isAssignableFrom(filterValue.getClass())) {
							filterValue = defaultDtoTr.translate((Identifiable) filterValue);
						}
					}
				}
				try {
					field.set(this, filterValue);
					if (afterAssign != null) {
						afterAssign.accept(field, filterValue);
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new SystemException(e);
				}
			}
		});
	}

	private Class<?> unProxyClass(Class<? extends DefaultDtoTranslator> clazz) {
		for (Class<?> implementedInterface : clazz.getInterfaces()) {
			if (implementedInterface.getName().equals(WELD_PROXY_INTERFACE_NAME)) {
				return clazz.getSuperclass();
			}
		}

		return clazz;
	}

	public Set<FilterParam> getAsFilterParams() {
		Set<FilterParam> filterParams = new HashSet<>();
		getFilterMap().forEach((key, value) -> {
			Object paramValue = value;
			if (value instanceof IdentifiableDto) {
				paramValue = ((IdentifiableDto) value).getIdentifiable();
				// TODO FIXME когда будет рефакторинг BusinessObjectDto
			} else if (value instanceof BusinessObjectDto) {
				paramValue = ((BusinessObjectDto) value).getIdentifiable();
			}
			filterParams.add(FilterParam.create(key, paramValue));
		});

		return Collections.unmodifiableSet(filterParams);
	}

	private static final long serialVersionUID = -8035900814158542628L;
}
