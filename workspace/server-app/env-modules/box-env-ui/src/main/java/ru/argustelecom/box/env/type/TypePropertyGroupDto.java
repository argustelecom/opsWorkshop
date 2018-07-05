package ru.argustelecom.box.env.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TypePropertyGroupDto {
	private String name;
	private Integer ordinalNumber;
	private int maxOrdinalNumber;
}
