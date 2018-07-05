package ru.argustelecom.box.env.personnel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.login.model.Login;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@Setter
@AllArgsConstructor
public class LoginListDto extends ConvertibleDto {
	private Long id;
	private String login;
	private String email;

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return LoginListDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return Login.class;
	}
}
