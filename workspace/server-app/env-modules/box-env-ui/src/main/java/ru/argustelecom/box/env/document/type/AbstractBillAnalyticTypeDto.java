package ru.argustelecom.box.env.document.type;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.dto.ConvertibleDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = { "id" }, callSuper = false)
public abstract class AbstractBillAnalyticTypeDto extends ConvertibleDto {

	private Long id;
	private String name;
	private String description;
	private boolean availableForCustomPeriod;

}