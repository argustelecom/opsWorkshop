package ru.argustelecom.box.env.type;

import ru.argustelecom.box.env.commodity.model.CommodityType;
import ru.argustelecom.box.env.type.model.Type;
import ru.argustelecom.system.inf.page.CurrentEntity;
import ru.argustelecom.system.inf.page.PresentationState;

@PresentationState
public class CurrentType extends CurrentEntity<Type> {

	private static final long serialVersionUID = -8571564980074911930L;

	public void newTypeCreated(CommodityType newCommodityType) {
		NewTypeCreatedEvent event = new NewTypeCreatedEvent(newCommodityType);
		fire(event);
	}

	public String getCreateNewCommodityTypeEvent() {
		return getEventName(NewTypeCreatedEvent.class);
	}

	public class NewTypeCreatedEvent {
		private CommodityType newCommodityType;

		private NewTypeCreatedEvent(CommodityType newCommodityType) {
			this.newCommodityType = newCommodityType;
		}

		public CommodityType getNewCommodityType() {
			return newCommodityType;
		}
	}

}