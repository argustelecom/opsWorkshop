package ru.argustelecom.box.env.numerationpattern.metamodel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Synchronized;
import ru.argustelecom.system.inf.exception.SystemException;

@ApplicationScoped
public class NumerationPatternVariableService {

	private List<NumerationVariable> numerationVariables = new ArrayList<>();
	private Map<Class<?>, NumerationPatternFormatter> returnTypePatternFormatters;
	private List<Class<?>> validReturnTypes;

	{
		initValidReturnTypes();
		initReturnTypePatterns();
	}

	@Synchronized
	public <T, V> void add(String forClass, String alias, Class<T> target, Class<V> returnType,
			NumerationPatternVariableCallback<T, V> callback, V defaultValue) {
		if (!validReturnTypes.contains(returnType)) {
			throw new SystemException("Возвращаемый тип для метода не разрешен");
		}
		numerationVariables.add(new NumerationVariable<>(forClass, alias, target, returnType, callback, defaultValue));
	}

	public List<NumerationVariable> availableAliases(String forClass) {
		//@formatter:off
		return numerationVariables.stream()
				.filter(v ->  v.getForClass() == null || v.getForClass().equals(forClass))
				.collect(Collectors.toList());
		//@formatter:on
	}

	public Map<Class<?>, NumerationPatternFormatter> getReturnTypePatternFormatters() {
		return returnTypePatternFormatters;
	}

	@SuppressWarnings("unchecked")
	public Map<NumerationVariable, Object> createVariableContext(Class<?> forClass, Object... variableHolders) {
		Map<NumerationVariable, Object> variableContext = new HashMap<>();
		for (NumerationVariable numerationVariable : numerationVariables) {
			String forClassName = numerationVariable.getForClass();
			Class<?> variableHolderClass = numerationVariable.getVariableHolder();
			for (Object variableHolder : variableHolders) {
				if ((forClassName != null && forClassName.equals(forClass.getName()))
						&& (variableHolderClass != null && variableHolderClass.equals(variableHolder.getClass()))) {
					variableContext.put(numerationVariable, numerationVariable.getCallback().get(variableHolder));
				}
			}
			if (forClassName == null && variableHolderClass == null) {
				variableContext.put(numerationVariable, numerationVariable.getCallback().get(null));
			}
		}
		return variableContext;
	}

	public Map<NumerationVariable, Object> createDefaultContext(String forClass) {
		Map<NumerationVariable, Object> defaultVariableContext = new HashMap<>();
		for (NumerationVariable numerationVariable : numerationVariables) {
			String variableForClass = numerationVariable.getForClass();
			if (variableForClass == null || variableForClass.equals(forClass)) {
				defaultVariableContext.put(numerationVariable, numerationVariable.getDefaultValue());
			}
		}
		return defaultVariableContext;
	}

	private void initValidReturnTypes() {
		//@formatter:off

		validReturnTypes = Arrays.asList(
				Integer.class,
				Long.class,
				Float.class,
				Double.class,
				int.class,
				long.class,
				float.class,
				double.class,
				String.class,
				Date.class
		);

		//@formatter:on
	}

	private void initReturnTypePatterns() {
		Map<Class<?>, NumerationPatternFormatter> patternFormatterMap = new HashMap<>();

		//@formatter:off

		patternFormatterMap.put(Date.class, new NumerationPatternFormatter(
				Pattern.compile("(?:((yyyy|yy)|MM|dd)(?!.*\\1)){1,3}+"),
				true,
				(format, obj) -> new SimpleDateFormat(format).format(obj)));

		//@formatter:on

		returnTypePatternFormatters = Collections.unmodifiableMap(patternFormatterMap);
	}

	@Getter
	@AllArgsConstructor
	public static class NumerationVariable<T, V> {
		private String forClass;
		private String alias;
		private Class<T> variableHolder;
		private Class<V> returnType;
		private NumerationPatternVariableCallback<T, V> callback;
		private V defaultValue;
	}

	@Getter
	@AllArgsConstructor
	public static class NumerationPatternFormatter {
		private Pattern pattern;
		private boolean isFormatMandatory;
		private NumerationPatternFormatCallback callback;
	}
}