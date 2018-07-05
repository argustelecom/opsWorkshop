package ru.argustelecom.box.env.numerationpattern.parser;

import static org.mockito.Mockito.when;
import static ru.argustelecom.box.env.numerationpattern.metamodel.NumerationPatternVariableService.NumerationPatternFormatter;
import static ru.argustelecom.box.env.numerationpattern.metamodel.NumerationPatternVariableService.NumerationVariable;
import static ru.argustelecom.box.env.numerationpattern.parser.NumerationPatternLexeme.LexemeType;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.contract.model.ContractExtension;
import ru.argustelecom.box.env.numerationpattern.NumerationSequenceRepository;
import ru.argustelecom.box.env.numerationpattern.metamodel.NumerationPatternVariableService;
import ru.argustelecom.system.inf.exception.BusinessException;

@RunWith(MockitoJUnitRunner.class)
public class NumerationPatternValidatorTest {
	@Mock
	private NumerationPatternVariableService numerationPatternVariableService;

	@Mock
	private NumerationSequenceRepository numerationSequenceRepository;

	@InjectMocks
	private NumerationPatternValidator validator;

	@Before
	public void init() {
		//@formatter:off
		when(numerationPatternVariableService.availableAliases(ContractExtension.class.getName()))
				.thenReturn(Arrays.asList(
						new NumerationVariable<>(
								ContractExtension.class.getName(),
								"ContractNum",
								Contract.class,
								String.class,
								Contract::getDocumentNumber,
								String.format("Дог-%d", new Random().nextInt(99999))),
						new NumerationVariable<>(
								ContractExtension.class.getName(),
								"ContractExtNum",
								Contract.class,
								Long.class,
								Contract::nextExtensionNumber,
								(long) new Random().nextInt()),

						//системная дата определяется для всех классов
						new NumerationVariable<>(
								null,
								"SysDate",
								null,
								Date.class,
								instance -> new Date(),
								new Date()
						)));
		//@formatter:on

		Map<Class<?>, NumerationPatternFormatter> patternFormatterMap = new HashMap<>();
		patternFormatterMap.put(Date.class,
				new NumerationPatternFormatter(Pattern.compile("(?:((yyyy|yy)|MM|dd)(?!.*\\1)){1,3}+"),
						true,
						(format, obj) -> new SimpleDateFormat(format).format(obj)));

		when(numerationPatternVariableService.getReturnTypePatternFormatters()).thenReturn(patternFormatterMap);

		when(numerationSequenceRepository.getSequenceNames()).thenReturn(Arrays.asList("A", "B"));
	}

	@Test
	public void testValidateSuccess() {
		validator.validate(ContractExtension.class.getName(),
				Arrays.asList(new NumerationPatternLexeme(LexemeType.SEQUENCE, "A"),
						new NumerationPatternLexeme(LexemeType.SEQUENCE, "B"),
						new NumerationPatternLexeme(LexemeType.VARIABLE, "ContractNum"),
						new NumerationPatternLexeme(LexemeType.VARIABLE, "ContractExtNum"),
						new NumerationPatternLexeme(LexemeType.VARIABLE, "SysDate:yyyyMMdd")));
	}

	@Test(expected = BusinessException.class)
	public void testSequenceDoesNotExist() {
		validator.validate(ContractExtension.class.getName(), Arrays.asList(new NumerationPatternLexeme(LexemeType.SEQUENCE, "C")));
	}

	@Test(expected = BusinessException.class)
	public void testVariableDoesNotExist() {
		validator.validate(ContractExtension.class.getName(),
				Arrays.asList(new NumerationPatternLexeme(LexemeType.VARIABLE, "Variable")));
	}

	@Test(expected = BusinessException.class)
	public void testVariableFormatNotSupported() {
		validator.validate(ContractExtension.class.getName(),
				Arrays.asList(new NumerationPatternLexeme(LexemeType.VARIABLE, "ContractNum:ABC")));
	}

	@Test(expected = BusinessException.class)
	public void testVariableInvalidFormat() {
		validator.validate(ContractExtension.class.getName(),
				Arrays.asList(new NumerationPatternLexeme(LexemeType.VARIABLE, "SysDate:ABC")));
	}

}