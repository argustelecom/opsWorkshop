package ru.argustelecom.box.env.billing.subscription;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <b>Не использовать</b>. От создания подписок на основании заявки пока что отказались: BOX-1290. Специально сделан
 * Deprecated.
 */
@Deprecated
@Getter
@Setter
@NoArgsConstructor
public class SubscriptionCreationByOrderDto extends SubscriptionCreationDto {

	private BigDecimal sumOfPromisedPayment;

}