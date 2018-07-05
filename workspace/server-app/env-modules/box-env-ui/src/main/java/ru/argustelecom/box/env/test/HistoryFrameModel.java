package ru.argustelecom.box.env.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.PostConstruct;

import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class HistoryFrameModel implements Serializable {

	private static final long serialVersionUID = 1L;
	private Collection<HistoryEntry> history;

	@PostConstruct
	public void postConstruct() {
		history = new ArrayList<>();
		history.add(new HistoryEntry("19:00 25.07.2016",
				"Добавлен документ: Проект_Сбербанк_Индустриальный_АТС.docx Комментарий: проект готов, прикладываю. Отдаю на согласование",
				"Иванов И. И."));
		history.add(new HistoryEntry("13:00 21.07.2016",
				"Добавлены участники задачи: Генералов И. П. Комментарий: Ваня, не забывай подписывать меня на задачи, после подготовки проекта - отдай задачу мне на согласование",
				"Генералов И. П."));
		history.add(new HistoryEntry("14:13 19.07.2016",
				"Изменен статус задачи на \"Выполняется\"; Изменен срок задачи на  25.07.2016 23:00 Комментарий: связался с контактным лицом и уточнил требования, к понедельнику подготовлю проект и смету",
				"Иванов И. И."));
		history.add(new HistoryEntry("13:05 19.07.2016",
				"По заявке 0005 Подключение создана задача \"Подготовка проекта\"; Статус: \"Не просмотрена\"; Исполнитель: Иванов И. И. Срок задачи: 20.07.2016 20:00 Комментарий: Виртуальная АТС, требования: городской номер телефона и номер 8-800. Внутренняя телефонная сеть с короткой нумерацией - 20 ТА.",
				"Продавайкин И. А."));
	}

	public Collection<HistoryEntry> getHistory() {
		return history;
	}

	public class HistoryEntry {
		String date;
		String details;
		String autor;

		HistoryEntry(String date, String details, String autor) {
			this.date = date;
			this.details = details;
			this.autor = autor;
		}

		public String getDate() {
			return date;
		}

		public String getDetails() {
			return details;
		}

		public String getAutor() {
			return autor;
		}
	}
}
