package ru.argustelecom.box.env.stl.period.config;

import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.toLocalDateTime;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import ru.argustelecom.box.env.stl.period.PeriodConfig;
import ru.argustelecom.system.inf.chrono.TZ;

public abstract class AbstractPeriodConfig<C extends AbstractPeriodConfig<C>> implements PeriodConfig {

	private LocalDateTime poiLocal;
	private Date poiTemporal;
	private ZoneId zoneId;

	public AbstractPeriodConfig() {
		zoneId = TZ.getServerZoneId();
	}

	public ZoneId getZoneId() {
		return zoneId;
	}

	@SuppressWarnings("unchecked")
	public C setZoneId(ZoneId zoneId) {
		this.zoneId = zoneId;
		return (C) this;
	}

	@Override
	public LocalDateTime getPoi() {
		checkState(poiLocal != null || poiTemporal != null && zoneId != null);
		return poiLocal != null ? poiLocal : toLocalDateTime(poiTemporal, zoneId);
	}

	@SuppressWarnings("unchecked")
	public C setPoi(Date poi) {
		this.poiLocal = null;
		this.poiTemporal = poi;
		return (C) this;
	}

	@SuppressWarnings("unchecked")
	public C setPoi(LocalDateTime poi) {
		this.poiLocal = poi;
		this.poiTemporal = null;
		return (C) this;
	}
}
