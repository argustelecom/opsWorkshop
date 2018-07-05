package ru.argustelecom.box.env.document.model;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import ru.argustelecom.box.env.report.model.HasTemplates;
import ru.argustelecom.box.env.report.model.ReportModelTemplate;
import ru.argustelecom.box.env.type.model.Type;

@MappedSuperclass
@Access(AccessType.FIELD)
public abstract class DocumentType extends Type implements HasTemplates {

	private static final long serialVersionUID = 4957291878005577511L;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(schema = "system", name = "document_type_templates", joinColumns = @JoinColumn(name = "document_type_id"), inverseJoinColumns = @JoinColumn(name = "document_template_id"))
	@OrderBy("creationDate desc")
	private List<ReportModelTemplate> templates = new ArrayList<>();

	protected DocumentType() {
	}

	protected DocumentType(Long id) {
		super(id);
	}

	public List<ReportModelTemplate> getTemplates() {
		return unmodifiableList(templates);
	}

	public boolean addTemplate(ReportModelTemplate template) {
		if (!templates.contains(template)) {
			templates.add(0, template);
			return true;
		}
		return false;
	}

	public boolean removeTemplate(ReportModelTemplate template) {
		return templates.remove(template);
	}
}
