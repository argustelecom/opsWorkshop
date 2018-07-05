package ru.argustelecom.box.env.report;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import ru.argustelecom.box.env.report.api.ReportOutputFormat;
import ru.argustelecom.box.env.report.model.ReportModelTemplate;

public class ReportItem {

	private ReportModelTemplate template;
	private ReportOutputFormat outputFormat;

	public ReportItem(ReportModelTemplate template, ReportOutputFormat outputFormat) {
		this.template = template;
		this.outputFormat = outputFormat;
	}

	public ReportModelTemplate getTemplate() {
		return template;
	}

	public ReportOutputFormat getOutputFormat() {
		return outputFormat;
	}

	@Override
	public String toString() {
		return String.format("ReportItem { templateId=%d, outputFormat=%s }", template.getId(), outputFormat);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getTemplate()).append(getOutputFormat()).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		ReportItem other = (ReportItem) obj;
		return new EqualsBuilder().append(this.getTemplate(), other.getTemplate())
				.append(this.getOutputFormat(), other.getOutputFormat()).isEquals();
	}

}