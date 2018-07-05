package ru.argustelecom.box.env.lifecycle;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LifecycleHistoryItemDto {

	private Date transitionTime;
	private String fromState;
	private String toState;
	private String initiatorName;

	@Builder
	public LifecycleHistoryItemDto(Date transitionTime, String fromState, String toState, String initiatorName) {
		this.transitionTime = transitionTime;
		this.fromState = fromState;
		this.toState = toState;
		this.initiatorName = initiatorName;
	}

}