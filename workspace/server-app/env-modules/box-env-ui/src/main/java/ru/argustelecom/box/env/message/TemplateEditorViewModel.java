package ru.argustelecom.box.env.message;

import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import ru.argustelecom.box.env.message.model.MessageTemplate;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;
import ru.argustelecom.system.inf.transaction.UnitOfWork;

@PresentationModel
public class TemplateEditorViewModel extends ViewModel {

	private static final long serialVersionUID = 8693211408393234717L;

	@Inject
	private MessageService mailService;

	@Inject
	private UnitOfWork unitOfWork;

	private List<MessageTemplate> templates = new ArrayList<>();

	private MessageTemplate selectedTemplate;

	private String templateInMarkdown;

	private String templateInHtml;

	@PostConstruct
	public void postConstruct() {
		templates = mailService.getAllTemplates();
		unitOfWork.makePermaLong();
	}

	public void showInHtml() {
		if (StringUtils.isBlank(templateInMarkdown)) {
			templateInHtml = null;
		}
		else {
			Parser markdownParser = Parser.builder().build();
			HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();
			templateInHtml = htmlRenderer.render(markdownParser.parse(templateInMarkdown));
		}
	}

	public void saveTemplate() throws Exception {
		checkState(selectedTemplate != null);

		selectedTemplate.setContent(templateInMarkdown);
	}

	public List<MessageTemplate> getTemplates() {
		return templates;
	}

	public MessageTemplate getSelectedTemplate() {
		return selectedTemplate;
	}
	public void setSelectedTemplate(MessageTemplate selectedTemplate) {
		this.selectedTemplate = selectedTemplate;
		templateInMarkdown = selectedTemplate != null ? selectedTemplate.getContent() : null;
		templateInHtml = null;
	}

	public String getTemplateInMarkdown() {
		return templateInMarkdown;
	}
	public void setTemplateInMarkdown(String templateInMarkdown) {
		this.templateInMarkdown = templateInMarkdown;
	}

	public String getTemplateInHtml() {
		return templateInHtml;
	}

}
