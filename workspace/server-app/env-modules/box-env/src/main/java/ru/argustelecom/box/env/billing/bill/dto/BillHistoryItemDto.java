package ru.argustelecom.box.env.billing.bill.dto;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;
import ru.argustelecom.box.env.billing.bill.model.BillHistoryItem;
import ru.argustelecom.box.env.dto2.ConvertibleDto;
import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
public class BillHistoryItemDto extends ConvertibleDto {

	private Long id;
	private Date changedDate;
	private String number;
	private String employeeName;
	private Long version;

	@Builder
	public BillHistoryItemDto(Long id, Date changedDate, String number, String employeeName, Long version) {
		this.id = id;
		this.changedDate = changedDate;
		this.number = number;
		this.employeeName = employeeName;
		this.version = version;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return BillHistoryDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return BillHistoryItem.class;
	}

}