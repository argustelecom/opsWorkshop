package ru.argustelecom.box.env.report;

import static ru.argustelecom.box.env.dto.DefaultDtoConverterUtils.translate;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.initializeAndUnproxy;
import static ru.argustelecom.system.inf.utils.CDIHelper.lookupCDIBean;

import java.util.List;

import javax.persistence.EntityManager;

import lombok.Getter;
import ru.argustelecom.box.env.document.type.ReportModelTemplateDto;
import ru.argustelecom.box.env.document.type.ReportModelTemplateDtoTranslator;
import ru.argustelecom.box.env.dto.IdentifiableDto;
import ru.argustelecom.box.env.report.model.HasTemplates;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
public class TemplateHolderDto<T extends HasTemplates & Identifiable> implements IdentifiableDto {
	private Long id;
	private Class<T> clazz;
	private List<ReportModelTemplateDto> templates;

	@SuppressWarnings("unchecked")
	public TemplateHolderDto(T entity) {
		this.id = entity.getId();
		this.clazz = (Class<T>) initializeAndUnproxy(entity).getClass();
		this.templates = translate(lookupCDIBean(ReportModelTemplateDtoTranslator.class), entity.getTemplates());
	}

	public HasTemplates getHasTemplates(EntityManager em) {
		return (HasTemplates) getIdentifiable(em);
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return clazz;
	}
}
