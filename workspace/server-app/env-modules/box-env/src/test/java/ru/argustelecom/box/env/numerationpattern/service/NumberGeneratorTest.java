package ru.argustelecom.box.env.numerationpattern.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static ru.argustelecom.box.env.type.model.TypeCreationalContext.creationalContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.common.collect.Lists;

import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.contract.model.ContractExtension;
import ru.argustelecom.box.env.contract.model.ContractExtensionType;
import ru.argustelecom.box.env.numerationpattern.NumberGenerator;
import ru.argustelecom.box.env.numerationpattern.NumerationPatternRepository;
import ru.argustelecom.box.env.numerationpattern.NumerationSequenceGenerator;
import ru.argustelecom.box.env.numerationpattern.metamodel.NumerationPatternVariableService;
import ru.argustelecom.box.env.numerationpattern.metamodel.NumerationPatternVariableService.NumerationPatternFormatter;
import ru.argustelecom.box.env.numerationpattern.metamodel.NumerationPatternVariableService.NumerationVariable;
import ru.argustelecom.box.env.numerationpattern.model.ContractNumerationPattern;
import ru.argustelecom.system.inf.utils.ReflectionUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest(NumberGenerator.class)
public class NumberGeneratorTest {
	@Mock
	private NumerationPatternRepository numerationPatternRepository;

	@Mock
	private NumerationPatternVariableService numerationPatternVariableService;

	@Mock
	private NumerationSequenceGenerator numerationSequenceGenerator;

	@InjectMocks
	private NumberGenerator numberGenerator;

	@Test
	public void testGenerateNumber() throws Exception {
		ContractNumerationPattern contractNumerationPattern = new ContractNumerationPattern(1L);
		//@formatter:off
		contractNumerationPattern
				.setStatements(
						"addLit ДЗ-\n" +
						"addSeq A\n" +
						"addLit :\n" +
						"addSeq A\n" +
						"addLit -\n" +
						"addVar ContractNum\n" +
						"addLit -\n" +
						"addVar ContractExtNum\n" +
						"addLit /\n" +
						"addVar SysDate:yyyyMMdd");
		//@formatter:on
		contractNumerationPattern.setPattern("ДЗ-<A>:<A>-{ContractNum}-{ContractExtNum}/{SysDate:yyyyMMdd}");

		Contract contract = ReflectionUtils.newInstance(Contract.class);
		contract.setDocumentNumber("ABC");

		List<ContractNumerationPattern> resultList = Lists.newArrayList(contractNumerationPattern);
		when(numerationPatternRepository.findByClassAndType(ContractExtension.class,
				creationalContext(ContractExtensionType.class).createType(1L))).thenReturn(resultList);

		NumberGenerator spy = PowerMockito.spy(numberGenerator);
		Predicate<ContractNumerationPattern> predicate = numerationPattern -> numerationPattern
				.getContractType() != null;

		PowerMockito.doReturn(contractNumerationPattern).when(spy, "find", resultList, predicate);
		//@formatter:off
		Map<NumerationVariable, Object> contextVar = new HashMap<>();
		contextVar.put(new NumerationVariable<>(
				ContractExtension.class.getName(),
				"ContractNum",
				Contract.class,
				String.class,
				Contract::getDocumentNumber,
				String.format("Дог-%d", new Random().nextInt(99999))), "ABC");
		contextVar.put(new NumerationVariable<>(
				ContractExtension.class.getName(),
				"ContractExtNum",
				Contract.class,
				Long.class,
				Contract::nextExtensionNumber,
				(long) new Random().nextInt()), 1L);
		contextVar.put(new NumerationVariable<>(
				null,
				"SysDate",
				null,
				Date.class,
				instance -> new Date(),
				new Date()), new Date());
		//@formatter:on
		when(numerationPatternVariableService.createVariableContext(ContractExtension.class, contract))
				.thenReturn(contextVar);

		when(numerationSequenceGenerator.getNextNumber("A")).thenReturn("1");

		Map<Class<?>, NumerationPatternFormatter> patternFormatterMap = new HashMap<>();

		//@formatter:off

		patternFormatterMap.put(Date.class, new NumerationPatternFormatter(
				Pattern.compile("(?:((yyyy|yy)|dd|MM)(?!.*\\1)){1,3}+"),
				true,
				(format, obj) -> new SimpleDateFormat(format).format(obj)));

		//@formatter:on

		when(numerationPatternVariableService.getReturnTypePatternFormatters()).thenReturn(patternFormatterMap);

		assertEquals("ДЗ-1:1-ABC-1/" + new SimpleDateFormat("yyyyMMdd").format(new Date()),
				numberGenerator.generateNumber(ContractExtension.class,
						creationalContext(ContractExtensionType.class).createType(1L), contract));
	}

}