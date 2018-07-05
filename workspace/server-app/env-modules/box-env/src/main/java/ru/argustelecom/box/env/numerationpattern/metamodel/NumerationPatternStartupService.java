package ru.argustelecom.box.env.numerationpattern.metamodel;

import java.util.Date;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.contract.model.ContractExtension;

/**
 * Сервис добавляет правила для нумерованых классов.
 * После разделения box-env на модули, в каждом созданном модуле должен быть свой startup сервис, добавляющий метаданные.
 */

@Startup
@Singleton
public class NumerationPatternStartupService {

	private static final int CONTRACT_NUM = 10000;
	private static final int CONTRACT_NUM_BOUND = 10;

	@Inject
	private NumerationPatternVariableService numerationPatternVariableService;

	@PostConstruct
	private void startup() {
		//@formatter:off
		numerationPatternVariableService.add(
				ContractExtension.class.getName(),
				"ContractNum",
				Contract.class,
				String.class,
				Contract::getDocumentNumber,
				String.format("Дог-%04d", new Random().nextInt(CONTRACT_NUM))
		);

		numerationPatternVariableService.add(
				ContractExtension.class.getName(),
				"ContractExtNum",
				Contract.class,
				Long.class,
				Contract::nextExtensionNumber,
				(long) new Random().nextInt(CONTRACT_NUM_BOUND)
		);

		//системная дата определяется для всех классов
		numerationPatternVariableService.add(
				null,
				"SysDate",
				null,
				Date.class,
				instance -> new Date(),
				new Date()
		);
		//@formatter:on
	}
}
