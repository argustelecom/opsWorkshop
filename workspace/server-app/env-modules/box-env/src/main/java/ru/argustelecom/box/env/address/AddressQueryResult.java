package ru.argustelecom.box.env.address;

import static java.lang.String.format;

import java.io.Serializable;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.MappedSuperclass;
import javax.persistence.SqlResultSetMapping;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@MappedSuperclass
@EqualsAndHashCode(of = { "id" })
//@formatter:off
@SqlResultSetMapping(
		name = AddressQueryResult.ADDRESS_QUERY_RESULT_MAPPER,
		classes = {
				@ConstructorResult(
						targetClass = AddressQueryResult.class,
						columns = {
								@ColumnResult(name="location_id", type = Long.class),
								@ColumnResult(name="location_class", type = String.class),
								@ColumnResult(name="tree_display_name", type = String.class)
						}
				)
		})
//@formatter:on
public class AddressQueryResult implements Serializable {

	public static final String ADDRESS_QUERY_RESULT_MAPPER = "AddressQueryResultMapper";

	public AddressQueryResult(Long id, String locationClass, String displayName) {
		this.id = id;
		this.locationClass = LocationClass.valueOf(locationClass);
		this.displayName = displayName;
	}

	private Long id;
	private LocationClass locationClass;
	private String displayName;

	@Override
	public String toString() {
		return format("%s [id=%d, displayName=%s]", locationClass.name(), id, displayName);
	}

	private static final long serialVersionUID = -5759193065694204392L;
}