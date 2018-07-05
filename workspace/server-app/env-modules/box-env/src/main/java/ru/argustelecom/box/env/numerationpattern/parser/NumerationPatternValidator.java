package ru.argustelecom.box.env.numerationpattern.parser;

import static ru.argustelecom.box.env.numerationpattern.NumerationPatternUtils.getVariableFormat;
import static ru.argustelecom.box.env.numerationpattern.NumerationPatternUtils.getVariableName;
import static ru.argustelecom.box.env.numerationpattern.metamodel.NumerationPatternVariableService.NumerationPatternFormatter;
import static ru.argustelecom.box.env.numerationpattern.metamodel.NumerationPatternVariableService.NumerationVariable;

import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;

import ru.argustelecom.box.env.numerationpattern.metamodel.NumerationPatternVariableService;
import ru.argustelecom.box.env.numerationpattern.NumerationSequenceRepository;
import ru.argustelecom.box.env.numerationpattern.nls.NumerationMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.system.inf.exception.BusinessException;

@ApplicationService
public class NumerationPatternValidator {

	@Inject
	private NumerationSequenceRepository numerationSequenceRepository;

	@Inject
	private NumerationPatternVariableService numerationPatternVariableService;

	public void validate(String forClass, List<NumerationPatternLexeme> lexemes) {
		List<NumerationVariable> availableAliases = numerationPatternVariableService.availableAliases(forClass);
		List<String> numerationSequenceNames = numerationSequenceRepository.getSequenceNames();
		NumerationMessagesBundle messages = LocaleUtils.getMessages(NumerationMessagesBundle.class);

		for (NumerationPatternLexeme lexeme : lexemes) {
			switch (lexeme.getType()) {
			case LITERAL:
				break;
			case SEQUENCE:
				if (!numerationSequenceNames.contains(lexeme.getValue())) {
					throw new BusinessException(messages.sequenceNotExist(lexeme.getValue()));
				}
				break;
			case VARIABLE: {
				String variableName = getVariableName(lexeme);

				//@formatter:off
				NumerationVariable numerationVariable = availableAliases.stream()
						.filter(v -> v.getAlias().equals(variableName))
						.findFirst()
						.orElse(null);
				//@formatter:on

				if (numerationVariable == null) {
					throw new BusinessException(messages.variableNotExist(lexeme.getValue()));
				}

				String variableFormat = getVariableFormat(lexeme);
				NumerationPatternFormatter numerationPatternFormatter = numerationPatternVariableService
						.getReturnTypePatternFormatters().get(numerationVariable.getReturnType());
				if (variableFormat != null) {
					Pattern pattern;
					if (numerationPatternFormatter == null
							|| (pattern = numerationPatternFormatter.getPattern()) == null) {
						throw new BusinessException(messages.variableIsNotAvailable(variableName));
					}
					if (!pattern.matcher(variableFormat).matches()) {
						throw new BusinessException(messages.patternIsNotApplicable(variableName, variableFormat));
					}
				} else if (numerationPatternFormatter != null && numerationPatternFormatter.isFormatMandatory()) {
					throw new BusinessException(messages.patternIsNotSpecified(variableName));
				}
				break;
			}
			default:
				throw new BusinessException(messages.unsupportedLexemeType(lexeme.getType().getName()));
			}
		}
	}
}
