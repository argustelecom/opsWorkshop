package ru.argustelecom.box.nri.logicalresources.phone;

import org.jboss.logging.Logger;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import ru.argustelecom.box.nri.logicalresources.phone.lifecycle.PhoneNumberState;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumber;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;

/**
 * Запускает на выполнение job в 23:59:00 по переводу из PhoneNumberState.LOCKED в PhoneNumberState.AVAILABLE
 */
@Singleton
public class LogicalResourceStateWorker {
	private static final Logger log = Logger.getLogger(LogicalResourceStateWorker.class.getName());
	/**
	 * репозиторий для работы с телфонными номерами
	 */
	@Inject
	private PhoneNumberRepository repository;

	/**
	 * собственно сам job
	 */
	@Schedule(second = "00", minute = "59", hour = "23")
	public void changeState() {
		LocalDate currentTime = LocalDate.fromDateFields(new Date());
		List<PhoneNumber> numbers = repository.findAllWithState(PhoneNumberState.LOCKED);

		for (PhoneNumber number : numbers) {
			Date stateChanged = number.getStateChangeDate();
			Days period = Days.daysBetween(LocalDate.fromDateFields(stateChanged), currentTime);

			int blockedInterval = number.getSpecInstance().getType().getBlockedInterval();
			if (blockedInterval == 0 ||	period.getDays() >= blockedInterval) {
				try {
					number.setState(PhoneNumberState.AVAILABLE);
					repository.save(number);
				}catch(Exception ex){
					log.warn("Не смогли сменить статус",ex);
				}
			}
		}
	}
}
