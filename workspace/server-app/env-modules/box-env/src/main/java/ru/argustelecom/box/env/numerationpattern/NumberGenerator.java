package ru.argustelecom.box.env.numerationpattern;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import javax.inject.Inject;

import com.google.common.base.Strings;

import ru.argustelecom.box.env.billing.bill.model.BillType;
import ru.argustelecom.box.env.contract.model.AbstractContractType;
import ru.argustelecom.box.env.numerationpattern.metamodel.NumerationPatternVariableService;
import ru.argustelecom.box.env.numerationpattern.metamodel.NumerationPatternVariableService.NumerationVariable;
import ru.argustelecom.box.env.numerationpattern.model.BillNumerationPattern;
import ru.argustelecom.box.env.numerationpattern.model.ContractNumerationPattern;
import ru.argustelecom.box.env.numerationpattern.model.NumerationPattern;
import ru.argustelecom.box.env.numerationpattern.nls.NumerationMessagesBundle;
import ru.argustelecom.box.env.numerationpattern.statement.Statement;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.system.inf.exception.BusinessException;

@ApplicationService
public class NumberGenerator {

	@Inject
	private NumerationPatternRepository numerationPatternRepository;

	@Inject
	private NumerationPatternVariableService numerationPatternVariableService;

	@Inject
	private NumerationSequenceGenerator numerationSequenceGenerator;

	public String generateNumber(Class<?> forClass, Object... varHolders) {
		return generateNumber(forClass, numerationPatternRepository.findByClass(forClass), varHolders);
	}

	// TODO сделать общий метод, который бы получал на вход экземпляр Type
	public String generateNumber(Class<?> forClass, BillType billType, Object... varHolders) {
		List<BillNumerationPattern> patterns = numerationPatternRepository.findByClassAndType(forClass, billType);

		Predicate<BillNumerationPattern> predicate = numerationPattern -> numerationPattern.getBillType() != null;
		NumerationPattern np = Optional.ofNullable(find(patterns, predicate))
				.orElse(find(patterns, predicate.negate()));
		return generateNumber(forClass, np, varHolders);
	}

	public String generateNumber(Class<?> forClass, AbstractContractType contractType, Object... varHolders) {
		List<ContractNumerationPattern> patterns = numerationPatternRepository.findByClassAndType(forClass,
				contractType);

		Predicate<ContractNumerationPattern> predicate = numerationPattern -> numerationPattern
				.getContractType() != null;
		NumerationPattern np = Optional.ofNullable(find(patterns, predicate))
				.orElse(find(patterns, predicate.negate()));
		return generateNumber(forClass, np, varHolders);
	}

	private <T extends NumerationPattern> NumerationPattern find(List<T> numerationPatterns, Predicate<T> predicate) {
		return numerationPatterns.stream().filter(predicate).findFirst().orElse(null);
	}

	private String generateNumber(Class<?> forClass, NumerationPattern numerationPattern, Object... varHolders) {
		if (numerationPattern == null || Strings.isNullOrEmpty(numerationPattern.getPattern())) {
			NumerationMessagesBundle messages = LocaleUtils.getMessages(NumerationMessagesBundle.class);
			throw new BusinessException(messages.numerationPatternIsNotSpecified());
		}

		List<Statement> statements = numerationPattern.getStatements();

		Map<NumerationVariable, Object> variableContext = numerationPatternVariableService
				.createVariableContext(forClass, varHolders);

		return generate(variableContext, statements, false);
	}

	public String generatePreviewNumber(String forClass, List<Statement> statements) {
		Map<NumerationVariable, Object> defaultVariableContext = numerationPatternVariableService
				.createDefaultContext(forClass);

		return generate(defaultVariableContext, statements, true);
	}

	private String generate(Map<NumerationVariable, Object> variableContext, List<Statement> statements,
			boolean isPreview) {
		StringBuilder builder = new StringBuilder();
		Map<String, String> seqValues = new HashMap<>();
		NumerationMessagesBundle messages = LocaleUtils.getMessages(NumerationMessagesBundle.class);

		for (Statement statement : statements) {
			switch (statement.getType()) {
			case LITERAL:
				builder.append(statement.getValue());
				break;
			case SEQUENCE:
				if (isPreview) {
					builder.append(numerationSequenceGenerator.getInitialValue(statement.getValue()));
				} else {
					// если в маске используется одна последовательность несколько раз
					if (!seqValues.containsKey(statement.getValue())) {
						seqValues.put(statement.getValue(),
								numerationSequenceGenerator.getNextNumber(statement.getValue()));
					}
					builder.append(seqValues.get(statement.getValue()));
				}
				break;
			case VARIABLE:
				builder.append(getVariableValue(variableContext, statement));
				break;
			default:
				throw new BusinessException(messages.unsupportedInstruction(statement.getValue()));
			}
		}
		return builder.toString();
	}

	private Object getVariableValue(Map<NumerationVariable, Object> variableContext, Statement statement) {
		String variableName = NumerationPatternUtils.getVariableName(statement.getValue());
		String variableFormat = NumerationPatternUtils.getVariableFormat(statement.getValue());
		NumerationVariable numerationVariable = findNumerationVariableByAlias(variableContext, variableName);

		Object value;
		if (variableFormat != null) {
			value = numerationPatternVariableService.getReturnTypePatternFormatters()
					.get(numerationVariable.getReturnType()).getCallback()
					.applyFormat(variableFormat, variableContext.get(numerationVariable));
		} else {
			value = variableContext.get(numerationVariable);
		}
		return value;
	}

	private NumerationVariable findNumerationVariableByAlias(Map<NumerationVariable, Object> numerationVariables,
			String variableName) {
		//@formatter:off
		return numerationVariables.keySet().stream()
				.filter(numerationVariable -> numerationVariable.getAlias().equals(variableName))
				.findFirst()
				.orElse(null);
		//@formatter:on
	}
}
