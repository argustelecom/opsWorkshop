package ru.argustelecom.box.env.billing.mediation;

import static ru.argustelecom.box.env.billing.invoice.model.JobDataType.REGULAR;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import ru.argustelecom.box.env.billing.invoice.ChargeJobAppService;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJob;
import ru.argustelecom.system.inf.configuration.ServerRuntimeProperties;

/**
 * Сервис синхронизации данных биллинга и предбиллинга.
 * <p/>
 * Гарантирует за счет аннотации {@linkplain Singleton}, что если работа выполняется дольше интервала срабатывания, то
 * следующие события игнорируются, а не накапливаются.
 * <p/>
 * Конфигурируется с помощью cron-like выражений, задаваемые через настройки
 * {@literal box.billing.mediationsync.timer.minute} и {@literal box.billing.mediationsync.timer.hour}. Запуск
 * производиться на основании значения свойства {@literal box.billing.mediationsync.enabled}.
 * <p/>
 * По умолчанию выполняется каждые 5 минут.
 */
@Singleton
@Startup
public class MediationSyncService {

	private static final Logger log = Logger.getLogger(MediationSyncService.class);

	private static final String MEDIATION_SYNC_ENABLED_PROPERTY = "box.billing.mediationsync.enabled";
	private static final String MEDIATION_SYNC_TIMER_MINUTE_PROPERTY = "box.billing.mediationsync.timer.minute";
	private static final String MEDIATION_SYNC_TIMER_HOUR_PROPERTY = "box.billing.mediationsync.timer.hour";
	private static final String MEDIATION_SYNC_ENABLED_DEFAULT_VALUE = "true";
	private static final String MEDIATION_SYNC_TIMER_MINUTE_DEFAULT_VALUE = "0/5";
	private static final String MEDIATION_SYNC_TIMER_HOUR_DEFAULT_VALUE = "0/1";

	@Resource
	private TimerService service;

	@Inject
	private ChargeJobAppService chargeJobAs;

	@Inject
	private JobSynchronizerService jobSynchronizerSrv;

	@PostConstruct
	private void init() {
		String isEnabled = ServerRuntimeProperties.instance().getProperties()
				.getProperty(MEDIATION_SYNC_ENABLED_PROPERTY, MEDIATION_SYNC_ENABLED_DEFAULT_VALUE);
		if (!Boolean.parseBoolean(isEnabled)) {
			log.info("Значение свойства box.billing.mediationsync.enabled = false, синхронизация данных между "
					+ "предбиллингом и биллингом отключена");
			return;
		}

		String minute = ServerRuntimeProperties.instance().getProperties()
				.getProperty(MEDIATION_SYNC_TIMER_MINUTE_PROPERTY, MEDIATION_SYNC_TIMER_MINUTE_DEFAULT_VALUE);
		String hour = ServerRuntimeProperties.instance().getProperties().getProperty(MEDIATION_SYNC_TIMER_HOUR_PROPERTY,
				MEDIATION_SYNC_TIMER_HOUR_DEFAULT_VALUE);

		log.infof("Настройки синхронизации данных биллинга и предбиллинга: minute(%s), hour(%s)", minute, hour);

		ScheduleExpression exp = new ScheduleExpression();
		exp.hour(hour).minute(minute);

		TimerConfig cfg = new TimerConfig();
		cfg.setPersistent(false);

		service.createCalendarTimer(exp, cfg);
	}

	@Timeout
	@SuppressWarnings("unused")
	public void synchronize(Timer timer) {
		ChargeJob job;
		do {
			job = chargeJobAs.findMostEarlyForSynchronization();
			jobSynchronizerSrv.synchronize(job);
		} while (job != null && job.getDataType() == REGULAR);
	}
}
