package ru.argustelecom.box.env.personnel;

import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.login.model.Login;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class LoginListDtoTranslator implements DefaultDtoTranslator<LoginListDto, Login> {
	@Override
	public LoginListDto translate(Login login) {
		return login != null ? new LoginListDto(login.getLoginId(), login.getLoginName(), login.getEmail()) : null;
	}
}
