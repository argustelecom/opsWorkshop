package ru.argustelecom.box.inf.queue.impl.context;

import org.junit.Assert;
import org.junit.Test;

import ru.argustelecom.box.inf.queue.api.context.Context;
import ru.argustelecom.box.inf.queue.api.context.EntityReference;
import ru.argustelecom.box.inf.queue.api.model.QueueEvent;
import ru.argustelecom.system.inf.modelbase.Identifiable;

public class ContextMapperTest {

	private static final String MARSHALLED_BEAN = "{\"stringProp\":\"Test\",\"longProp\":42,\"integerProp\":42,\"booleanProp\":true,\"doubleProp\":3.14,\"entityProp\":\"Entity-42\"}";

	@Test
	public void shouldMarshallContextBean() {
		ContextBean bean = new ContextBean();
		bean.setStringProp("Test");
		bean.setLongProp(42L);
		bean.setIntegerProp(42);
		bean.setBooleanProp(true);
		bean.setDoubleProp(3.14D);
		bean.setEntityProp(new EntityReference<>(new Entity(42L)));

		String marshalled = ContextMapper.marshall(bean);
		Assert.assertEquals(MARSHALLED_BEAN, marshalled);
	}

	@Test
	public void shouldUpdateContextBean() {
		ContextBean bean = new ContextBean();
		ContextMapper.update(bean, MARSHALLED_BEAN);

		Assert.assertEquals("Test", bean.getStringProp());
		Assert.assertEquals(Long.valueOf(42L), bean.getLongProp());
		Assert.assertEquals(Integer.valueOf(42), bean.getIntegerProp());
		Assert.assertEquals(Boolean.valueOf(true), bean.getBooleanProp());
		Assert.assertEquals(Double.valueOf(3.14D), bean.getDoubleProp());

		Assert.assertNotNull(bean.getEntityProp());
		Assert.assertEquals("Entity-42", bean.getEntityProp().identity());
	}

	class Entity implements Identifiable {
		private Long id;

		public Entity(Long id) {
			this.id = id;
		}

		@Override
		public Long getId() {
			return id;
		}
	}

	class ContextBean extends Context {

		private String stringProp;
		private Long longProp;
		private Integer integerProp;
		private Boolean booleanProp;
		private Double doubleProp;
		private EntityReference<Entity> entityProp;

		public ContextBean() {
			super();
		}

		protected ContextBean(QueueEvent event) {
			super(event);
		}

		public String getStringProp() {
			return stringProp;
		}

		public void setStringProp(String stringProp) {
			this.stringProp = stringProp;
		}

		public Long getLongProp() {
			return longProp;
		}

		public void setLongProp(Long longProp) {
			this.longProp = longProp;
		}

		public Integer getIntegerProp() {
			return integerProp;
		}

		public void setIntegerProp(Integer integerProp) {
			this.integerProp = integerProp;
		}

		public Boolean getBooleanProp() {
			return booleanProp;
		}

		public void setBooleanProp(Boolean booleanProp) {
			this.booleanProp = booleanProp;
		}

		public Double getDoubleProp() {
			return doubleProp;
		}

		public void setDoubleProp(Double doubleProp) {
			this.doubleProp = doubleProp;
		}

		public EntityReference<Entity> getEntityProp() {
			return entityProp;
		}

		public void setEntityProp(EntityReference<Entity> entityProp) {
			this.entityProp = entityProp;
		}

		public String getNotJsonProperty() {
			return "NotJsonProperty";
		}

		private static final long serialVersionUID = 8612617286391444379L;
	}

}
