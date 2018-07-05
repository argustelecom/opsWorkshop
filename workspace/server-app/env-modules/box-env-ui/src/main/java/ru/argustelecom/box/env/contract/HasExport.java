package ru.argustelecom.box.env.contract;

import java.io.InputStream;
import java.util.function.Function;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.report.ReportItem;
import ru.argustelecom.system.inf.Notification;

public interface HasExport {
	default Function<ReportItem, StreamedContent> initExportFnc(
			ContractCardGenerationAppService contractCardGenerationAs, AbstractContract context, String errorMsg) {
		return report -> {
			if (report != null) {
				try {
					InputStream cardIs = contractCardGenerationAs.generate(context, report.getTemplate(),
							report.getOutputFormat());
					return (StreamedContent) new DefaultStreamedContent(
							cardIs,
							report.getTemplate().getMimeType(),
							String.format("%s %s.%s", context.getType().getName(),
							context.getDocumentNumber(), report.getOutputFormat().name().toLowerCase()));
				} catch (ContractGenerationException ex) {
					Notification.error(errorMsg, ex.getMessage());				}
			}

			return null;
		};
	};
}
