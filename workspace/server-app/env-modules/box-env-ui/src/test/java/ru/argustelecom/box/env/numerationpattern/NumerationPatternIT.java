package ru.argustelecom.box.env.numerationpattern;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;
import ru.argustelecom.box.env.numerationpattern.testdata.NumerationSequenceDeleteProvider;
import ru.argustelecom.box.env.stl.period.Period;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Row;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.DataProvider;

import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;

public class NumerationPatternIT extends AbstractWebUITest {

    /**
     * Сценарий id = C269166 Создание последовательности
     * Предварительные условия: Открыта вкладка "Последовательности номеров".
     * Сценарий:
     * <ol>
     * <li>Нажать кнопку "Создать последовательность номеров".
     * <li>В диалоге ввести: Название
     * <li>В диалоге ввести: Начальное значение
     * <li>В диалоге ввести: Шаг
     * <li>В диалоге ввести: Период
     * <li>В диалоге ввести: Разрядность
     * <li>Нажать кнопку "Создать".
     * <li>Проверить, что создана последовательность.
     * <p>
     * Исполнитель: [v.sysoev]
     */
    @Test
    public void shouldCreateNumerationSequence(@InitialPage NumerationSequencePage page) {
        String name = "TestSequence" + Math.abs(new Random().nextInt());
        String initialValue = "5";
        String increment = "3";
        String period = PeriodUnit.MONTH.getName();
        String capacity = "5";

        page.openCreateDialog.click();
        page.name.input(name);
        page.initialValue.input(initialValue);
        page.increment.input(increment);
        page.periods.select(period);
        page.capacity.input(capacity);
        page.create.click();

        Row newSequence = page.createdSequences.findRow(name);

        assertEquals(name, newSequence.getCell(0).getTextString());
        assertEquals(initialValue, newSequence.getCell(1).getTextString());
        assertEquals(increment, newSequence.getCell(2).getTextString());
        assertEquals(period, newSequence.getCell(3).getTextString());
        assertEquals(capacity, newSequence.getCell(4).getTextString());
    }

    /**
     * Сценарий id = C269168 Удаление последовательности
     * Предварительные условия: Открыта вкладка "Последовательности номеров".
     * Сценарий:
     * <ol>
     * <li>В строке с последовательностью, которую требуется удалить, нажать кнопку "Удалить последовательность".
     * <li>Проверить, что удалена последовательность.
     * <p>
     * Исполнитель: [v.sysoev]
     */
    @Test(expected = NoSuchElementException.class)
    public void shouldDeleteNumerationSequence(
            @InitialPage NumerationSequencePage page,
            @DataProvider(
                    providerClass = NumerationSequenceDeleteProvider.class,
                    contextPropertyName = NumerationSequenceDeleteProvider.SEQUENCE_NAME
            ) String sequenceName
    ) {
        page.deleteSequence(sequenceName);
        page.createdSequences.findRow(sequenceName);
    }
}