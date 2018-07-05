package ru.argustelecom.box.env.message;

import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import ru.argustelecom.box.env.message.model.MessageTemplate;
import ru.argustelecom.box.env.message.model.MessageTemplate.MessageTemplateQuery;
import ru.argustelecom.box.env.message.nls.MessageMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.BusinessException;

@Stateless
public class MessageService implements Serializable {

	private static final long serialVersionUID = 2340496053324677617L;

	@PersistenceContext
	private EntityManager em;

	public String createMessage(Long messageTemplateId, Map<String, Object> dataModel) {
		Configuration cfg = new Configuration();
		cfg.setDefaultEncoding("UTF-8");
		String message = null;
		MessageTemplate messageTemplate = em.find(MessageTemplate.class, messageTemplateId);
		StringTemplateLoader templateLoader = new StringTemplateLoader();
		templateLoader.putTemplate(messageTemplate.getName(), messageTemplate.getContent());
		cfg.setTemplateLoader(templateLoader);

		try (Writer writer = new StringWriter()){
			Template freeMarkerTemplate = cfg.getTemplate(messageTemplate.getName());
			freeMarkerTemplate.process(dataModel, writer);
			Parser markdownParser = Parser.builder().build();
			HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();
			message = htmlRenderer.render(markdownParser.parse(writer.toString()));
		} catch (Exception e) {
			throw new BusinessException(LocaleUtils.getMessages(MessageMessagesBundle.class).createMessageError(), e);
		}

		return message;
	}

	public List<MessageTemplate> getAllTemplates() {
		return new MessageTemplateQuery().createTypedQuery(em).getResultList();
	}

}
