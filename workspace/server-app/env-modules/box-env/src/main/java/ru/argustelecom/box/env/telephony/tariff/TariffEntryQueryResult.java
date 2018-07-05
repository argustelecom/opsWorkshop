package ru.argustelecom.box.env.telephony.tariff;

import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.MappedSuperclass;
import javax.persistence.SqlResultSetMapping;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.inf.hibernate.types.IntArrayType;

@Getter
@MappedSuperclass
@EqualsAndHashCode(of = { "id" }, callSuper = false)
//@formatter:off
@SqlResultSetMapping(
		name = TariffEntryQueryResult.TARIFF_ENTRY_QUERY_RESULT_MAPPER,
		classes = {
				@ConstructorResult(
						targetClass = TariffEntryQueryResult.class,
						columns = {
								@ColumnResult(name="id", type = Long.class),
								@ColumnResult(name="tariff_name", type = String.class),
								@ColumnResult(name="tariff_id", type = Long.class),
								@ColumnResult(name="name", type = String.class),
								@ColumnResult(name="prefix", type = IntArrayType.class),
								@ColumnResult(name="charge_per_unit", type = BigDecimal.class),
								@ColumnResult(name="zone_id", type = Long.class),
								@ColumnResult(name="zone_name", type = String.class)
						}
				)
		})
//@formatter:on
public class TariffEntryQueryResult extends TariffEntryBaseResult {

	public static final String TARIFF_ENTRY_QUERY_RESULT_MAPPER = "TariffEntryQueryResultMapper";

	private Long id;
	private String tariffName;
	private Long tariffId;
	private Long zoneId;

	public TariffEntryQueryResult(Long id, String tariffName, Long tariffId, String name, List<Integer> prefixes,
			BigDecimal chargePerUnit, Long zoneId, String zoneName) {
		super(checkNotNull(name), checkNotNull(prefixes), new Money(checkNotNull(chargePerUnit)),
				checkNotNull(zoneName));
		this.id = id;
		this.tariffName = tariffName;
		this.tariffId = tariffId;
		this.zoneId = zoneId;
	}
}
