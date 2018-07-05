package ru.argustelecom.box.env.numerationpattern;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class NumerationPatternDto {
	@Setter
	private Long id;
	private String className;
	@Setter
	private String pattern;

	public NumerationPatternDto(String className) {
		this.className = className;
	}

	@Builder
	public NumerationPatternDto(Long id, String className, String pattern) {
		this.id = id;
		this.className = className;
		this.pattern = pattern;
	}
}
