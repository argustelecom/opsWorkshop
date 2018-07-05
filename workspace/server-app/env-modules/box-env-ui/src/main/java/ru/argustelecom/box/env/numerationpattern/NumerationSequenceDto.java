package ru.argustelecom.box.env.numerationpattern;

import java.util.Date;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.numerationpattern.model.NumerationSequence;
import ru.argustelecom.box.env.numerationpattern.model.NumerationSequence.PeriodType;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class NumerationSequenceDto extends ConvertibleDto {

	private Long id;
	private String name;
	private Long initialValue;
	private Long increment;
	private PeriodType period;
	private Integer capacity;
	private Date validTo;
	private Long currentValue;
	private boolean canBeDeleted = true;

	@Builder
	public NumerationSequenceDto(Long id, String name, Long initialValue, Long increment, PeriodType period,
								 Integer capacity, Date validTo, Long currentValue, boolean canBeDeleted) {
		this.id = id;
		this.name = name;
		this.initialValue = initialValue;
		this.increment = increment;
		this.period = period;
		this.capacity = capacity;
		this.validTo = validTo;
		this.currentValue = currentValue;
		this.canBeDeleted = canBeDeleted;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return NumerationSequenceDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return NumerationSequence.class;
	}
}
