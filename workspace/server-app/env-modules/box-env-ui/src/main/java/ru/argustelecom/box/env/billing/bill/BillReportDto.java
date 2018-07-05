package ru.argustelecom.box.env.billing.bill;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.billing.bill.model.Bill;
import ru.argustelecom.box.env.dto.IdentifiableDto;
import ru.argustelecom.system.inf.chrono.DateUtils;
import ru.argustelecom.system.inf.chrono.TZ;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
public class BillReportDto implements IdentifiableDto {
	private Long id;
	private String billNumber;
	private Date billDate;
	private String customerName;
	@Setter
	private String error = StringUtils.EMPTY;

	@Builder
	public BillReportDto(Long id, String billNumber, Date billDate, String customerName) {
		this.id = id;
		this.billNumber = billNumber;
		this.billDate = billDate;
		this.customerName = customerName;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return Bill.class;
	}

	@Override
	public String toString() {
		return String.format("%s %s %s   %s", billNumber,
				DateUtils.format(billDate, DateUtils.DATE_DEFAULT_PATTERN, TZ.getServerTimeZone()), customerName,
				error);
	}
}
