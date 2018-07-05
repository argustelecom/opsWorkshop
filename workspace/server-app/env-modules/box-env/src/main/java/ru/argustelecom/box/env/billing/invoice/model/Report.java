package ru.argustelecom.box.env.billing.invoice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Report {
	private String result;
	private Integer totalProcessed;
	private Integer success;
	private Map<String, Integer> unsuitables;
}
