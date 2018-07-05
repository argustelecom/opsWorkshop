package ru.argustelecom.box.env.message.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.modelbase.SuperClass;

@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "message_template")
public class MessageTemplate extends SuperClass {

	private static final long serialVersionUID = 7591355173837476100L;

	public static final long PA_ACCOUNT_DATA_TEMPLATE_ID = 1;
	public static final long SALDO_EXPORT_TEMPLATE_ID = 2;
	public static final long SALDO_EXPORT_ERROR_TEMPLATE_ID = 3;

	private String name;

	private String content;
	
	@Id
	@Override
	@Access(AccessType.PROPERTY)
	public Long getId() {
		return super.getId();
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	public static class MessageTemplateQuery extends EntityQuery<MessageTemplate> {

		public MessageTemplateQuery() {
			super(MessageTemplate.class);
		}
	}
}
