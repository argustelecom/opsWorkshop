package ru.argustelecom.box.env.commodity;

import java.util.Date;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.MappedSuperclass;
import javax.persistence.SqlResultSetMapping;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@MappedSuperclass
//@formatter:off
	@SqlResultSetMapping(
		name = RatedOutgoingCallsRechargeContext.RATED_OUTGOING_CALLS_DATA_MAPPER,
		classes = {
			@ConstructorResult(
				targetClass = RatedOutgoingCallsRechargeContext.class,
				columns = {
					@ColumnResult(name="service_id", type = Long.class),
					@ColumnResult(name="min_call_date", type = Date.class),
					@ColumnResult(name="max_call_date", type = Date.class)
				}
			)
		})
	//@formatter:on
@AllArgsConstructor
@Getter
public class RatedOutgoingCallsRechargeContext {

	static final String RATED_OUTGOING_CALLS_DATA_MAPPER = "RatedOutgoingCallsRechargeContextDataMapper";

	private Long serviceId;
	private Date minCallDate;
	private Date maxCallDate;

}