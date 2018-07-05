package ru.argustelecom.box.env.billing.account;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = { "id" }, callSuper = false)
public class PersonalAccountDto extends ConvertibleDto {

	private Long id;
	private String number;
	private Money availableBalance;
	private List<SubscriptionDto> subscriptions;

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return PersonalAccountDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return PersonalAccount.class;
	}
}
