package ru.argustelecom.box.nri.resources;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.nri.resources.spec.ParameterSpecificationDto;

import java.io.Serializable;

/**
 * Описатель параметра ресурса.
 * Представляет собой значение параметра ресурса
 * @author a.wisniewski
 * @since 02.10.2017
 */
@Getter
@Setter
public class ParamDescriptorDto implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * спецификация параметра
	 */
	private ParameterSpecificationDto paramSpec;

	/**
	 * значение параметра
	 */
	private String paramValue;
}
