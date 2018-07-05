package ru.argustelecom.box.env.test;

import java.io.Serializable;

import ru.argustelecom.system.inf.page.PresentationModel;

import javax.inject.Named;

@PresentationModel
@Named(value = "orderAttributesFrame")
public class OrderAttributesFrameModel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private OrderStatus status = OrderStatus.NEW;

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public enum OrderStatus {
		NEW ("Новая"),
		IN_THE_PIPELINE ("В работе"),
		POSTPONED ("Отложена");
		
		String name;
		
		OrderStatus(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	}
}
