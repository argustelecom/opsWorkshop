package ru.argustelecom.box.env.contract;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.customer.CustomerTypeDto;
import ru.argustelecom.box.env.document.type.DocumentTypeDto;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public abstract class AbstractContractTypeDto extends DocumentTypeDto {

	private CustomerTypeDto customerTypeDto;
	private String description;

}