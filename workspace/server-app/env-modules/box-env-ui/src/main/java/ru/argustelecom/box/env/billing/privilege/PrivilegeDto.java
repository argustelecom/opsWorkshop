package ru.argustelecom.box.env.billing.privilege;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Хитрое <b>dto</b> для ФБ "Привилегий", в котором объединяется работа с
 * {@linkplain ru.argustelecom.box.env.privilege.model.Privilege привилегиями(доверительный/пробный период)} и
 * {@linkplain ru.argustelecom.box.env.privilege.discount.model.Discount скидками}
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class PrivilegeDto {

	private Long id;
	private PrivilegeTypeRef type;
	private Date validFrom;
	private Date validTo;
	private String objectName;
	private BigDecimal rateOfDiscount;
	private PrivilegeSubjectDto subject;

	@Builder
	public PrivilegeDto(Long id, PrivilegeTypeRef type, Date validFrom, Date validTo, String objectName,
			BigDecimal rateOfDiscount, PrivilegeSubjectDto subject) {
		this.id = id;
		this.type = type;
		this.validFrom = validFrom;
		this.validTo = validTo;
		this.objectName = objectName;
		this.rateOfDiscount = rateOfDiscount;
		this.subject = subject;
	}

}