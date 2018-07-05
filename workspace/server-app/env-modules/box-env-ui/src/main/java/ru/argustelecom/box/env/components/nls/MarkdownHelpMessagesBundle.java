package ru.argustelecom.box.env.components.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface MarkdownHelpMessagesBundle {

	@Message(value = "Вариант 1")
	String case1();

	@Message(value = "Вариант 2")
	String case2();

	@Message(value = "Результат")
	String result();

	@Message(value = "Подсказка")
	String prompt();

	@Message(value = "*Курсив*")
	String iCase1();

	@Message(value = "_Курсив_")
	String iCase2();

	@Message(value = "**Жирный**")
	String bCase1();

	@Message(value = "__Жирный__")
	String bCase2();

	@Message(value = "# Заголовок 1")
	String h1Case1();

	@Message(value = "Заголовок 1\n" + "=========")
	String h1Case2();

	@Message(value = "## Заголовок 2")
	String h2Case1();

	@Message(value = "Заголовок 2\n" + "---------")
	String h2Case2();

	@Message(value = "[Ссылка](http://argustelecom.ru)")
	String hrefCase1();

	@Message(value = "[Ссылка][1]\n" + "\n" + "[1]: http://argustelecom.ru\"")
	String hrefCase2();

	@Message(value = "[Ссылка](http://argustelecom.ru)")
	String imgCase1();

	@Message(value = "![Изображение][1]\n" + "\n" + "[1]: http://url/1.jpg")
	String imgCase2();

	@Message(value = "> Блок цитаты")
	String q();

	@Message(value = "Абзац\n" + "\n" + "Новый абзац - после одной пустой строки")
	String p();

	@Message(value = "* Список\n" + "* Список\r\n" + "* Список")
	String ulCase1();

	@Message(value = "- Список\n" + "- Список\n" + "- Список")
	String ulCase2();

	@Message(value = "* 1 уровень\n" + "  * 2 уровень(2 лидирующих пробела)\n"
			+ "    * 3 уровень(4 лидирующих пробела)")
	String olTab();

	@Message(value = "1. Один\n" + "2. Два\n" + "3. Три")
	String olCase1();

	@Message(value = "1) Один\n" + "2) Два\n" + "3) Три")
	String olCase2();

	@Message(value = "Горизонтальная линия\n" + "\n" + "---")
	String hrCase1();

	@Message(value = "Горизонтальная линия\n" + "\n" + "***")
	String hrCase2();

}
