package ru.argustelecom.ops.workshop.dbmanagement;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author t.vildanov
 */
@Embeddable
public class Status implements Serializable {
	static final String VALUE_ATTR_NAME = "state";
	static final String CHECK_ATTR_NAME = "lastCheck";

	private Boolean state;
	private Long lastCheck;
	private Boolean updating;
	private StatusUpdater updater;

	public Boolean getState() {
		synchronized (state) {
			return state;
		}
	}

	public Boolean isUpdating() {
		synchronized (updating) {
			return updating;
		}
	}

	public Long getLastCheck() {
		synchronized (lastCheck) {
			return lastCheck;
		}
	}

	void setState(Boolean value) {
		synchronized (state) {
			state = value;
		}
	}

	void setUpdater(StatusUpdater updater) {
		this.updater = updater;
	}

	void setLastCheck(Long lastCheck) {
		synchronized (this.lastCheck) {
			this.lastCheck = lastCheck;
		}
	}

	void updateStatus() {
		if (updater == null)
			return;

		synchronized (updating) {
			if (updating)
				return;
			updating = true;
		}
		try {
			updater.update(this);
		}
		finally {
			synchronized (updating) {
				updating = false;
			}
		}
	}
}


@FunctionalInterface
interface StatusUpdater {
	void update(Status status);
}