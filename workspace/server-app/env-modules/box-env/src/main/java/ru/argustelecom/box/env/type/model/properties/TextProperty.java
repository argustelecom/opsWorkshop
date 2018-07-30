package ru.argustelecom.box.env.type.model.properties;

import static com.google.common.base.Preconditions.checkState;

import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.argustelecom.box.env.stl.json.JsonHelper;
import ru.argustelecom.box.env.type.model.TypeInstance;
import ru.argustelecom.box.env.type.model.TypeProperty;
import ru.argustelecom.box.env.type.model.TypePropertyHolder;
import ru.argustelecom.box.env.type.nls.TypeMessagesBundle;
import ru.argustelecom.box.inf.modelbase.MetadataUnit;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.validation.ValidationResult;

@Entity
@Access(AccessType.FIELD)
public class TextProperty extends TypeProperty<String> {

	private static final long serialVersionUID = 8544219035931851058L;

	@Column(name = "txt_default", length = 1024)
	private String defaultValue;

	@Column(name = "txt_pattern", length = 512)
	private String pattern;

	@Column(name = "txt_lines")
	private int linesCount;

	@Transient
	private Pattern compiledPattern;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected TextProperty() {
	}

	/**
	 * Конструктор предназначен для инстанцирования свойства его холдером. Не делай этот конструктор публичным. Не делай
	 * других публичных конструкторов. Свойство должны инстанцироваться сугубо холдером или спецификацией (делегирует
	 * холдеру) для обеспецения корректного связывания холдера(спецификации) и свойства.
	 * 
	 * @param holder
	 *            - владелец свойства, часть спецификации
	 * @param id
	 *            - уникальный идентификатор свойства. Получается при помощи генератора инкапсулированного в
	 *            MetadataUnit.generateId()
	 * 
	 * @see TypePropertyHolder#createProperty(Class, String, Long)
	 * @see MetadataUnit#generateId()
	 * @see MetadataUnit#generateId(javax.persistence.EntityManager)
	 */
	protected TextProperty(TypePropertyHolder holder, Long id) {
		super(holder, id);
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		if (!Objects.equals(this.pattern, pattern)) {
			this.compiledPattern = compilePattern(pattern);
			this.pattern = pattern;
		}
	}

	protected Pattern getCompiledPattern() {
		if (compiledPattern == null) {
			compiledPattern = compilePattern(pattern);
		}
		return compiledPattern;
	}

	private Pattern compilePattern(String pattern) {
		try {
			return pattern != null ? Pattern.compile(pattern) : null;
		} catch (PatternSyntaxException e) {
			TypeMessagesBundle messages = LocaleUtils.getMessages(TypeMessagesBundle.class);
			throw messages.textValuePatternCompileException(pattern, e);
		}
	}

	public boolean isMultiline() {
		return linesCount > 1;
	}

	public int getLinesCount() {
		return linesCount;
	}

	public void setLinesCount(int linesCount) {
		this.linesCount = linesCount;
	}

	@Override
	public Class<?> getValueClass() {
		return String.class;
	}

	@Override
	public String getDefaultValue() {
		return defaultValue;
	}

	@Override
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = checkValue(defaultValue);
	}

	@Override
	public String getDefaultValueAsString() {
		return defaultValue;
	}

	@Override
	public ValidationResult<TypeProperty<String>> validateValue(String value) {
		ValidationResult<TypeProperty<String>> result = ValidationResult.success();
		Pattern actualPattern = getCompiledPattern();

		if (actualPattern != null && !actualPattern.matcher(value).matches()) {
			TypeMessagesBundle messages = LocaleUtils.getMessages(TypeMessagesBundle.class);
			result.error(this, messages.textValueMismatchWithPattern(getObjectName(), this.pattern));
		}
		return result;
	}

	@Override
	protected String extractValue(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName) {
		return extractValueAsString(context, propertiesRoot, qualifiedName);
	}

	@Override
	protected String extractValueAsString(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName) {
		return JsonHelper.STRING.get(propertiesRoot, qualifiedName);
	}

	@Override
	protected void putValue(TypeInstance<?> context, ObjectNode propertiesRoot, String qualifiedName, String value) {
		checkState(value != null);
		checkValue(value);
		JsonHelper.STRING.set(propertiesRoot, qualifiedName, value);
	}
}
