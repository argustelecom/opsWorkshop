package ru.argustelecom.box.env.numerationpattern.parser;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import ru.argustelecom.box.env.numerationpattern.statement.Statement;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class NumerationPatternParser {

	@Inject
	private NumerationPatternValidator validator;

	public List<Statement> parse(String forClass, String stringToParse) {
		List<NumerationPatternLexeme> lexemes = NumerationPatternLexer.scan(stringToParse);
		validator.validate(forClass, lexemes);
		//@formatter:off
		return lexemes.stream()
				.map(lexeme -> new Statement(lexeme.getValue(), lexeme.getType().getStatementType()))
				.collect(Collectors.toList());
		//@formatter:on
	}
}
