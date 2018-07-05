package ru.argustelecom.box.env.billing.subscription;

import static java.lang.String.format;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.system.inf.modelbase.NamedObject;

@Getter
@Setter
public class SubjectCauseDto implements NamedObject, Serializable {

	private Long id;
	private Long causeId;
	private String name;
	private String fullName;
	private SubjectCauseType type;
	private String note;

	@Builder
	public SubjectCauseDto(Long id, Long causeId, String name, String fullName, SubjectCauseType type, String note) {
		this.id = id;
		this.causeId = causeId;
		this.name = name;
		this.fullName = fullName;
		this.type = type;
		this.note = note;
	}

	@Override
	public String getObjectName() {
		return name;
	}

	@Override
	public String toString() {
		return format("SubjectCauseDto {id=%s, causeId=%d, type=%s}", getId() != null ? getId() : StringUtils.EMPTY,
				getCauseId(), getType());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getId() != null ? getId() : getCauseId()).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		SubjectCauseDto other = (SubjectCauseDto) obj;

		EqualsBuilder equalsBuilder = getId() != null ? new EqualsBuilder().append(this.getId(), other.getId())
				: new EqualsBuilder().append(this.getCauseId(), other.getCauseId());

		return equalsBuilder.isEquals();
	}

	private static final long serialVersionUID = -2243452459868955273L;

}